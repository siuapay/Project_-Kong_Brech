package com.branch.demo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tai_chinh")
public class TaiChinh {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loai_giao_dich", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoaiGiaoDich loaiGiaoDich;
    
    @Column(name = "so_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal soTien;
    
    @Column(name = "mo_ta", length = 1000)
    private String moTa;
    
    @Column(name = "ngay_giao_dich")
    private LocalDate ngayGiaoDich;
    
    @Column(name = "danh_muc", length = 255)
    private String danhMuc;
    
    @Column(name = "nguoi_thuc_hien", length = 255)
    private String nguoiThucHien;
    
    @Column(name = "phuong_thuc_thanh_toan")
    @Enumerated(EnumType.STRING)
    private PhuongThucThanhToan phuongThucThanhToan;
    
    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;
    
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiGiaoDich trangThai = TrangThaiGiaoDich.HOAN_THANH;
    
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
    public TaiChinh() {}
    
    public TaiChinh(LoaiGiaoDich loaiGiaoDich, BigDecimal soTien, String moTa) {
        this.loaiGiaoDich = loaiGiaoDich;
        this.soTien = soTien;
        this.moTa = moTa;
        this.ngayGiaoDich = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LoaiGiaoDich getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public LocalDate getNgayGiaoDich() { return ngayGiaoDich; }
    public void setNgayGiaoDich(LocalDate ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }
    
    public String getDanhMuc() { return danhMuc; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }
    
    public String getNguoiThucHien() { return nguoiThucHien; }
    public void setNguoiThucHien(String nguoiThucHien) { this.nguoiThucHien = nguoiThucHien; }
    
    public PhuongThucThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
    
    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public TrangThaiGiaoDich getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiGiaoDich trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isThu() {
        return loaiGiaoDich == LoaiGiaoDich.THU;
    }
    
    public boolean isChi() {
        return loaiGiaoDich == LoaiGiaoDich.CHI;
    }
    
    public boolean isDangHien() {
        return loaiGiaoDich == LoaiGiaoDich.THU && 
               (danhMuc != null && danhMuc.toLowerCase().contains("dâng hiến"));
    }
    
    // Enums
    public enum LoaiGiaoDich {
        THU, CHI
    }
    
    public enum PhuongThucThanhToan {
        TIEN_MAT, CHUYEN_KHOAN, THE, KHAC
    }
    
    public enum TrangThaiGiaoDich {
        DANG_CHO, HOAN_THANH, BI_HUY
    }
}