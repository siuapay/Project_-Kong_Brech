package com.branch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chap_su")
public class ChapSu extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;

    @Column(name = "chuc_vu", length = 50)
    @Enumerated(EnumType.STRING)
    private ChucVu chucVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_nganh_id")
    private BanNganh banNganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diem_nhom_id")
    private DiemNhom diemNhom;

    @Column(name = "dien_thoai", length = 20)
    @Pattern(regexp = "^[0-9]{10,11}$|^$", message = "Số điện thoại phải có 10-11 chữ số")
    private String dienThoai;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;

    @Column(name = "ngay_bat_dau_phuc_vu")
    private LocalDate ngayBatDauPhucVu;

    @Column(name = "nhiem_ky_bat_dau")
    private LocalDate nhiemKyBatDau;

    @Column(name = "nhiem_ky_ket_thuc")
    private LocalDate nhiemKyKetThuc;

    @Column(name = "tieu_su", length = 2000)
    private String tieuSu;

    @Column(name = "mo_ta_cong_viec", length = 1000)
    private String moTaCongViec;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @ElementCollection
    @CollectionTable(name = "chap_su_trach_nhiem", joinColumns = @JoinColumn(name = "chap_su_id"))
    @Column(name = "trach_nhiem", length = 255)
    private List<String> danhSachTrachNhiem = new ArrayList<>();

    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private TrangThaiChapSu trangThai = TrangThaiChapSu.DANG_NHIEM_VU;

    // Constructors
    public ChapSu() {
    }

    public ChapSu(String hoTen, ChucVu chucVu) {
        this.hoTen = hoTen;
        this.chucVu = chucVu;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    public BanNganh getBanNganh() {
        return banNganh;
    }

    public void setBanNganh(BanNganh banNganh) {
        this.banNganh = banNganh;
    }

    public DiemNhom getDiemNhom() {
        return diemNhom;
    }

    public void setDiemNhom(DiemNhom diemNhom) {
        this.diemNhom = diemNhom;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        // Clean phone number: remove spaces and non-numeric characters
        if (dienThoai != null && !dienThoai.trim().isEmpty()) {
            this.dienThoai = dienThoai.replaceAll("[^0-9]", "");
        } else {
            this.dienThoai = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDate getNgayBatDauPhucVu() {
        return ngayBatDauPhucVu;
    }

    public void setNgayBatDauPhucVu(LocalDate ngayBatDauPhucVu) {
        this.ngayBatDauPhucVu = ngayBatDauPhucVu;
    }

    public LocalDate getNhiemKyBatDau() {
        return nhiemKyBatDau;
    }

    public void setNhiemKyBatDau(LocalDate nhiemKyBatDau) {
        this.nhiemKyBatDau = nhiemKyBatDau;
    }

    public LocalDate getNhiemKyKetThuc() {
        return nhiemKyKetThuc;
    }

    public void setNhiemKyKetThuc(LocalDate nhiemKyKetThuc) {
        this.nhiemKyKetThuc = nhiemKyKetThuc;
    }

    public String getTieuSu() {
        return tieuSu;
    }

    public void setTieuSu(String tieuSu) {
        this.tieuSu = tieuSu;
    }

    public String getMoTaCongViec() {
        return moTaCongViec;
    }

    public void setMoTaCongViec(String moTaCongViec) {
        this.moTaCongViec = moTaCongViec;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getDanhSachTrachNhiem() {
        return danhSachTrachNhiem;
    }

    public void setDanhSachTrachNhiem(List<String> danhSachTrachNhiem) {
        this.danhSachTrachNhiem = danhSachTrachNhiem;
    }

    public TrangThaiChapSu getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiChapSu trangThai) {
        this.trangThai = trangThai;
    }

    // Helper methods
    public void addTrachNhiem(String trachNhiem) {
        if (!danhSachTrachNhiem.contains(trachNhiem)) {
            danhSachTrachNhiem.add(trachNhiem);
        }
    }

    public void removeTrachNhiem(String trachNhiem) {
        danhSachTrachNhiem.remove(trachNhiem);
    }

    // Helper method to get nhiệm kỳ as string
    public String getNhiemKy() {
        if (nhiemKyBatDau != null && nhiemKyKetThuc != null) {
            return nhiemKyBatDau.getYear() + " - " + nhiemKyKetThuc.getYear();
        } else if (nhiemKyBatDau != null) {
            return "Từ " + nhiemKyBatDau.getYear();
        }
        return null;
    }

    public int getTuoi() {
        if (ngaySinh != null) {
            return LocalDate.now().getYear() - ngaySinh.getYear();
        }
        return 0;
    }

    // Enums
    public enum ChucVu {
        QUAN_NHIEM("Quản nhiệm"),
        PHU_TA_QUAN_NHIEM("Phụ tá quản nhiệm"),
        THU_KY_1("Thư ký 1"),
        THU_KY_2("Thư ký 2"),
        THU_QUY_1("Thủ quỹ 1"),
        THU_QUY_2("Thủ quỹ 2"),
        UY_VIEN("Ủy viên"),
        CHAP_SU("Chấp sự"),
        CONG_SU("Cộng sự");

        private final String displayName;

        ChucVu(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TrangThaiChapSu {
        DANG_NHIEM_VU, HET_NHIEM_KY, TAM_NGHI, CHUYEN_DI
    }
}