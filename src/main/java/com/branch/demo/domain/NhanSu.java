package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nhan_su")
public class NhanSu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "chuc_vu", length = 255)
    private String chucVu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_nganh_id")
    private BanNganh banNganh;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diem_nhom_id")
    private DiemNhom diemNhom;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    
    @Column(name = "dia_chi", length = 500)
    private String diaChi;
    
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;
    
    @Column(name = "ngay_bat_dau_phuc_vu")
    private LocalDate ngayBatDauPhucVu;
    
    @Column(name = "mo_ta_cong_viec", length = 1000)
    private String moTaCongViec;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @ElementCollection
    @CollectionTable(name = "nhan_su_trach_nhiem", joinColumns = @JoinColumn(name = "nhan_su_id"))
    @Column(name = "trach_nhiem", length = 255)
    private List<String> danhSachTrachNhiem = new ArrayList<>();
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiNhanSu trangThai = TrangThaiNhanSu.HOAT_DONG;
    
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
    public NhanSu() {}
    
    public NhanSu(String hoTen, String chucVu) {
        this.hoTen = hoTen;
        this.chucVu = chucVu;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    
    public BanNganh getBanNganh() { return banNganh; }
    public void setBanNganh(BanNganh banNganh) { this.banNganh = banNganh; }
    
    public DiemNhom getDiemNhom() { return diemNhom; }
    public void setDiemNhom(DiemNhom diemNhom) { this.diemNhom = diemNhom; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public LocalDate getNgayBatDauPhucVu() { return ngayBatDauPhucVu; }
    public void setNgayBatDauPhucVu(LocalDate ngayBatDauPhucVu) { this.ngayBatDauPhucVu = ngayBatDauPhucVu; }
    
    public String getMoTaCongViec() { return moTaCongViec; }
    public void setMoTaCongViec(String moTaCongViec) { this.moTaCongViec = moTaCongViec; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public List<String> getDanhSachTrachNhiem() { return danhSachTrachNhiem; }
    public void setDanhSachTrachNhiem(List<String> danhSachTrachNhiem) { this.danhSachTrachNhiem = danhSachTrachNhiem; }
    
    public TrangThaiNhanSu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNhanSu trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void addTrachNhiem(String trachNhiem) {
        if (!danhSachTrachNhiem.contains(trachNhiem)) {
            danhSachTrachNhiem.add(trachNhiem);
        }
    }
    
    public void removeTrachNhiem(String trachNhiem) {
        danhSachTrachNhiem.remove(trachNhiem);
    }
    
    // Enums
    public enum TrangThaiNhanSu {
        HOAT_DONG, DANG_PHUC_VU, TAM_NGHI, NGHI_VIEC, CHUYEN_BAN
    }
}