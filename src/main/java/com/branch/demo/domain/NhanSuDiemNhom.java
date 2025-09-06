package com.branch.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_su_diem_nhom")
public class NhanSuDiemNhom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "chuc_vu", length = 255)
    private String chucVu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diem_nhom_id")
    @JsonBackReference
    private DiemNhom diemNhom;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "ngay_bat_dau_phuc_vu")
    private LocalDate ngayBatDauPhucVu;
    
    @Column(name = "mo_ta_cong_viec", length = 1000)
    private String moTaCongViec;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiNhanSuDiemNhom trangThai = TrangThaiNhanSuDiemNhom.DANG_PHUC_VU;
    
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
    public NhanSuDiemNhom() {}
    
    public NhanSuDiemNhom(String hoTen, String chucVu, DiemNhom diemNhom) {
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.diemNhom = diemNhom;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    
    public DiemNhom getDiemNhom() { return diemNhom; }
    public void setDiemNhom(DiemNhom diemNhom) { this.diemNhom = diemNhom; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getNgayBatDauPhucVu() { return ngayBatDauPhucVu; }
    public void setNgayBatDauPhucVu(LocalDate ngayBatDauPhucVu) { this.ngayBatDauPhucVu = ngayBatDauPhucVu; }
    
    public String getMoTaCongViec() { return moTaCongViec; }
    public void setMoTaCongViec(String moTaCongViec) { this.moTaCongViec = moTaCongViec; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public TrangThaiNhanSuDiemNhom getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNhanSuDiemNhom trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isTruongNhom() {
        return chucVu != null && chucVu.toLowerCase().contains("trưởng");
    }
    
    public boolean isPhoNhom() {
        return chucVu != null && chucVu.toLowerCase().contains("phó");
    }
    
    // Enums
    public enum TrangThaiNhanSuDiemNhom {
        DANG_PHUC_VU, TAM_NGHI, CHUYEN_DIEM, NGHI_VIEC
    }
}