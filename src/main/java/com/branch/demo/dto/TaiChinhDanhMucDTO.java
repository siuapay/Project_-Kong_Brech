package com.branch.demo.dto;

import com.branch.demo.domain.TaiChinhDanhMuc;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaiChinhDanhMucDTO {
    
    private Long id;
    
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String tenDanhMuc;
    
    @NotNull(message = "Loại danh mục không được để trống")
    private String loai; // THU hoặc CHI
    
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String moTa;
    
    // Display fields (read-only)
    private String loaiDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long soLuongGiaoDich; // Số lượng giao dịch sử dụng danh mục này
    private boolean dangSuDung; // Có đang được sử dụng không
    
    // Constructors
    public TaiChinhDanhMucDTO() {}
    
    public TaiChinhDanhMucDTO(TaiChinhDanhMuc entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.tenDanhMuc = entity.getTenDanhMuc();
            this.loai = entity.getLoai() != null ? entity.getLoai().name() : null;
            this.moTa = entity.getMoTa();
            this.loaiDisplayName = entity.getLoai() != null ? entity.getLoai().getDisplayName() : null;
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }
    
    // Convert to Entity
    public TaiChinhDanhMuc toEntity() {
        TaiChinhDanhMuc entity = new TaiChinhDanhMuc();
        entity.setId(this.id);
        entity.setTenDanhMuc(this.tenDanhMuc);
        entity.setLoai(this.loai != null ? TaiChinhDanhMuc.LoaiDanhMuc.valueOf(this.loai) : null);
        entity.setMoTa(this.moTa);
        return entity;
    }
    
    // Update existing entity
    public void updateEntity(TaiChinhDanhMuc entity) {
        if (entity != null) {
            entity.setTenDanhMuc(this.tenDanhMuc);
            entity.setLoai(this.loai != null ? TaiChinhDanhMuc.LoaiDanhMuc.valueOf(this.loai) : null);
            entity.setMoTa(this.moTa);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    public String getLoaiDisplayName() { return loaiDisplayName; }
    public void setLoaiDisplayName(String loaiDisplayName) { this.loaiDisplayName = loaiDisplayName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getSoLuongGiaoDich() { return soLuongGiaoDich; }
    public void setSoLuongGiaoDich(Long soLuongGiaoDich) { this.soLuongGiaoDich = soLuongGiaoDich; }
    
    public boolean isDangSuDung() { return dangSuDung; }
    public void setDangSuDung(boolean dangSuDung) { this.dangSuDung = dangSuDung; }
    
    // Helper methods for display
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getUpdatedAtFormatted() {
        return updatedAt != null ? updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public boolean isThu() {
        return "THU".equals(loai);
    }
    
    public boolean isChi() {
        return "CHI".equals(loai);
    }
    
    public String getLoaiCssClass() {
        return isThu() ? "badge-success" : "badge-danger";
    }
}