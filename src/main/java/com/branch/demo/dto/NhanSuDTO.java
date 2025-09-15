package com.branch.demo.dto;

public class NhanSuDTO {
    private Long id;
    private String hoTen;
    private String chucVu;
    private String banNganh;
    private String email;
    private String dienThoai;

    public NhanSuDTO() {}

    public NhanSuDTO(Long id, String hoTen, String chucVu, String banNganh, String email, String dienThoai) {
        this.id = id;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.banNganh = banNganh;
        this.email = email;
        this.dienThoai = dienThoai;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getBanNganh() { return banNganh; }
    public void setBanNganh(String banNganh) { this.banNganh = banNganh; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
}