package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "su_kien")
public class SuKien {
    
    public enum TrangThaiSuKien {
        DANG_CHUAN_BI("Đang chuẩn bị"),
        DANG_DIEN_RA("Đang diễn ra"),
        DA_KET_THUC("Đã kết thúc"),
        DA_HUY("Đã hủy");

        private final String displayName;

        TrangThaiSuKien(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_su_kien", nullable = false, length = 255)
    private String tenSuKien;
    
    @Column(name = "mo_ta", length = 1000)
    private String moTa;
    
    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;
    
    @Column(name = "ngay_dien_ra")
    private LocalDateTime ngayDienRa;
    
    @Column(name = "dia_diem", length = 500)
    private String diaDiem;
    
    // Ràng buộc foreign key với entity LoaiSuKien
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_su_kien_id", foreignKey = @ForeignKey(name = "FK_su_kien_loai_su_kien"))
    private LoaiSuKien loaiSuKien;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiSuKien trangThai = TrangThaiSuKien.DANG_CHUAN_BI;
    
    @Column(name = "so_luong_tham_gia_du_kien")
    private Integer soLuongThamGiaDuKien;
    
    @Column(name = "so_luong_tham_gia_thuc_te")
    private Integer soLuongThamGiaThucTe;
    
    @Column(name = "hinh_anh_url", length = 500)
    private String hinhAnhUrl;
    
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;
    
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
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
    public SuKien() {}
    
    public SuKien(String tenSuKien, LocalDateTime ngayDienRa, LoaiSuKien loaiSuKien) {
        this.tenSuKien = tenSuKien;
        this.ngayDienRa = ngayDienRa;
        this.loaiSuKien = loaiSuKien;
    }    

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenSuKien() { return tenSuKien; }
    public void setTenSuKien(String tenSuKien) { this.tenSuKien = tenSuKien; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public LocalDateTime getNgayDienRa() { return ngayDienRa; }
    public void setNgayDienRa(LocalDateTime ngayDienRa) { this.ngayDienRa = ngayDienRa; }
    
    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }
    
    public LoaiSuKien getLoaiSuKien() { return loaiSuKien; }
    public void setLoaiSuKien(LoaiSuKien loaiSuKien) { this.loaiSuKien = loaiSuKien; }
    
    public TrangThaiSuKien getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiSuKien trangThai) { this.trangThai = trangThai; }
    
    public Integer getSoLuongThamGiaDuKien() { return soLuongThamGiaDuKien; }
    public void setSoLuongThamGiaDuKien(Integer soLuongThamGiaDuKien) { this.soLuongThamGiaDuKien = soLuongThamGiaDuKien; }
    
    public Integer getSoLuongThamGiaThucTe() { return soLuongThamGiaThucTe; }
    public void setSoLuongThamGiaThucTe(Integer soLuongThamGiaThucTe) { this.soLuongThamGiaThucTe = soLuongThamGiaThucTe; }
    
    public String getHinhAnhUrl() { return hinhAnhUrl; }
    public void setHinhAnhUrl(String hinhAnhUrl) { this.hinhAnhUrl = hinhAnhUrl; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { 
        this.deleted = deleted;
        if (deleted) {
            this.deletedAt = LocalDateTime.now();
        } else {
            this.deletedAt = null;
        }
    }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isDangDienRa() {
        LocalDateTime now = LocalDateTime.now();
        return ngayDienRa != null && ngayDienRa.toLocalDate().equals(now.toLocalDate());
    }
    
    public boolean isSapDienRa() {
        LocalDateTime now = LocalDateTime.now();
        return ngayDienRa != null && ngayDienRa.isAfter(now);
    }
    
    public boolean isDaKetThuc() {
        LocalDateTime now = LocalDateTime.now();
        return ngayDienRa != null && ngayDienRa.isBefore(now);
    }
    
    // Validation methods
    public boolean isValidForSave() {
        return tenSuKien != null && !tenSuKien.trim().isEmpty() 
               && loaiSuKien != null && ngayDienRa != null;
    }
    
    public String getLoaiSuKienTen() {
        return loaiSuKien != null ? loaiSuKien.getTenLoai() : null;
    }
}