package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chap_su")
public class ChapSu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "chuc_vu", nullable = false, length = 255)
    private String chucVu;
    
    @Column(name = "cap_bac", length = 100)
    private String capBac; // Mục sư, Truyền đạo, Chấp sự
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "nam_sinh")
    private Integer namSinh;
    
    @Column(name = "nhiem_ky_bat_dau")
    private LocalDate nhiemKyBatDau;
    
    @Column(name = "nhiem_ky_ket_thuc")
    private LocalDate nhiemKyKetThuc;
    
    @Column(name = "mo_ta_trach_nhiem", length = 1000)
    private String moTaTrachNhiem;
    
    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "thu_tu_hien_thi")
    private Integer thuTuHienThi = 0;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiChapSu trangThai = TrangThaiChapSu.DANG_NHIEM_VU;
    
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
    public ChapSu() {}
    
    public ChapSu(String hoTen, String chucVu, String capBac) {
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.capBac = capBac;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    
    public String getCapBac() { return capBac; }
    public void setCapBac(String capBac) { this.capBac = capBac; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getNamSinh() { return namSinh; }
    public void setNamSinh(Integer namSinh) { this.namSinh = namSinh; }
    
    public LocalDate getNhiemKyBatDau() { return nhiemKyBatDau; }
    public void setNhiemKyBatDau(LocalDate nhiemKyBatDau) { this.nhiemKyBatDau = nhiemKyBatDau; }
    
    public LocalDate getNhiemKyKetThuc() { return nhiemKyKetThuc; }
    public void setNhiemKyKetThuc(LocalDate nhiemKyKetThuc) { this.nhiemKyKetThuc = nhiemKyKetThuc; }
    
    public String getMoTaTrachNhiem() { return moTaTrachNhiem; }
    public void setMoTaTrachNhiem(String moTaTrachNhiem) { this.moTaTrachNhiem = moTaTrachNhiem; }
    
    public String getTieuSu() { return tieuSu; }
    public void setTieuSu(String tieuSu) { this.tieuSu = tieuSu; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public Integer getThuTuHienThi() { return thuTuHienThi; }
    public void setThuTuHienThi(Integer thuTuHienThi) { this.thuTuHienThi = thuTuHienThi; }
    
    public TrangThaiChapSu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiChapSu trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isChapSuTruong() {
        return chucVu != null && chucVu.toLowerCase().contains("trưởng");
    }
    
    public boolean isMucSu() {
        return capBac != null && capBac.toLowerCase().contains("mục sư");
    }
    
    public boolean isTruyenDao() {
        return capBac != null && capBac.toLowerCase().contains("truyền đạo");
    }
    
    public int getTuoi() {
        if (namSinh != null) {
            return LocalDate.now().getYear() - namSinh;
        }
        return 0;
    }
    
    // Enums
    public enum TrangThaiChapSu {
        DANG_NHIEM_VU, HET_NHIEM_KY, TAM_NGHI, CHUYEN_DI
    }
}