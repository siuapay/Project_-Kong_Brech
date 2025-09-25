package com.branch.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diem_nhom")
public class DiemNhom extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten_diem_nhom", nullable = false, length = 255)
    private String tenDiemNhom;
    
    @Column(name = "dia_chi", length = 500)
    private String diaChi;
    
    @Column(name = "mo_ta", length = 1000)
    private String moTa;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "diem_nhom_ban_nganh",
        joinColumns = @JoinColumn(name = "diem_nhom_id"),
        inverseJoinColumns = @JoinColumn(name = "ban_nganh_id")
    )
    @JsonIgnore
    private List<BanNganh> danhSachBanNganh = new ArrayList<>();
    
    @OneToMany(mappedBy = "diemNhom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Nhom> danhSachNhom = new ArrayList<>();
    
    @OneToMany(mappedBy = "diemNhom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NhanSuDiemNhom> danhSachNhanSu = new ArrayList<>();
    
    @OneToMany(mappedBy = "diemNhom", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NhanSu> danhSachNhanSuTrucTiep = new ArrayList<>();
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiDiemNhom trangThai = TrangThaiDiemNhom.HOAT_DONG;
    
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
    public DiemNhom() {}
    
    public DiemNhom(String tenDiemNhom, String diaChi) {
        this.tenDiemNhom = tenDiemNhom;
        this.diaChi = diaChi;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenDiemNhom() { return tenDiemNhom; }
    public void setTenDiemNhom(String tenDiemNhom) { this.tenDiemNhom = tenDiemNhom; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public List<BanNganh> getDanhSachBanNganh() { return danhSachBanNganh; }
    public void setDanhSachBanNganh(List<BanNganh> danhSachBanNganh) { this.danhSachBanNganh = danhSachBanNganh; }
    
    public List<Nhom> getDanhSachNhom() { return danhSachNhom; }
    public void setDanhSachNhom(List<Nhom> danhSachNhom) { this.danhSachNhom = danhSachNhom; }
    
    public List<NhanSuDiemNhom> getDanhSachNhanSu() { return danhSachNhanSu; }
    public void setDanhSachNhanSu(List<NhanSuDiemNhom> danhSachNhanSu) { this.danhSachNhanSu = danhSachNhanSu; }
    
    public List<NhanSu> getDanhSachNhanSuTrucTiep() { return danhSachNhanSuTrucTiep; }
    public void setDanhSachNhanSuTrucTiep(List<NhanSu> danhSachNhanSuTrucTiep) { this.danhSachNhanSuTrucTiep = danhSachNhanSuTrucTiep; }
    
    public TrangThaiDiemNhom getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDiemNhom trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void addNhom(Nhom nhom) {
        danhSachNhom.add(nhom);
        nhom.setDiemNhom(this);
    }
    
    public void removeNhom(Nhom nhom) {
        danhSachNhom.remove(nhom);
        nhom.setDiemNhom(null);
    }
    
    public void addBanNganh(BanNganh banNganh) {
        if (!danhSachBanNganh.contains(banNganh)) {
            danhSachBanNganh.add(banNganh);
            banNganh.getDanhSachDiemNhom().add(this);
        }
    }
    
    public void removeBanNganh(BanNganh banNganh) {
        danhSachBanNganh.remove(banNganh);
        banNganh.getDanhSachDiemNhom().remove(this);
    }
    
    public int getTongSoTinHuu() {
        return danhSachNhom.stream()
                .mapToInt(Nhom::getSoLuongTinHuu)
                .sum();
    }
    
    // Enums
    public enum TrangThaiDiemNhom {
        HOAT_DONG, CHUAN_BI, TAM_NGHI, DONG_CUA
    }
}