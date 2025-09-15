package com.branch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "tai_chinh_danh_muc", 
       uniqueConstraints = @UniqueConstraint(columnNames = "ten_danh_muc"))
public class TaiChinhDanhMuc extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_danh_muc", nullable = false, length = 255)
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String tenDanhMuc;
    
    @Column(name = "loai", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Loại danh mục không được để trống")
    private LoaiDanhMuc loai;
    
    @Column(name = "mo_ta", length = 1000)
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String moTa;
    
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
    public TaiChinhDanhMuc() {}
    
    public TaiChinhDanhMuc(String tenDanhMuc, LoaiDanhMuc loai) {
        this.tenDanhMuc = tenDanhMuc;
        this.loai = loai;
    }
    
    public TaiChinhDanhMuc(String tenDanhMuc, LoaiDanhMuc loai, String moTa) {
        this.tenDanhMuc = tenDanhMuc;
        this.loai = loai;
        this.moTa = moTa;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public LoaiDanhMuc getLoai() { return loai; }
    public void setLoai(LoaiDanhMuc loai) { this.loai = loai; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isThu() {
        return loai == LoaiDanhMuc.THU;
    }
    
    public boolean isChi() {
        return loai == LoaiDanhMuc.CHI;
    }
    
    public String getLoaiDisplayName() {
        return loai.getDisplayName();
    }
    
    // Enum for category type
    public enum LoaiDanhMuc {
        THU("Thu"),
        CHI("Chi");
        
        private final String displayName;
        
        LoaiDanhMuc(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    @Override
    public String toString() {
        return "TaiChinhDanhMuc{" +
                "id=" + id +
                ", tenDanhMuc='" + tenDanhMuc + '\'' +
                ", loai=" + loai +
                ", moTa='" + moTa + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiChinhDanhMuc)) return false;
        TaiChinhDanhMuc that = (TaiChinhDanhMuc) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}