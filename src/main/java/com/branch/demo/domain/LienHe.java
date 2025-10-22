package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lien_he")
public class LienHe extends BaseAuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "chu_de", length = 255)
    private String chuDe;
    
    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;
    
    @Column(name = "loai_lien_he")
    @Enumerated(EnumType.STRING)
    private LoaiLienHe loaiLienHe;
    
    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiLienHe trangThai = TrangThaiLienHe.CHUA_XU_LY;
    
    // Thông tin xử lý bởi MODERATOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_xu_ly_id")
    private Account moderatorXuLy;
    
    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;
    
    @Column(name = "ghi_chu_xu_ly", columnDefinition = "TEXT")
    private String ghiChuXuLy;
    
    // Thông tin báo cáo vi phạm
    @Column(name = "co_vi_pham")
    private Boolean coViPham = false;
    
    @Column(name = "ly_do_vi_pham", columnDefinition = "TEXT")
    private String lyDoViPham;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_bao_cao_id")
    private Account moderatorBaoCao;
    
    @Column(name = "ngay_bao_cao")
    private LocalDateTime ngayBaoCao;
    
    // Thông tin xử lý vi phạm bởi ADMIN
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_xu_ly_id")
    private Account adminXuLy;
    
    @Column(name = "ngay_admin_xu_ly")
    private LocalDateTime ngayAdminXuLy;
    
    @Column(name = "quyet_dinh_admin")
    @Enumerated(EnumType.STRING)
    private QuyetDinhAdmin quyetDinhAdmin;
    
    // Constructors
    public LienHe() {}
    
    public LienHe(String hoTen, String email, String chuDe, String noiDung) {
        this.hoTen = hoTen;
        this.email = email;
        this.chuDe = chuDe;
        this.noiDung = noiDung;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    
    public String getChuDe() { return chuDe; }
    public void setChuDe(String chuDe) { this.chuDe = chuDe; }
    
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public LoaiLienHe getLoaiLienHe() { return loaiLienHe; }
    public void setLoaiLienHe(LoaiLienHe loaiLienHe) { this.loaiLienHe = loaiLienHe; }
    
    public TrangThaiLienHe getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLienHe trangThai) { this.trangThai = trangThai; }
    
    public Account getModeratorXuLy() { return moderatorXuLy; }
    public void setModeratorXuLy(Account moderatorXuLy) { this.moderatorXuLy = moderatorXuLy; }
    
    public LocalDateTime getNgayXuLy() { return ngayXuLy; }
    public void setNgayXuLy(LocalDateTime ngayXuLy) { this.ngayXuLy = ngayXuLy; }
    
    public String getGhiChuXuLy() { return ghiChuXuLy; }
    public void setGhiChuXuLy(String ghiChuXuLy) { this.ghiChuXuLy = ghiChuXuLy; }
    
    public Boolean getCoViPham() { return coViPham; }
    public void setCoViPham(Boolean coViPham) { this.coViPham = coViPham; }
    
    public String getLyDoViPham() { return lyDoViPham; }
    public void setLyDoViPham(String lyDoViPham) { this.lyDoViPham = lyDoViPham; }
    
    public Account getModeratorBaoCao() { return moderatorBaoCao; }
    public void setModeratorBaoCao(Account moderatorBaoCao) { this.moderatorBaoCao = moderatorBaoCao; }
    
    public LocalDateTime getNgayBaoCao() { return ngayBaoCao; }
    public void setNgayBaoCao(LocalDateTime ngayBaoCao) { this.ngayBaoCao = ngayBaoCao; }
    
    public Account getAdminXuLy() { return adminXuLy; }
    public void setAdminXuLy(Account adminXuLy) { this.adminXuLy = adminXuLy; }
    
    public LocalDateTime getNgayAdminXuLy() { return ngayAdminXuLy; }
    public void setNgayAdminXuLy(LocalDateTime ngayAdminXuLy) { this.ngayAdminXuLy = ngayAdminXuLy; }
    
    public QuyetDinhAdmin getQuyetDinhAdmin() { return quyetDinhAdmin; }
    public void setQuyetDinhAdmin(QuyetDinhAdmin quyetDinhAdmin) { this.quyetDinhAdmin = quyetDinhAdmin; }
    
    // Helper methods
    public void xacNhanXuLy(Account moderator, String ghiChu) {
        this.moderatorXuLy = moderator;
        this.ngayXuLy = LocalDateTime.now();
        this.ghiChuXuLy = ghiChu;
        this.trangThai = TrangThaiLienHe.DA_XU_LY;
    }
    
    public void baoCaoViPham(Account moderator, String lyDo) {
        this.moderatorBaoCao = moderator;
        this.lyDoViPham = lyDo;
        this.ngayBaoCao = LocalDateTime.now();
        this.coViPham = true;
        this.trangThai = TrangThaiLienHe.CHO_ADMIN_XU_LY;
    }
    
    public void adminXuLyViPham(Account admin, QuyetDinhAdmin quyetDinh) {
        this.adminXuLy = admin;
        this.quyetDinhAdmin = quyetDinh;
        this.ngayAdminXuLy = LocalDateTime.now();
        
        if (quyetDinh == QuyetDinhAdmin.XOA_LIEN_HE) {
            this.trangThai = TrangThaiLienHe.DA_XOA;
        } else if (quyetDinh == QuyetDinhAdmin.GIU_LAI) {
            this.trangThai = TrangThaiLienHe.DA_XU_LY;
            this.coViPham = false; // Reset vi phạm
        }
    }
    
    public boolean isDaXuLy() {
        return trangThai == TrangThaiLienHe.DA_XU_LY;
    }
    
    public boolean isChoAdminXuLy() {
        return trangThai == TrangThaiLienHe.CHO_ADMIN_XU_LY;
    }
    
    public String getTenNguoiXuLy() {
        if (moderatorXuLy != null) {
            if (moderatorXuLy.getNhanSu() != null) {
                return moderatorXuLy.getNhanSu().getHoTen();
            } else if (moderatorXuLy.getChapSu() != null) {
                return moderatorXuLy.getChapSu().getHoTen();
            }
        }
        return null;
    }
    
    // Enums
    public enum LoaiLienHe {
        GOP_Y("Góp ý"), 
        KHIEU_NAI("Khiếu nại"), 
        DE_XUAT("Đề xuất"), 
        QUANG_CAO("Quảng cáo"), 
        HOP_TAC("Hợp tác"), 
        KHAC("Khác");
        
        private final String displayName;
        
        LoaiLienHe(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TrangThaiLienHe {
        CHUA_XU_LY("Chưa xử lý"),
        DANG_XU_LY("Đang xử lý"), 
        DA_XU_LY("Đã xử lý"),
        CHO_ADMIN_XU_LY("Chờ Admin xử lý"),
        DA_XOA("Đã xóa");
        
        private final String displayName;
        
        TrangThaiLienHe(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum QuyetDinhAdmin {
        XOA_LIEN_HE("Xóa liên hệ"),
        GIU_LAI("Giữ lại");
        
        private final String displayName;
        
        QuyetDinhAdmin(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}