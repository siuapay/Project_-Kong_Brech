package com.branch.demo.dto;

public class DanhMucDTO {
    private Long id;
    private String tenDanhMuc;
    private String slug;
    private String moTa;
    private Integer thuTu;
    private String mauSac;
    private String icon;
    private String trangThai;
    
    // Constructors
    public DanhMucDTO() {}
    
    public DanhMucDTO(Long id, String tenDanhMuc, String slug, String moTa) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
        this.slug = slug;
        this.moTa = moTa;
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
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}