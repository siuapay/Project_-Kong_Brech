package com.branch.demo.dto;

import com.branch.demo.domain.ThongBao;
import java.time.LocalDateTime;

public class ThongBaoDTO {
    private Long id;
    private String tieuDe;
    private String noiDung;
    private String loaiThongBao;
    private String trangThai;
    private String nguoiGui;
    private String nguoiNhan;
    private Long doiTuongId;
    private String doiTuongType;
    private String linkAction;
    private String icon;
    private String mauSac;
    private LocalDateTime createdAt;
    private String timeAgo;
    
    // Constructor từ entity
    public ThongBaoDTO(ThongBao thongBao) {
        this.id = thongBao.getId();
        this.tieuDe = thongBao.getTieuDe();
        this.noiDung = thongBao.getNoiDung();
        this.loaiThongBao = thongBao.getLoaiThongBao() != null ? thongBao.getLoaiThongBao().name() : null;
        this.trangThai = thongBao.getTrangThai() != null ? thongBao.getTrangThai().name() : null;
        this.nguoiGui = thongBao.getNguoiGui();
        this.nguoiNhan = thongBao.getNguoiNhan();
        this.doiTuongId = thongBao.getDoiTuongId();
        this.doiTuongType = thongBao.getDoiTuongType();
        this.linkAction = thongBao.getLinkAction();
        this.icon = thongBao.getIcon();
        this.mauSac = thongBao.getMauSac();
        this.createdAt = thongBao.getCreatedAt();
        this.timeAgo = thongBao.getTimeAgo();
    }
    
    // Default constructor
    public ThongBaoDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTieuDe() {
        return tieuDe;
    }
    
    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }
    
    public String getNoiDung() {
        return noiDung;
    }
    
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
    
    public String getLoaiThongBao() {
        return loaiThongBao;
    }
    
    public void setLoaiThongBao(String loaiThongBao) {
        this.loaiThongBao = loaiThongBao;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public String getNguoiGui() {
        return nguoiGui;
    }
    
    public void setNguoiGui(String nguoiGui) {
        this.nguoiGui = nguoiGui;
    }
    
    public String getNguoiNhan() {
        return nguoiNhan;
    }
    
    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }
    
    public Long getDoiTuongId() {
        return doiTuongId;
    }
    
    public void setDoiTuongId(Long doiTuongId) {
        this.doiTuongId = doiTuongId;
    }
    
    public String getDoiTuongType() {
        return doiTuongType;
    }
    
    public void setDoiTuongType(String doiTuongType) {
        this.doiTuongType = doiTuongType;
    }
    
    public String getLinkAction() {
        return linkAction;
    }
    
    public void setLinkAction(String linkAction) {
        this.linkAction = linkAction;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getMauSac() {
        return mauSac;
    }
    
    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}