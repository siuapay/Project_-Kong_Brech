package com.branch.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bai_viet")
public class BaiViet extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tieu_de", nullable = false, length = 500)
    private String tieuDe;

    @Column(name = "slug", unique = true, nullable = false, length = 500)
    private String slug;

    @Column(name = "tom_tat", length = 1000)
    private String tomTat;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "noi_dung_rich", columnDefinition = "NVARCHAR(MAX)")
    private String noiDungRich; // Rich HTML content with embedded images/videos

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien; // Ảnh chính hiển thị thumbnail

    @ElementCollection
    @CollectionTable(name = "bai_viet_hinh_anh", joinColumns = @JoinColumn(name = "bai_viet_id"))
    @Column(name = "url_hinh_anh", length = 500)
    private List<String> danhSachHinhAnh = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "bai_viet_video", joinColumns = @JoinColumn(name = "bai_viet_id"))
    @Column(name = "url_video", length = 500)
    private List<String> danhSachVideo = new ArrayList<>();

    @Column(name = "trang_thai", length = 50)
    @Enumerated(EnumType.STRING)
    private TrangThaiBaiViet trangThai = TrangThaiBaiViet.NHAP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_id")
    private DanhMuc danhMuc;

    @Column(name = "tac_gia", length = 255)
    private String tacGia;

    @Column(name = "loai_tac_gia", length = 50)
    @Enumerated(EnumType.STRING)
    private LoaiTacGia loaiTacGia = LoaiTacGia.ADMIN;

    @Column(name = "luot_xem")
    private Long luotXem = 0L;

    @Column(name = "noi_bat")
    private Boolean noiBat = false;

    @Column(name = "cho_phep_binh_luan")
    private Boolean choPhepBinhLuan = true;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @Column(name = "ngay_xuat_ban")
    private LocalDateTime ngayXuatBan;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum TrangThaiBaiViet {
        NHAP("Nháp"),
        CHO_DUYET("Chờ duyệt"),
        DA_XUAT_BAN("Đã xuất bản"),
        TU_CHOI("Từ chối"),
        TAM_NGHI("Tạm nghỉ");

        private final String displayName;

        TrangThaiBaiViet(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum LoaiTacGia {
        MUC_SU("Mục sư"),
        THU_KY("Thư ký"),
        ADMIN("Admin"),
        NHAN_SU("Nhân sự"),
        KHACH("Khách"),
        EDITOR("Biên tập viên"),
        CONTRIBUTOR("Cộng tác viên"),
        GUEST("Khách mời");

        private final String displayName;

        LoaiTacGia(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Auto set ngayXuatBan when status is DA_XUAT_BAN
        if (trangThai == TrangThaiBaiViet.DA_XUAT_BAN && ngayXuatBan == null) {
            ngayXuatBan = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto set ngayXuatBan when status changes to DA_XUAT_BAN
        if (trangThai == TrangThaiBaiViet.DA_XUAT_BAN && ngayXuatBan == null) {
            ngayXuatBan = LocalDateTime.now();
        }
    }

    // Constructors
    public BaiViet() {
    }

    public BaiViet(String tieuDe, String slug) {
        this.tieuDe = tieuDe;
        this.slug = slug;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTomTat() {
        return tomTat;
    }

    public void setTomTat(String tomTat) {
        this.tomTat = tomTat;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNoiDungRich() {
        return noiDungRich;
    }

    public void setNoiDungRich(String noiDungRich) {
        this.noiDungRich = noiDungRich;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public List<String> getDanhSachHinhAnh() {
        return danhSachHinhAnh;
    }

    public void setDanhSachHinhAnh(List<String> danhSachHinhAnh) {
        this.danhSachHinhAnh = danhSachHinhAnh;
    }

    public List<String> getDanhSachVideo() {
        return danhSachVideo;
    }

    public void setDanhSachVideo(List<String> danhSachVideo) {
        this.danhSachVideo = danhSachVideo;
    }

    public TrangThaiBaiViet getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiBaiViet trangThai) {
        this.trangThai = trangThai;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public LoaiTacGia getLoaiTacGia() {
        return loaiTacGia;
    }

    public void setLoaiTacGia(LoaiTacGia loaiTacGia) {
        this.loaiTacGia = loaiTacGia;
    }

    public Long getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(Long luotXem) {
        this.luotXem = luotXem;
    }

    public Boolean getNoiBat() {
        return noiBat;
    }

    public void setNoiBat(Boolean noiBat) {
        this.noiBat = noiBat;
    }

    public Boolean getChoPhepBinhLuan() {
        return choPhepBinhLuan;
    }

    public void setChoPhepBinhLuan(Boolean choPhepBinhLuan) {
        this.choPhepBinhLuan = choPhepBinhLuan;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public LocalDateTime getNgayXuatBan() {
        return ngayXuatBan;
    }

    public void setNgayXuatBan(LocalDateTime ngayXuatBan) {
        this.ngayXuatBan = ngayXuatBan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Helper methods
    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void tangLuotXem() {
        this.luotXem = (this.luotXem == null ? 0 : this.luotXem) + 1;
    }

    public boolean isDaXuatBan() {
        return trangThai == TrangThaiBaiViet.DA_XUAT_BAN;
    }

    public void addHinhAnh(String urlHinhAnh) {
        if (danhSachHinhAnh == null) {
            danhSachHinhAnh = new ArrayList<>();
        }
        if (!danhSachHinhAnh.contains(urlHinhAnh)) {
            danhSachHinhAnh.add(urlHinhAnh);
        }
    }

    public void removeHinhAnh(String urlHinhAnh) {
        if (danhSachHinhAnh != null) {
            danhSachHinhAnh.remove(urlHinhAnh);
        }
    }

    public void addVideo(String urlVideo) {
        if (danhSachVideo == null) {
            danhSachVideo = new ArrayList<>();
        }
        if (!danhSachVideo.contains(urlVideo)) {
            danhSachVideo.add(urlVideo);
        }
    }

    public void removeVideo(String urlVideo) {
        if (danhSachVideo != null) {
            danhSachVideo.remove(urlVideo);
        }
    }

    public int getSoLuongHinhAnh() {
        return danhSachHinhAnh != null ? danhSachHinhAnh.size() : 0;
    }

    public int getSoLuongVideo() {
        return danhSachVideo != null ? danhSachVideo.size() : 0;
    }
}