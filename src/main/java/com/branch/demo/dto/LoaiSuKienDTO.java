package com.branch.demo.dto;

import com.branch.demo.domain.LoaiSuKien;

public class LoaiSuKienDTO {
    
    private Long id;
    private String tenLoai;
    private String moTa;
    private String mauSac;
    private String icon;
    private Integer thuTu;
    private boolean kichHoat = true;
    
    // Constructors
    public LoaiSuKienDTO() {}
    
    public LoaiSuKienDTO(String tenLoai, String moTa) {
        this.tenLoai = tenLoai;
        this.moTa = moTa;
    }
    
    // Convert from Entity to DTO
    public static LoaiSuKienDTO fromEntity(LoaiSuKien loaiSuKien) {
        if (loaiSuKien == null) return null;
        
        LoaiSuKienDTO dto = new LoaiSuKienDTO();
        dto.setId(loaiSuKien.getId());
        dto.setTenLoai(loaiSuKien.getTenLoai());
        dto.setMoTa(loaiSuKien.getMoTa());
        dto.setMauSac(loaiSuKien.getMauSac());
        dto.setIcon(loaiSuKien.getIcon());
        dto.setKichHoat(loaiSuKien.isKichHoat());
        
        return dto;
    }
    
    // Convert from DTO to Entity
    public LoaiSuKien toEntity() {
        LoaiSuKien loaiSuKien = new LoaiSuKien();
        loaiSuKien.setId(this.id);
        loaiSuKien.setTenLoai(this.tenLoai);
        loaiSuKien.setMoTa(this.moTa);
        loaiSuKien.setMauSac(this.mauSac);
        loaiSuKien.setIcon(this.icon);
        loaiSuKien.setKichHoat(this.kichHoat);
        
        return loaiSuKien;
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
}