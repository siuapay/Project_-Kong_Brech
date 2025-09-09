package com.branch.demo.dto;

import com.branch.demo.domain.BaiViet;
import java.time.LocalDateTime;
import java.util.List;

public class BaiVietDTO {
    private Long id;
    private String tieuDe;
    private String slug;
    private String tomTat;
    private String noiDung;
    private String noiDungRich;
    private String anhDaiDien;
    private List<String> danhSachHinhAnh;
    private List<String> danhSachVideo;
    private BaiViet.TrangThaiBaiViet trangThai;
    private Long danhMucId;
    private String tenDanhMuc;
    private String tacGia;
    private BaiViet.LoaiTacGia loaiTacGia;
    private Long luotXem;
    private Boolean noiBat;
    private Boolean choPhepBinhLuan;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private LocalDateTime ngayXuatBan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BaiVietDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTomTat() { return tomTat; }
    public void setTomTat(String tomTat) { this.tomTat = tomTat; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getNoiDungRich() { return noiDungRich; }
    public void setNoiDungRich(String noiDungRich) { this.noiDungRich = noiDungRich; }

    public String getAnhDaiDien() { return anhDaiDien; }
    public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }

    public List<String> getDanhSachHinhAnh() { return danhSachHinhAnh; }
    public void setDanhSachHinhAnh(List<String> danhSachHinhAnh) { this.danhSachHinhAnh = danhSachHinhAnh; }

    public List<String> getDanhSachVideo() { return danhSachVideo; }
    public void setDanhSachVideo(List<String> danhSachVideo) { this.danhSachVideo = danhSachVideo; }

    public BaiViet.TrangThaiBaiViet getTrangThai() { return trangThai; }
    public void setTrangThai(BaiViet.TrangThaiBaiViet trangThai) { this.trangThai = trangThai; }

    public Long getDanhMucId() { return danhMucId; }
    public void setDanhMucId(Long danhMucId) { this.danhMucId = danhMucId; }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public BaiViet.LoaiTacGia getLoaiTacGia() { return loaiTacGia; }
    public void setLoaiTacGia(BaiViet.LoaiTacGia loaiTacGia) { this.loaiTacGia = loaiTacGia; }

    public Long getLuotXem() { return luotXem; }
    public void setLuotXem(Long luotXem) { this.luotXem = luotXem; }

    public Boolean getNoiBat() { return noiBat; }
    public void setNoiBat(Boolean noiBat) { this.noiBat = noiBat; }

    public Boolean getChoPhepBinhLuan() { return choPhepBinhLuan; }
    public void setChoPhepBinhLuan(Boolean choPhepBinhLuan) { this.choPhepBinhLuan = choPhepBinhLuan; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }

    public LocalDateTime getNgayXuatBan() { return ngayXuatBan; }
    public void setNgayXuatBan(LocalDateTime ngayXuatBan) { this.ngayXuatBan = ngayXuatBan; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}