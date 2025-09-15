package com.branch.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class TaiChinhThongKeDTO {
    
    private Integer nam;
    private BigDecimal tongThu = BigDecimal.ZERO;
    private BigDecimal tongChi = BigDecimal.ZERO;
    private BigDecimal soDu = BigDecimal.ZERO;
    private String trangThaiTaiChinh;
    
    // Thống kê chi tiết
    private List<ThongKeDanhMuc> thongKeDanhMuc;
    private List<ThongKeThang> thongKeThang;
    private List<ThongKeNguoiPhuTrach> thongKeNguoiPhuTrach;
    
    // So sánh với năm trước
    private TaiChinhThongKeDTO namTruoc;
    private BigDecimal tyLeThayDoiThu;
    private BigDecimal tyLeThayDoiChi;
    private BigDecimal tyLeThayDoiSoDu;
    
    // Constructors
    public TaiChinhThongKeDTO() {}
    
    public TaiChinhThongKeDTO(Integer nam, BigDecimal tongThu, BigDecimal tongChi) {
        this.nam = nam;
        this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
        this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
        this.soDu = this.tongThu.subtract(this.tongChi);
        this.trangThaiTaiChinh = calculateTrangThai();
    }
    
    // Getters and Setters
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    
    public BigDecimal getTongThu() { return tongThu; }
    public void setTongThu(BigDecimal tongThu) { 
        this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
        this.soDu = this.tongThu.subtract(this.tongChi);
        this.trangThaiTaiChinh = calculateTrangThai();
    }
    
    public BigDecimal getTongChi() { return tongChi; }
    public void setTongChi(BigDecimal tongChi) { 
        this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
        this.soDu = this.tongThu.subtract(this.tongChi);
        this.trangThaiTaiChinh = calculateTrangThai();
    }
    
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
    
    public String getTrangThaiTaiChinh() { return trangThaiTaiChinh; }
    public void setTrangThaiTaiChinh(String trangThaiTaiChinh) { this.trangThaiTaiChinh = trangThaiTaiChinh; }
    
    public List<ThongKeDanhMuc> getThongKeDanhMuc() { return thongKeDanhMuc; }
    public void setThongKeDanhMuc(List<ThongKeDanhMuc> thongKeDanhMuc) { this.thongKeDanhMuc = thongKeDanhMuc; }
    
    public List<ThongKeThang> getThongKeThang() { return thongKeThang; }
    public void setThongKeThang(List<ThongKeThang> thongKeThang) { this.thongKeThang = thongKeThang; }
    
    public List<ThongKeNguoiPhuTrach> getThongKeNguoiPhuTrach() { return thongKeNguoiPhuTrach; }
    public void setThongKeNguoiPhuTrach(List<ThongKeNguoiPhuTrach> thongKeNguoiPhuTrach) { this.thongKeNguoiPhuTrach = thongKeNguoiPhuTrach; }
    
    public TaiChinhThongKeDTO getNamTruoc() { return namTruoc; }
    public void setNamTruoc(TaiChinhThongKeDTO namTruoc) { this.namTruoc = namTruoc; }
    
    public BigDecimal getTyLeThayDoiThu() { return tyLeThayDoiThu; }
    public void setTyLeThayDoiThu(BigDecimal tyLeThayDoiThu) { this.tyLeThayDoiThu = tyLeThayDoiThu; }
    
    public BigDecimal getTyLeThayDoiChi() { return tyLeThayDoiChi; }
    public void setTyLeThayDoiChi(BigDecimal tyLeThayDoiChi) { this.tyLeThayDoiChi = tyLeThayDoiChi; }
    
    public BigDecimal getTyLeThayDoiSoDu() { return tyLeThayDoiSoDu; }
    public void setTyLeThayDoiSoDu(BigDecimal tyLeThayDoiSoDu) { this.tyLeThayDoiSoDu = tyLeThayDoiSoDu; }
    
    // Helper methods
    private String calculateTrangThai() {
        if (soDu.compareTo(BigDecimal.ZERO) > 0) {
            return "Lời";
        } else if (soDu.compareTo(BigDecimal.ZERO) < 0) {
            return "Lỗ";
        } else {
            return "Hòa vốn";
        }
    }
    
    public boolean isLoi() {
        return soDu.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isLo() {
        return soDu.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isHoaVon() {
        return soDu.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public String getTrangThaiCssClass() {
        if (isLoi()) return "text-success";
        if (isLo()) return "text-danger";
        return "text-warning";
    }
    
    // Formatted display methods
    public String getTongThuFormatted() {
        return String.format("%,.0f", tongThu);
    }
    
    public String getTongChiFormatted() {
        return String.format("%,.0f", tongChi);
    }
    
    public String getSoDuFormatted() {
        return String.format("%,.0f", soDu);
    }
    
    // Inner classes for detailed statistics
    public static class ThongKeDanhMuc {
        private String tenDanhMuc;
        private String loai;
        private BigDecimal tongTien;
        private Long soLuongGiaoDich;
        private BigDecimal tyLe;
        
        public ThongKeDanhMuc() {}
        
        public ThongKeDanhMuc(String tenDanhMuc, String loai, BigDecimal tongTien, Long soLuongGiaoDich) {
            this.tenDanhMuc = tenDanhMuc;
            this.loai = loai;
            this.tongTien = tongTien != null ? tongTien : BigDecimal.ZERO;
            this.soLuongGiaoDich = soLuongGiaoDich != null ? soLuongGiaoDich : 0L;
        }
        
        // Getters and Setters
        public String getTenDanhMuc() { return tenDanhMuc; }
        public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
        
        public String getLoai() { return loai; }
        public void setLoai(String loai) { this.loai = loai; }
        
        public BigDecimal getTongTien() { return tongTien; }
        public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
        
        public Long getSoLuongGiaoDich() { return soLuongGiaoDich; }
        public void setSoLuongGiaoDich(Long soLuongGiaoDich) { this.soLuongGiaoDich = soLuongGiaoDich; }
        
        public BigDecimal getTyLe() { return tyLe; }
        public void setTyLe(BigDecimal tyLe) { this.tyLe = tyLe; }
        
        public String getTongTienFormatted() {
            return String.format("%,.0f", tongTien);
        }
    }
    
    public static class ThongKeThang {
        private Integer thang;
        private BigDecimal tongThu;
        private BigDecimal tongChi;
        private BigDecimal soDu;
        
        public ThongKeThang() {}
        
        public ThongKeThang(Integer thang, BigDecimal tongThu, BigDecimal tongChi) {
            this.thang = thang;
            this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
            this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
            this.soDu = this.tongThu.subtract(this.tongChi);
        }
        
        // Getters and Setters
        public Integer getThang() { return thang; }
        public void setThang(Integer thang) { this.thang = thang; }
        
        public BigDecimal getTongThu() { return tongThu; }
        public void setTongThu(BigDecimal tongThu) { this.tongThu = tongThu; }
        
        public BigDecimal getTongChi() { return tongChi; }
        public void setTongChi(BigDecimal tongChi) { this.tongChi = tongChi; }
        
        public BigDecimal getSoDu() { return soDu; }
        public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
        
        public String getTongThuFormatted() {
            return String.format("%,.0f", tongThu);
        }
        
        public String getTongChiFormatted() {
            return String.format("%,.0f", tongChi);
        }
        
        public String getSoDuFormatted() {
            return String.format("%,.0f", soDu);
        }
    }
    
    public static class ThongKeNguoiPhuTrach {
        private String tenNguoiPhuTrach;
        private BigDecimal tongThu;
        private BigDecimal tongChi;
        private Long soLuongGiaoDich;
        
        public ThongKeNguoiPhuTrach() {}
        
        public ThongKeNguoiPhuTrach(String tenNguoiPhuTrach, BigDecimal tongThu, BigDecimal tongChi, Long soLuongGiaoDich) {
            this.tenNguoiPhuTrach = tenNguoiPhuTrach;
            this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
            this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
            this.soLuongGiaoDich = soLuongGiaoDich != null ? soLuongGiaoDich : 0L;
        }
        
        // Getters and Setters
        public String getTenNguoiPhuTrach() { return tenNguoiPhuTrach; }
        public void setTenNguoiPhuTrach(String tenNguoiPhuTrach) { this.tenNguoiPhuTrach = tenNguoiPhuTrach; }
        
        public BigDecimal getTongThu() { return tongThu; }
        public void setTongThu(BigDecimal tongThu) { this.tongThu = tongThu; }
        
        public BigDecimal getTongChi() { return tongChi; }
        public void setTongChi(BigDecimal tongChi) { this.tongChi = tongChi; }
        
        public Long getSoLuongGiaoDich() { return soLuongGiaoDich; }
        public void setSoLuongGiaoDich(Long soLuongGiaoDich) { this.soLuongGiaoDich = soLuongGiaoDich; }
        
        public String getTongThuFormatted() {
            return String.format("%,.0f", tongThu);
        }
        
        public String getTongChiFormatted() {
            return String.format("%,.0f", tongChi);
        }
    }
}