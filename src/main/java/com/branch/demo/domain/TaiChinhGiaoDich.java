package com.branch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tai_chinh_giao_dich")
public class TaiChinhGiaoDich extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thoi_gian", nullable = false)
    @NotNull(message = "Thời gian giao dịch không được để trống")
    private LocalDateTime thoiGian;

    @Column(name = "loai", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Loại giao dịch không được để trống")
    private LoaiGiaoDich loai;

    @Column(name = "so_tien", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal soTien;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    @NotBlank(message = "Nội dung giao dịch không được để trống")
    @Size(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
    private String noiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_id", nullable = false)
    @NotNull(message = "Danh mục không được để trống")
    private TaiChinhDanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_phu_trach_id")
    private NhanSu nguoiPhuTrach;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Validate that transaction type matches category type
        if (danhMuc != null && loai != null) {
            // Temporarily disabled for debugging
            // validateLoaiWithDanhMuc();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Validate that transaction type matches category type
        if (danhMuc != null && loai != null) {
            // Temporarily disabled for debugging
            // validateLoaiWithDanhMuc();
        }
    }

    // Constructors
    public TaiChinhGiaoDich() {
    }

    public TaiChinhGiaoDich(LocalDateTime thoiGian, LoaiGiaoDich loai, BigDecimal soTien,
            String noiDung, TaiChinhDanhMuc danhMuc) {
        this.thoiGian = thoiGian;
        this.loai = loai;
        this.soTien = soTien;
        this.noiDung = noiDung;
        this.danhMuc = danhMuc;
    }

    public TaiChinhGiaoDich(LocalDateTime thoiGian, LoaiGiaoDich loai, BigDecimal soTien,
            String noiDung, TaiChinhDanhMuc danhMuc, NhanSu nguoiPhuTrach) {
        this.thoiGian = thoiGian;
        this.loai = loai;
        this.soTien = soTien;
        this.noiDung = noiDung;
        this.danhMuc = danhMuc;
        this.nguoiPhuTrach = nguoiPhuTrach;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public LoaiGiaoDich getLoai() {
        return loai;
    }

    public void setLoai(LoaiGiaoDich loai) {
        this.loai = loai;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public TaiChinhDanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(TaiChinhDanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public NhanSu getNguoiPhuTrach() {
        return nguoiPhuTrach;
    }

    public void setNguoiPhuTrach(NhanSu nguoiPhuTrach) {
        this.nguoiPhuTrach = nguoiPhuTrach;
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

    // Helper methods
    public boolean isThu() {
        return loai == LoaiGiaoDich.THU;
    }

    public boolean isChi() {
        return loai == LoaiGiaoDich.CHI;
    }

    public Integer getNam() {
        return thoiGian != null ? thoiGian.getYear() : null;
    }

    public String getLoaiDisplayName() {
        return loai.getDisplayName();
    }

    public String getTenDanhMuc() {
        return danhMuc != null ? danhMuc.getTenDanhMuc() : null;
    }

    public String getTenNguoiPhuTrach() {
        return nguoiPhuTrach != null ? nguoiPhuTrach.getHoTen() : null;
    }

    private void validateLoaiWithDanhMuc() {
        if (danhMuc != null && loai != null) {
            boolean isValid = (loai == LoaiGiaoDich.THU && danhMuc.getLoai() == TaiChinhDanhMuc.LoaiDanhMuc.THU) ||
                    (loai == LoaiGiaoDich.CHI && danhMuc.getLoai() == TaiChinhDanhMuc.LoaiDanhMuc.CHI);

            if (!isValid) {
                throw new IllegalArgumentException("Loại giao dịch không khớp với loại danh mục");
            }
        }
    }

    // Enum for transaction type
    public enum LoaiGiaoDich {
        THU("Thu"),
        CHI("Chi");

        private final String displayName;

        LoaiGiaoDich(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public String toString() {
        return "TaiChinhGiaoDich{" +
                "id=" + id +
                ", thoiGian=" + thoiGian +
                ", loai=" + loai +
                ", soTien=" + soTien +
                ", noiDung='" + noiDung + '\'' +
                ", danhMuc=" + (danhMuc != null ? danhMuc.getTenDanhMuc() : null) +
                ", nguoiPhuTrach=" + (nguoiPhuTrach != null ? nguoiPhuTrach.getHoTen() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaiChinhGiaoDich))
            return false;
        TaiChinhGiaoDich that = (TaiChinhGiaoDich) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}