package com.branch.demo.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiaoDichSearchCriteria {
    
    private String search; // General search term
    private String loai; // THU, CHI
    private Long danhMucId;
    private Long nguoiPhuTrachId;
    private Long nhanSuId; // Alias for nguoiPhuTrachId
    private String noiDung;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime tuNgay;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime denNgay;
    
    private BigDecimal soTienMin;
    private BigDecimal soTienMax;
    private Integer nam;
    private Integer thang;
    
    // Pagination
    private int page = 0;
    private int size = 20;
    private String sort = "thoiGian";
    private String direction = "desc";
    
    // Constructors
    public GiaoDichSearchCriteria() {}
    
    // Getters and Setters
    public String getSearch() { return search; }
    public void setSearch(String search) { 
        this.search = search; 
        this.noiDung = search; // Map search to noiDung for compatibility
    }
    
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    
    public Long getDanhMucId() { return danhMucId; }
    public void setDanhMucId(Long danhMucId) { this.danhMucId = danhMucId; }
    
    public Long getNguoiPhuTrachId() { return nguoiPhuTrachId; }
    public void setNguoiPhuTrachId(Long nguoiPhuTrachId) { this.nguoiPhuTrachId = nguoiPhuTrachId; }
    
    public Long getNhanSuId() { return nhanSuId != null ? nhanSuId : nguoiPhuTrachId; }
    public void setNhanSuId(Long nhanSuId) { 
        this.nhanSuId = nhanSuId; 
        this.nguoiPhuTrachId = nhanSuId; // Map nhanSuId to nguoiPhuTrachId for compatibility
    }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public LocalDateTime getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDateTime tuNgay) { this.tuNgay = tuNgay; }
    
    public LocalDateTime getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDateTime denNgay) { this.denNgay = denNgay; }
    
    public BigDecimal getSoTienMin() { return soTienMin; }
    public void setSoTienMin(BigDecimal soTienMin) { this.soTienMin = soTienMin; }
    
    public BigDecimal getSoTienMax() { return soTienMax; }
    public void setSoTienMax(BigDecimal soTienMax) { this.soTienMax = soTienMax; }
    
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    
    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    
    // Helper methods
    public boolean hasFilters() {
        return loai != null || danhMucId != null || nguoiPhuTrachId != null || 
               (noiDung != null && !noiDung.trim().isEmpty()) ||
               tuNgay != null || denNgay != null || 
               soTienMin != null || soTienMax != null ||
               nam != null || thang != null;
    }
    
    public void reset() {
        this.loai = null;
        this.danhMucId = null;
        this.nguoiPhuTrachId = null;
        this.noiDung = null;
        this.tuNgay = null;
        this.denNgay = null;
        this.soTienMin = null;
        this.soTienMax = null;
        this.nam = null;
        this.thang = null;
        this.page = 0;
    }
    
    @Override
    public String toString() {
        return "GiaoDichSearchCriteria{" +
                "loai='" + loai + '\'' +
                ", danhMucId=" + danhMucId +
                ", nguoiPhuTrachId=" + nguoiPhuTrachId +
                ", noiDung='" + noiDung + '\'' +
                ", tuNgay=" + tuNgay +
                ", denNgay=" + denNgay +
                ", soTienMin=" + soTienMin +
                ", soTienMax=" + soTienMax +
                ", nam=" + nam +
                ", thang=" + thang +
                '}';
    }
}