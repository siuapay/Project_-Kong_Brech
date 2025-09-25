package com.branch.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ban_nganh")
public class BanNganh extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_ban", nullable = false, length = 255)
    private String tenBan;

    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "ma_ban", unique = true, length = 50)
    private String maBan;

    @Column(name = "email_lien_he", length = 255)
    private String emailLienHe;

    @Column(name = "dien_thoai_lien_he", length = 20)
    private String dienThoaiLienHe;

    @OneToMany(mappedBy = "banNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NhanSu> danhSachNhanSu = new ArrayList<>();
    
    @OneToMany(mappedBy = "banNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TinHuu> danhSachTinHuu = new ArrayList<>();

    @ManyToMany(mappedBy = "danhSachBanNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DiemNhom> danhSachDiemNhom = new ArrayList<>();

    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiBanNganh trangThai = TrangThaiBanNganh.HOAT_DONG;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public BanNganh() {
    }

    public BanNganh(String tenBan, String maBan) {
        this.tenBan = tenBan;
        this.maBan = maBan;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public List<NhanSu> getDanhSachNhanSu() {
        return danhSachNhanSu;
    }

    public void setDanhSachNhanSu(List<NhanSu> danhSachNhanSu) {
        this.danhSachNhanSu = danhSachNhanSu;
    }
    
    public List<TinHuu> getDanhSachTinHuu() {
        return danhSachTinHuu;
    }

    public void setDanhSachTinHuu(List<TinHuu> danhSachTinHuu) {
        this.danhSachTinHuu = danhSachTinHuu;
    }

    public List<DiemNhom> getDanhSachDiemNhom() {
        return danhSachDiemNhom;
    }

    public void setDanhSachDiemNhom(List<DiemNhom> danhSachDiemNhom) {
        this.danhSachDiemNhom = danhSachDiemNhom;
    }

    public String getEmailLienHe() {
        return emailLienHe;
    }

    public void setEmailLienHe(String emailLienHe) {
        this.emailLienHe = emailLienHe;
    }

    public String getDienThoaiLienHe() {
        return dienThoaiLienHe;
    }

    public void setDienThoaiLienHe(String dienThoaiLienHe) {
        this.dienThoaiLienHe = dienThoaiLienHe;
    }

    public TrangThaiBanNganh getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiBanNganh trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public void addNhanSu(NhanSu nhanSu) {
        danhSachNhanSu.add(nhanSu);
        nhanSu.setBanNganh(this);
    }

    public void removeNhanSu(NhanSu nhanSu) {
        danhSachNhanSu.remove(nhanSu);
        nhanSu.setBanNganh(null);
    }

    public int getSoLuongNhanSu() {
        return danhSachNhanSu.size();
    }
    
    public void addTinHuu(TinHuu tinHuu) {
        danhSachTinHuu.add(tinHuu);
        tinHuu.setBanNganh(this);
    }

    public void removeTinHuu(TinHuu tinHuu) {
        danhSachTinHuu.remove(tinHuu);
        tinHuu.setBanNganh(null);
    }

    public int getSoLuongTinHuu() {
        return danhSachTinHuu.size();
    }
    
    public int getTongSoThanhVien() {
        return getSoLuongNhanSu() + getSoLuongTinHuu();
    }

    public NhanSu getTruongBan() {
        // Tìm theo thứ tự ưu tiên: Mục sư -> Truyền đạo -> Chấp sự -> Thư ký -> Thủ quỹ
        // -> Thành viên
        return danhSachNhanSu.stream()
                .filter(ns -> ns.getChucVu() != null)
                .min((ns1, ns2) -> {
                    NhanSu.ChucVu cv1 = ns1.getChucVu();
                    NhanSu.ChucVu cv2 = ns2.getChucVu();
                    return Integer.compare(getChucVuPriority(cv1), getChucVuPriority(cv2));
                })
                .orElse(null);
    }

    private int getChucVuPriority(NhanSu.ChucVu chucVu) {
        switch (chucVu) {
            case MUC_SU:
                return 1;
            case TRUYEN_DAO:
                return 2;
            case CHAP_SU:
                return 3;
            case THU_KY:
                return 4;
            case THU_QUY:
                return 5;
            case THANH_VIEN:
                return 6;
            default:
                return 999;
        }
    }

    // Enums
    public enum TrangThaiBanNganh {
        HOAT_DONG, TAM_NGHI, GIAI_TAN
    }
}