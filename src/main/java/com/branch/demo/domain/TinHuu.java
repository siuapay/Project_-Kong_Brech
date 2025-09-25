package com.branch.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_huu")
public class TinHuu extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "nam_sinh")
    private Integer namSinh;
    
    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh = "Nam";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhom_id")
    @JsonBackReference
    private Nhom nhom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_nganh_id")
    private BanNganh banNganh;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "dia_chi", length = 500)
    private String diaChi;
    
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "nghe_nghiep", length = 255)
    private String ngheNghiep;
    
    @Column(name = "tinh_trang_hon_nhan", length = 20)
    private String tinhTrangHonNhan;
    
    @Column(name = "ngay_gia_nhap")
    private LocalDate ngayGiaNhap;
    
    @Column(name = "ngay_bao_tin")
    private LocalDate ngayBaoTin;
    
    @Column(name = "ngay_thanh_can")
    private LocalDate ngayThanhCan;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "trang_thai", length = 20)
    private String trangThai = "HOAT_DONG";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
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
    public TinHuu() {}
    
    public TinHuu(String hoTen) {
        this.hoTen = hoTen;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public Integer getNamSinh() { return namSinh; }
    public void setNamSinh(Integer namSinh) { this.namSinh = namSinh; }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public Nhom getNhom() { return nhom; }
    public void setNhom(Nhom nhom) { this.nhom = nhom; }
    
    public BanNganh getBanNganh() { return banNganh; }
    public void setBanNganh(BanNganh banNganh) { this.banNganh = banNganh; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNgheNghiep() { return ngheNghiep; }
    public void setNgheNghiep(String ngheNghiep) { this.ngheNghiep = ngheNghiep; }
    
    public String getTinhTrangHonNhan() { return tinhTrangHonNhan; }
    public void setTinhTrangHonNhan(String tinhTrangHonNhan) { this.tinhTrangHonNhan = tinhTrangHonNhan; }
    
    public LocalDate getNgayGiaNhap() { return ngayGiaNhap; }
    public void setNgayGiaNhap(LocalDate ngayGiaNhap) { this.ngayGiaNhap = ngayGiaNhap; }
    
    public LocalDate getNgayBaoTin() { return ngayBaoTin; }
    public void setNgayBaoTin(LocalDate ngayBaoTin) { this.ngayBaoTin = ngayBaoTin; }
    
    public LocalDate getNgayThanhCan() { return ngayThanhCan; }
    public void setNgayThanhCan(LocalDate ngayThanhCan) { this.ngayThanhCan = ngayThanhCan; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    // Helper methods for soft delete
    public boolean isDeleted() { return deletedAt != null; }
    public void softDelete() { this.deletedAt = LocalDateTime.now(); }
    public void restore() { this.deletedAt = null; }
}