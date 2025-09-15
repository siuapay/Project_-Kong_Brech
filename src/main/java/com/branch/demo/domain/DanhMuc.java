package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "danh_muc")
public class DanhMuc extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_danh_muc", nullable = false, length = 255)
    private String tenDanhMuc;
    
    @Column(name = "slug", unique = true, nullable = false, length = 255)
    private String slug;
    
    @Column(name = "mo_ta", length = 1000)
    private String moTa;
    
    @Column(name = "thu_tu")
    private Integer thuTu = 0;
    
    @Column(name = "mau_sac", length = 20) // Hex color code
    private String mauSac;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "trang_thai", length = 50)
    @Enumerated(EnumType.STRING)
    private TrangThaiDanhMuc trangThai = TrangThaiDanhMuc.HOAT_DONG;
    
    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaiViet> danhSachBaiViet = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    public enum TrangThaiDanhMuc {
        HOAT_DONG("Hoạt động"),
        TAM_NGHI("Tạm nghỉ");
        
        private final String displayName;
        
        TrangThaiDanhMuc(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
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
    public DanhMuc() {}
    
    public DanhMuc(String tenDanhMuc, String slug) {
        this.tenDanhMuc = tenDanhMuc;
        this.slug = slug;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }
    
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public TrangThaiDanhMuc getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDanhMuc trangThai) { this.trangThai = trangThai; }
    
    public List<BaiViet> getDanhSachBaiViet() { return danhSachBaiViet; }
    public void setDanhSachBaiViet(List<BaiViet> danhSachBaiViet) { this.danhSachBaiViet = danhSachBaiViet; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    // Helper methods
    public int getSoLuongBaiViet() {
        return danhSachBaiViet != null ? (int) danhSachBaiViet.stream()
                .filter(bv -> bv.getDeletedAt() == null)
                .count() : 0;
    }
    
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void restore() {
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
}