package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
public class ThongBao extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tieuDe;
    
    @Column(columnDefinition = "TEXT")
    private String noiDung;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiThongBao loaiThongBao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiThongBao trangThai = TrangThaiThongBao.CHUA_DOC;
    
    // Người gửi thông báo
    @Column(nullable = false)
    private String nguoiGui;
    
    // Người nhận thông báo (admin username)
    @Column(nullable = false)
    private String nguoiNhan;
    
    // Liên kết đến đối tượng liên quan
    private Long doiTuongId; // ID của bài viết hoặc liên hệ
    
    @Column
    private String doiTuongType; // "BAI_VIET" hoặc "LIEN_HE"
    
    // URL để chuyển hướng khi click vào thông báo
    private String linkAction;
    
    @Column
    private String icon = "fas fa-bell"; // Icon mặc định
    
    @Column
    private String mauSac = "primary"; // Màu sắc thông báo
    
    public enum LoaiThongBao {
        BAI_VIET_MOI("Bài viết mới cần duyệt"),
        BAO_CAO_VI_PHAM("Báo cáo vi phạm"),
        BAI_VIET_CAP_NHAT("Bài viết được cập nhật"),
        LIEN_HE_MOI("Liên hệ mới"),
        HE_THONG("Thông báo hệ thống");
        
        private final String displayName;
        
        LoaiThongBao(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TrangThaiThongBao {
        CHUA_DOC("Chưa đọc"),
        DA_DOC("Đã đọc");
        
        private final String displayName;
        
        TrangThaiThongBao(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public ThongBao() {}
    
    public ThongBao(String tieuDe, String noiDung, LoaiThongBao loaiThongBao, 
                   String nguoiGui, String nguoiNhan) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loaiThongBao = loaiThongBao;
        this.nguoiGui = nguoiGui;
        this.nguoiNhan = nguoiNhan;
    }
    
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
    
    public LoaiThongBao getLoaiThongBao() {
        return loaiThongBao;
    }
    
    public void setLoaiThongBao(LoaiThongBao loaiThongBao) {
        this.loaiThongBao = loaiThongBao;
    }
    
    public TrangThaiThongBao getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(TrangThaiThongBao trangThai) {
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
    
    // Helper methods
    public boolean isChuaDoc() {
        return this.trangThai == TrangThaiThongBao.CHUA_DOC;
    }
    
    public void daDoc() {
        this.trangThai = TrangThaiThongBao.DA_DOC;
    }
    
    public String getTimeAgo() {
        if (this.getCreatedAt() == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime created = this.getCreatedAt();
        
        long minutes = java.time.Duration.between(created, now).toMinutes();
        
        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " giờ trước";
        
        long days = hours / 24;
        if (days < 7) return days + " ngày trước";
        
        long weeks = days / 7;
        return weeks + " tuần trước";
    }
}