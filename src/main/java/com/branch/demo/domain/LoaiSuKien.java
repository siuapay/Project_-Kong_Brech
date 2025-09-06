package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loai_su_kien")
public class LoaiSuKien {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_loai", nullable = false, unique = true, length = 100)
    private String tenLoai;
    
    @Column(name = "mo_ta", length = 500)
    private String moTa;
    
    @Column(name = "mau_sac", length = 7) // Hex color code #FFFFFF
    private String mauSac;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "thu_tu")
    private Integer thuTu = 0;
    
    @Column(name = "kich_hoat", nullable = false)
    private boolean kichHoat = true;
    
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
    public LoaiSuKien() {}
    
    public LoaiSuKien(String tenLoai, String moTa) {
        this.tenLoai = tenLoai;
        this.moTa = moTa;
    }
    
    public LoaiSuKien(String tenLoai, String moTa, String mauSac) {
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.mauSac = mauSac;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }
    
    public boolean isKichHoat() { return kichHoat; }
    public void setKichHoat(boolean kichHoat) { this.kichHoat = kichHoat; }
    
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
    
    @Override
    public String toString() {
        return tenLoai;
    }
}