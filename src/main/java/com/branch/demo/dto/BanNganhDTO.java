package com.branch.demo.dto;

public class BanNganhDTO {
    private Long id;
    private String tenBan;
    private String maBan;
    private String moTa;
    
    // Constructors
    public BanNganhDTO() {}
    
    public BanNganhDTO(Long id, String tenBan, String maBan, String moTa) {
        this.id = id;
        this.tenBan = tenBan;
        this.maBan = maBan;
        this.moTa = moTa;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    
    public String getMaBan() { return maBan; }
    public void setMaBan(String maBan) { this.maBan = maBan; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}