package com.branch.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nhom")
public class Nhom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_nhom", nullable = false, length = 255)
    private String tenNhom;
    
    @Column(name = "mo_ta", length = 1000)
    private String moTa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diem_nhom_id")
    @JsonBackReference
    private DiemNhom diemNhom;
    
    @OneToMany(mappedBy = "nhom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TinHuu> danhSachTinHuu = new ArrayList<>();
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiNhom trangThai = TrangThaiNhom.HOAT_DONG;
    
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
    public Nhom() {}
    
    public Nhom(String tenNhom) {
        this.tenNhom = tenNhom;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenNhom() { return tenNhom; }
    public void setTenNhom(String tenNhom) { this.tenNhom = tenNhom; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public DiemNhom getDiemNhom() { return diemNhom; }
    public void setDiemNhom(DiemNhom diemNhom) { this.diemNhom = diemNhom; }
    
    public List<TinHuu> getDanhSachTinHuu() { return danhSachTinHuu; }
    public void setDanhSachTinHuu(List<TinHuu> danhSachTinHuu) { this.danhSachTinHuu = danhSachTinHuu; }
    
    public TrangThaiNhom getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNhom trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void addTinHuu(TinHuu tinHuu) {
        danhSachTinHuu.add(tinHuu);
        tinHuu.setNhom(this);
    }
    
    public void removeTinHuu(TinHuu tinHuu) {
        danhSachTinHuu.remove(tinHuu);
        tinHuu.setNhom(null);
    }
    
    public int getSoLuongTinHuu() {
        return danhSachTinHuu.size();
    }
    
    // Enums
    public enum TrangThaiNhom {
        HOAT_DONG, TAM_NGHI, DONG_CUA
    }
}