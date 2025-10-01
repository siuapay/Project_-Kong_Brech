package com.branch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]{10,11}$|^$", message = "Số điện thoại phải có 10-11 chữ số")
    private String dienThoaiLienHe;

    @OneToMany(mappedBy = "banNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NhanSu> danhSachNhanSu = new ArrayList<>();

    @OneToMany(mappedBy = "banNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChapSu> danhSachChapSu = new ArrayList<>();

    @OneToMany(mappedBy = "banNganh", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TinHuu> danhSachTinHuu = new ArrayList<>();

    // Trưởng ban - chỉ có 1 (Nhân sự HOẶC Chấp sự)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truong_ban_nhan_su_id")
    private NhanSu truongBanNhanSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truong_ban_chap_su_id")
    private ChapSu truongBanChapSu;

    // Phó ban - có thể có nhiều (Many-to-Many)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ban_nganh_pho_ban_nhan_su",
        joinColumns = @JoinColumn(name = "ban_nganh_id"),
        inverseJoinColumns = @JoinColumn(name = "nhan_su_id")
    )
    private List<NhanSu> danhSachPhoBanNhanSu = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ban_nganh_pho_ban_chap_su",
        joinColumns = @JoinColumn(name = "ban_nganh_id"),
        inverseJoinColumns = @JoinColumn(name = "chap_su_id")
    )
    private List<ChapSu> danhSachPhoBanChapSu = new ArrayList<>();

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

    public List<ChapSu> getDanhSachChapSu() {
        return danhSachChapSu;
    }

    public void setDanhSachChapSu(List<ChapSu> danhSachChapSu) {
        this.danhSachChapSu = danhSachChapSu;
    }

    public List<TinHuu> getDanhSachTinHuu() {
        return danhSachTinHuu;
    }

    public void setDanhSachTinHuu(List<TinHuu> danhSachTinHuu) {
        this.danhSachTinHuu = danhSachTinHuu;
    }

    public NhanSu getTruongBanNhanSu() {
        return truongBanNhanSu;
    }

    public void setTruongBanNhanSu(NhanSu truongBanNhanSu) {
        this.truongBanNhanSu = truongBanNhanSu;
    }

    public ChapSu getTruongBanChapSu() {
        return truongBanChapSu;
    }

    public void setTruongBanChapSu(ChapSu truongBanChapSu) {
        this.truongBanChapSu = truongBanChapSu;
    }

    public List<NhanSu> getDanhSachPhoBanNhanSu() {
        return danhSachPhoBanNhanSu;
    }

    public void setDanhSachPhoBanNhanSu(List<NhanSu> danhSachPhoBanNhanSu) {
        this.danhSachPhoBanNhanSu = danhSachPhoBanNhanSu;
    }

    public List<ChapSu> getDanhSachPhoBanChapSu() {
        return danhSachPhoBanChapSu;
    }

    public void setDanhSachPhoBanChapSu(List<ChapSu> danhSachPhoBanChapSu) {
        this.danhSachPhoBanChapSu = danhSachPhoBanChapSu;
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
        // Clean phone number: remove spaces and non-numeric characters
        if (dienThoaiLienHe != null && !dienThoaiLienHe.trim().isEmpty()) {
            this.dienThoaiLienHe = dienThoaiLienHe.replaceAll("[^0-9]", "");
        } else {
            this.dienThoaiLienHe = null;
        }
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

    public void addChapSu(ChapSu chapSu) {
        danhSachChapSu.add(chapSu);
        chapSu.setBanNganh(this);
    }

    public void removeChapSu(ChapSu chapSu) {
        danhSachChapSu.remove(chapSu);
        chapSu.setBanNganh(null);
    }

    public int getSoLuongChapSu() {
        return danhSachChapSu.size();
    }

    public int getSoLuongTinHuu() {
        return danhSachTinHuu.size();
    }

    public int getTongSoThanhVien() {
        return getSoLuongNhanSu() + getSoLuongChapSu() + getSoLuongTinHuu();
    }

    // Helper methods để lấy thông tin trưởng ban và phó ban
    public String getTenTruongBan() {
        if (truongBanNhanSu != null) {
            return truongBanNhanSu.getHoTen();
        }
        if (truongBanChapSu != null) {
            return truongBanChapSu.getHoTen();
        }
        return null;
    }

    public String getLoaiTruongBan() {
        if (truongBanNhanSu != null) {
            return "Nhân sự";
        }
        if (truongBanChapSu != null) {
            return "Chấp sự";
        }
        return null;
    }

    public List<String> getDanhSachTenPhoBan() {
        List<String> danhSach = new ArrayList<>();
        
        // Thêm phó ban nhân sự
        if (danhSachPhoBanNhanSu != null) {
            for (NhanSu nhanSu : danhSachPhoBanNhanSu) {
                danhSach.add(nhanSu.getHoTen() + " (Nhân sự)");
            }
        }
        
        // Thêm phó ban chấp sự
        if (danhSachPhoBanChapSu != null) {
            for (ChapSu chapSu : danhSachPhoBanChapSu) {
                danhSach.add(chapSu.getHoTen() + " (Chấp sự)");
            }
        }
        
        return danhSach;
    }

    public boolean coTruongBan() {
        return truongBanNhanSu != null || truongBanChapSu != null;
    }

    public boolean coPhoBan() {
        return (danhSachPhoBanNhanSu != null && !danhSachPhoBanNhanSu.isEmpty()) || 
               (danhSachPhoBanChapSu != null && !danhSachPhoBanChapSu.isEmpty());
    }

    public int getSoLuongPhoBan() {
        int count = 0;
        if (danhSachPhoBanNhanSu != null) count += danhSachPhoBanNhanSu.size();
        if (danhSachPhoBanChapSu != null) count += danhSachPhoBanChapSu.size();
        return count;
    }

    // Helper methods để quản lý phó ban
    public void addPhoBanNhanSu(NhanSu nhanSu) {
        if (danhSachPhoBanNhanSu == null) {
            danhSachPhoBanNhanSu = new ArrayList<>();
        }
        if (!danhSachPhoBanNhanSu.contains(nhanSu)) {
            danhSachPhoBanNhanSu.add(nhanSu);
        }
    }

    public void removePhoBanNhanSu(NhanSu nhanSu) {
        if (danhSachPhoBanNhanSu != null) {
            danhSachPhoBanNhanSu.remove(nhanSu);
        }
    }

    public void addPhoBanChapSu(ChapSu chapSu) {
        if (danhSachPhoBanChapSu == null) {
            danhSachPhoBanChapSu = new ArrayList<>();
        }
        if (!danhSachPhoBanChapSu.contains(chapSu)) {
            danhSachPhoBanChapSu.add(chapSu);
        }
    }

    public void removePhoBanChapSu(ChapSu chapSu) {
        if (danhSachPhoBanChapSu != null) {
            danhSachPhoBanChapSu.remove(chapSu);
        }
    }

    // Enums
    public enum TrangThaiBanNganh {
        HOAT_DONG, TAM_NGHI, GIAI_TAN
    }
}