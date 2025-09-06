package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
public class ThongBao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;
    
    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;
    
    @Column(name = "loai_thong_bao")
    @Enumerated(EnumType.STRING)
    private LoaiThongBao loaiThongBao;
    
    @Column(name = "muc_do_uu_tien")
    @Enumerated(EnumType.STRING)
    private MucDoUuTien mucDoUuTien = MucDoUuTien.BINH_THUONG;
    
    @Column(name = "nguoi_gui", length = 255)
    private String nguoiGui;
    
    @Column(name = "doi_tuong_nhan", length = 255)
    private String doiTuongNhan; // Tất cả, Ban ngành cụ thể, Nhóm cụ thể
    
    @Column(name = "ngay_gui")
    private LocalDateTime ngayGui;
    
    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan;
    
    @Column(name = "hinh_anh_url", length = 500)
    private String hinhAnhUrl;
    
    @Column(name = "file_dinh_kem_url", length = 500)
    private String fileDinhKemUrl;
    
    @Column(name = "so_luot_xem")
    private Integer soLuotXem = 0;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiThongBao trangThai = TrangThaiThongBao.NHAP;
    
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
    public ThongBao() {}
    
    public ThongBao(String tieuDe, String noiDung, LoaiThongBao loaiThongBao) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loaiThongBao = loaiThongBao;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public LoaiThongBao getLoaiThongBao() { return loaiThongBao; }
    public void setLoaiThongBao(LoaiThongBao loaiThongBao) { this.loaiThongBao = loaiThongBao; }
    
    public MucDoUuTien getMucDoUuTien() { return mucDoUuTien; }
    public void setMucDoUuTien(MucDoUuTien mucDoUuTien) { this.mucDoUuTien = mucDoUuTien; }
    
    public String getNguoiGui() { return nguoiGui; }
    public void setNguoiGui(String nguoiGui) { this.nguoiGui = nguoiGui; }
    
    public String getDoiTuongNhan() { return doiTuongNhan; }
    public void setDoiTuongNhan(String doiTuongNhan) { this.doiTuongNhan = doiTuongNhan; }
    
    public LocalDateTime getNgayGui() { return ngayGui; }
    public void setNgayGui(LocalDateTime ngayGui) { this.ngayGui = ngayGui; }
    
    public LocalDateTime getNgayHetHan() { return ngayHetHan; }
    public void setNgayHetHan(LocalDateTime ngayHetHan) { this.ngayHetHan = ngayHetHan; }
    
    public String getHinhAnhUrl() { return hinhAnhUrl; }
    public void setHinhAnhUrl(String hinhAnhUrl) { this.hinhAnhUrl = hinhAnhUrl; }
    
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
    
    public Integer getSoLuotXem() { return soLuotXem; }
    public void setSoLuotXem(Integer soLuotXem) { this.soLuotXem = soLuotXem; }
    
    public TrangThaiThongBao getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiThongBao trangThai) { this.trangThai = trangThai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void tangLuotXem() {
        this.soLuotXem++;
    }
    
    public boolean isHetHan() {
        return ngayHetHan != null && LocalDateTime.now().isAfter(ngayHetHan);
    }
    
    public boolean isUuTienCao() {
        return mucDoUuTien == MucDoUuTien.CAO || mucDoUuTien == MucDoUuTien.KHAN_CAP;
    }
    
    // Enums
    public enum LoaiThongBao {
        CHUNG, SU_KIEN, HOP, KHAC_CAP, TU_THIEN, GIAO_DUC
    }
    
    public enum MucDoUuTien {
        THAP, BINH_THUONG, CAO, KHAN_CAP
    }
    
    public enum TrangThaiThongBao {
        NHAP, DA_GUI, HET_HAN, BI_XOA
    }
}