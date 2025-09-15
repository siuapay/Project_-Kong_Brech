package com.branch.demo.dto;

import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.TaiChinhDanhMuc;
import com.branch.demo.domain.NhanSu;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaiChinhGiaoDichDTO {
    
    private Long id;
    
    @NotNull(message = "Thời gian giao dịch không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime thoiGian;
    
    @NotNull(message = "Loại giao dịch không được để trống")
    private String loai; // THU hoặc CHI
    
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Số tiền không hợp lệ")
    private BigDecimal soTien;
    
    @NotBlank(message = "Nội dung giao dịch không được để trống")
    @Size(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
    private String noiDung;
    
    @NotNull(message = "Danh mục không được để trống")
    private Long danhMucId;
    
    private Long nguoiPhuTrachId;
    
    // Display fields (read-only)
    private String tenDanhMuc;
    private String loaiDanhMuc;
    private String tenNguoiPhuTrach;
    private String loaiDisplayName;
    private Integer nam;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public TaiChinhGiaoDichDTO() {}
    
    public TaiChinhGiaoDichDTO(TaiChinhGiaoDich entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.thoiGian = entity.getThoiGian();
            this.loai = entity.getLoai() != null ? entity.getLoai().name() : null;
            this.soTien = entity.getSoTien();
            this.noiDung = entity.getNoiDung();
            this.danhMucId = entity.getDanhMuc() != null ? entity.getDanhMuc().getId() : null;
            this.nguoiPhuTrachId = entity.getNguoiPhuTrach() != null ? entity.getNguoiPhuTrach().getId() : null;
            
            // Display fields
            this.tenDanhMuc = entity.getDanhMuc() != null ? entity.getDanhMuc().getTenDanhMuc() : null;
            this.loaiDanhMuc = entity.getDanhMuc() != null ? entity.getDanhMuc().getLoaiDisplayName() : null;
            this.tenNguoiPhuTrach = entity.getNguoiPhuTrach() != null ? entity.getNguoiPhuTrach().getHoTen() : null;
            this.loaiDisplayName = entity.getLoai() != null ? entity.getLoai().getDisplayName() : null;
            this.nam = entity.getNam();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }
    
    // Convert to Entity
    public TaiChinhGiaoDich toEntity() {
        TaiChinhGiaoDich entity = new TaiChinhGiaoDich();
        entity.setId(this.id);
        entity.setThoiGian(this.thoiGian);
        entity.setLoai(this.loai != null ? TaiChinhGiaoDich.LoaiGiaoDich.valueOf(this.loai) : null);
        entity.setSoTien(this.soTien);
        entity.setNoiDung(this.noiDung);
        
        if (this.danhMucId != null) {
            TaiChinhDanhMuc danhMuc = new TaiChinhDanhMuc();
            danhMuc.setId(this.danhMucId);
            entity.setDanhMuc(danhMuc);
        }
        
        if (this.nguoiPhuTrachId != null) {
            NhanSu nguoiPhuTrach = new NhanSu();
            nguoiPhuTrach.setId(this.nguoiPhuTrachId);
            entity.setNguoiPhuTrach(nguoiPhuTrach);
        }
        
        return entity;
    }
    
    // Update existing entity
    public void updateEntity(TaiChinhGiaoDich entity) {
        if (entity != null) {
            entity.setThoiGian(this.thoiGian);
            entity.setLoai(this.loai != null ? TaiChinhGiaoDich.LoaiGiaoDich.valueOf(this.loai) : null);
            entity.setSoTien(this.soTien);
            entity.setNoiDung(this.noiDung);
            
            if (this.danhMucId != null) {
                TaiChinhDanhMuc danhMuc = new TaiChinhDanhMuc();
                danhMuc.setId(this.danhMucId);
                entity.setDanhMuc(danhMuc);
            }
            
            if (this.nguoiPhuTrachId != null) {
                NhanSu nguoiPhuTrach = new NhanSu();
                nguoiPhuTrach.setId(this.nguoiPhuTrachId);
                entity.setNguoiPhuTrach(nguoiPhuTrach);
            } else {
                entity.setNguoiPhuTrach(null);
            }
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
    
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public Long getDanhMucId() { return danhMucId; }
    public void setDanhMucId(Long danhMucId) { this.danhMucId = danhMucId; }
    
    public Long getNguoiPhuTrachId() { return nguoiPhuTrachId; }
    public void setNguoiPhuTrachId(Long nguoiPhuTrachId) { this.nguoiPhuTrachId = nguoiPhuTrachId; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public String getLoaiDanhMuc() { return loaiDanhMuc; }
    public void setLoaiDanhMuc(String loaiDanhMuc) { this.loaiDanhMuc = loaiDanhMuc; }
    
    public String getTenNguoiPhuTrach() { return tenNguoiPhuTrach; }
    public void setTenNguoiPhuTrach(String tenNguoiPhuTrach) { this.tenNguoiPhuTrach = tenNguoiPhuTrach; }
    
    public String getLoaiDisplayName() { return loaiDisplayName; }
    public void setLoaiDisplayName(String loaiDisplayName) { this.loaiDisplayName = loaiDisplayName; }
    
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods for display
    public String getThoiGianFormatted() {
        return thoiGian != null ? thoiGian.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getThoiGianFormInput() {
        return thoiGian != null ? thoiGian.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) : "";
    }
    
    public String getSoTienFormatted() {
        return soTien != null ? String.format("%,.0f", soTien) : "0";
    }
    
    public boolean isThu() {
        return "THU".equals(loai);
    }
    
    public boolean isChi() {
        return "CHI".equals(loai);
    }
}