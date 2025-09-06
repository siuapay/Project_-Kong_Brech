package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lien_he")
public class LienHe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "chu_de", length = 255)
    private String chuDe;
    
    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;
    
    @Column(name = "loai_lien_he")
    @Enumerated(EnumType.STRING)
    private LoaiLienHe loaiLienHe;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiLienHe trangThai = TrangThaiLienHe.CHUA_DOC;
    
    @Column(name = "ngay_phan_hoi")
    private LocalDateTime ngayPhanHoi;
    
    @Column(name = "noi_dung_phan_hoi", columnDefinition = "TEXT")
    private String noiDungPhanHoi;
    
    @Column(name = "nguoi_phan_hoi", length = 255)
    private String nguoiPhanHoi;
    
    @Column(name = "dang_ky_nhan_tin")
    private Boolean dangKyNhanTin = false;
    
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
    public LienHe() {}
    
    public LienHe(String hoTen, String email, String chuDe, String noiDung) {
        this.hoTen = hoTen;
        this.email = email;
        this.chuDe = chuDe;
        this.noiDung = noiDung;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getChuDe() { return chuDe; }
    public void setChuDe(String chuDe) { this.chuDe = chuDe; }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public LoaiLienHe getLoaiLienHe() { return loaiLienHe; }
    public void setLoaiLienHe(LoaiLienHe loaiLienHe) { this.loaiLienHe = loaiLienHe; }
    
    public TrangThaiLienHe getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLienHe trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getNgayPhanHoi() { return ngayPhanHoi; }
    public void setNgayPhanHoi(LocalDateTime ngayPhanHoi) { this.ngayPhanHoi = ngayPhanHoi; }
    
    public String getNoiDungPhanHoi() { return noiDungPhanHoi; }
    public void setNoiDungPhanHoi(String noiDungPhanHoi) { this.noiDungPhanHoi = noiDungPhanHoi; }
    
    public String getNguoiPhanHoi() { return nguoiPhanHoi; }
    public void setNguoiPhanHoi(String nguoiPhanHoi) { this.nguoiPhanHoi = nguoiPhanHoi; }
    
    public Boolean getDangKyNhanTin() { return dangKyNhanTin; }
    public void setDangKyNhanTin(Boolean dangKyNhanTin) { this.dangKyNhanTin = dangKyNhanTin; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void phanHoi(String noiDungPhanHoi, String nguoiPhanHoi) {
        this.noiDungPhanHoi = noiDungPhanHoi;
        this.nguoiPhanHoi = nguoiPhanHoi;
        this.ngayPhanHoi = LocalDateTime.now();
        this.trangThai = TrangThaiLienHe.DA_PHAN_HOI;
    }
    
    public void daDoc() {
        if (this.trangThai == TrangThaiLienHe.CHUA_DOC) {
            this.trangThai = TrangThaiLienHe.DA_DOC;
        }
    }
    
    public boolean isDaPhanHoi() {
        return trangThai == TrangThaiLienHe.DA_PHAN_HOI;
    }
    
    // Enums
    public enum LoaiLienHe {
        GOP_Y, KHIEU_NAI, DE_XUAT, QUANG_CAO, HOP_TAC, KHAC
    }
    
    public enum TrangThaiLienHe {
        CHUA_DOC, DA_DOC, DANG_XU_LY, DA_PHAN_HOI, BI_XOA
    }
}