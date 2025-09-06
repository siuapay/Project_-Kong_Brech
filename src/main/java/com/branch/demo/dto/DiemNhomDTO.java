package com.branch.demo.dto;

public class DiemNhomDTO {
    private Long id;
    private String tenDiemNhom;
    private String diaChi;
    private String moTa;
    private String thoiGianSinhHoat;
    private BanNganhDTO banNganh;
    
    // Constructors
    public DiemNhomDTO() {}
    
    public DiemNhomDTO(Long id, String tenDiemNhom, String diaChi, String moTa, String thoiGianSinhHoat) {
        this.id = id;
        this.tenDiemNhom = tenDiemNhom;
        this.diaChi = diaChi;
        this.moTa = moTa;
        this.thoiGianSinhHoat = thoiGianSinhHoat;
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
    
    public String getThoiGianSinhHoat() { return thoiGianSinhHoat; }
    public void setThoiGianSinhHoat(String thoiGianSinhHoat) { this.thoiGianSinhHoat = thoiGianSinhHoat; }
    
    public BanNganhDTO getBanNganh() { return banNganh; }
    public void setBanNganh(BanNganhDTO banNganh) { this.banNganh = banNganh; }
    
    // Nested DTO for BanNganh
    public static class BanNganhDTO {
        private Long id;
        private String tenBan;
        private String maBan;
        
        public BanNganhDTO() {}
        
        public BanNganhDTO(Long id, String tenBan, String maBan) {
            this.id = id;
            this.tenBan = tenBan;
            this.maBan = maBan;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTenBan() { return tenBan; }
        public void setTenBan(String tenBan) { this.tenBan = tenBan; }
        
        public String getMaBan() { return maBan; }
        public void setMaBan(String maBan) { this.maBan = maBan; }
    }
}