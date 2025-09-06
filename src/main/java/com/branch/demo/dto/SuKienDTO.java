package com.branch.demo.dto;

import com.branch.demo.domain.SuKien;
import com.branch.demo.domain.LoaiSuKien;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class SuKienDTO {
    private Long id;
    private String tenSuKien;
    private String moTa;
    private String noiDung;
    private LocalDateTime ngayDienRa;
    private String diaDiem;
    private LoaiSuKien loaiSuKien;
    private Long loaiSuKienId; // For form binding
    private String loaiSuKienTen; // For display
    private SuKien.TrangThaiSuKien trangThai;
    private Integer soLuongThamGiaDuKien;
    private Integer soLuongThamGiaThucTe;
    private String hinhAnhUrl;
    private String ghiChu;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For file upload
    private MultipartFile hinhAnhFile;

    public SuKienDTO() {}

    public SuKienDTO(SuKien suKien) {
        if (suKien != null) {
            this.id = suKien.getId();
            this.tenSuKien = suKien.getTenSuKien();
            this.moTa = suKien.getMoTa();
            this.noiDung = suKien.getNoiDung();
            this.ngayDienRa = suKien.getNgayDienRa();
            this.diaDiem = suKien.getDiaDiem();
            this.loaiSuKien = suKien.getLoaiSuKien();
            if (suKien.getLoaiSuKien() != null) {
                this.loaiSuKienId = suKien.getLoaiSuKien().getId();
                this.loaiSuKienTen = suKien.getLoaiSuKien().getTenLoai();
            }
            this.trangThai = suKien.getTrangThai();
            this.soLuongThamGiaDuKien = suKien.getSoLuongThamGiaDuKien();
            this.soLuongThamGiaThucTe = suKien.getSoLuongThamGiaThucTe();
            this.hinhAnhUrl = suKien.getHinhAnhUrl();
            this.ghiChu = suKien.getGhiChu();
            this.createdAt = suKien.getCreatedAt();
            this.updatedAt = suKien.getUpdatedAt();
        }
    }
    
    // Static factory method
    public static SuKienDTO fromEntity(SuKien suKien) {
        return new SuKienDTO(suKien);
    }

    public SuKien toEntity() {
        SuKien suKien = new SuKien();
        suKien.setId(this.id);
        suKien.setTenSuKien(this.tenSuKien);
        suKien.setMoTa(this.moTa);
        suKien.setNoiDung(this.noiDung);
        suKien.setNgayDienRa(this.ngayDienRa);
        suKien.setDiaDiem(this.diaDiem);
        suKien.setLoaiSuKien(this.loaiSuKien);
        suKien.setTrangThai(this.trangThai);
        suKien.setSoLuongThamGiaDuKien(this.soLuongThamGiaDuKien);
        suKien.setSoLuongThamGiaThucTe(this.soLuongThamGiaThucTe);
        suKien.setHinhAnhUrl(this.hinhAnhUrl);
        suKien.setGhiChu(this.ghiChu);
        suKien.setCreatedAt(this.createdAt);
        suKien.setUpdatedAt(this.updatedAt);
        return suKien;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenSuKien() { return tenSuKien; }
    public void setTenSuKien(String tenSuKien) { this.tenSuKien = tenSuKien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalDateTime getNgayDienRa() { return ngayDienRa; }
    public void setNgayDienRa(LocalDateTime ngayDienRa) { this.ngayDienRa = ngayDienRa; }

    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }

    public LoaiSuKien getLoaiSuKien() { return loaiSuKien; }
    public void setLoaiSuKien(LoaiSuKien loaiSuKien) { 
        this.loaiSuKien = loaiSuKien;
        if (loaiSuKien != null) {
            this.loaiSuKienId = loaiSuKien.getId();
            this.loaiSuKienTen = loaiSuKien.getTenLoai();
        }
    }
    
    public Long getLoaiSuKienId() { return loaiSuKienId; }
    public void setLoaiSuKienId(Long loaiSuKienId) { this.loaiSuKienId = loaiSuKienId; }
    
    public String getLoaiSuKienTen() { return loaiSuKienTen; }
    public void setLoaiSuKienTen(String loaiSuKienTen) { this.loaiSuKienTen = loaiSuKienTen; }

    public SuKien.TrangThaiSuKien getTrangThai() { return trangThai; }
    public void setTrangThai(SuKien.TrangThaiSuKien trangThai) { this.trangThai = trangThai; }

    public Integer getSoLuongThamGiaDuKien() { return soLuongThamGiaDuKien; }
    public void setSoLuongThamGiaDuKien(Integer soLuongThamGiaDuKien) { this.soLuongThamGiaDuKien = soLuongThamGiaDuKien; }

    public Integer getSoLuongThamGiaThucTe() { return soLuongThamGiaThucTe; }
    public void setSoLuongThamGiaThucTe(Integer soLuongThamGiaThucTe) { this.soLuongThamGiaThucTe = soLuongThamGiaThucTe; }

    public String getHinhAnhUrl() { return hinhAnhUrl; }
    public void setHinhAnhUrl(String hinhAnhUrl) { this.hinhAnhUrl = hinhAnhUrl; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public MultipartFile getHinhAnhFile() { return hinhAnhFile; }
    public void setHinhAnhFile(MultipartFile hinhAnhFile) { this.hinhAnhFile = hinhAnhFile; }
}