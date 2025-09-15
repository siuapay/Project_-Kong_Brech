package com.branch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tai_chinh_nam")
public class TaiChinhNam {

    @Id
    @Column(name = "nam")
    @NotNull(message = "Năm không được để trống")
    private Integer nam;

    @Column(name = "tong_thu", precision = 15, scale = 2)
    private BigDecimal tongThu = BigDecimal.ZERO;

    @Column(name = "tong_chi", precision = 15, scale = 2)
    private BigDecimal tongChi = BigDecimal.ZERO;

    @Column(name = "so_du", precision = 15, scale = 2)
    private BigDecimal soDu = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateSoDu();
    }

    // Constructors
    public TaiChinhNam() {
    }

    public TaiChinhNam(Integer nam) {
        this.nam = nam;
        this.tongThu = BigDecimal.ZERO;
        this.tongChi = BigDecimal.ZERO;
        this.soDu = BigDecimal.ZERO;
    }

    public TaiChinhNam(Integer nam, BigDecimal tongThu, BigDecimal tongChi) {
        this.nam = nam;
        this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
        this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
        calculateSoDu();
    }

    // Getters and Setters
    public Integer getNam() {
        return nam;
    }

    public void setNam(Integer nam) {
        this.nam = nam;
    }

    public BigDecimal getTongThu() {
        return tongThu;
    }

    public void setTongThu(BigDecimal tongThu) {
        this.tongThu = tongThu != null ? tongThu : BigDecimal.ZERO;
        calculateSoDu();
    }

    public BigDecimal getTongChi() {
        return tongChi;
    }

    public void setTongChi(BigDecimal tongChi) {
        this.tongChi = tongChi != null ? tongChi : BigDecimal.ZERO;
        calculateSoDu();
    }

    public BigDecimal getSoDu() {
        return soDu;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods for transaction operations
    public void addThu(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.tongThu = this.tongThu.add(amount);
            calculateSoDu();
        }
    }

    public void addChi(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.tongChi = this.tongChi.add(amount);
            calculateSoDu();
        }
    }

    public void subtractThu(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.tongThu = this.tongThu.subtract(amount);
            if (this.tongThu.compareTo(BigDecimal.ZERO) < 0) {
                this.tongThu = BigDecimal.ZERO;
            }
            calculateSoDu();
        }
    }

    public void subtractChi(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.tongChi = this.tongChi.subtract(amount);
            if (this.tongChi.compareTo(BigDecimal.ZERO) < 0) {
                this.tongChi = BigDecimal.ZERO;
            }
            calculateSoDu();
        }
    }

    public void updateThu(BigDecimal oldAmount, BigDecimal newAmount) {
        if (oldAmount != null) {
            subtractThu(oldAmount);
        }
        if (newAmount != null) {
            addThu(newAmount);
        }
    }

    public void updateChi(BigDecimal oldAmount, BigDecimal newAmount) {
        if (oldAmount != null) {
            subtractChi(oldAmount);
        }
        if (newAmount != null) {
            addChi(newAmount);
        }
    }

    private void calculateSoDu() {
        if (tongThu == null)
            tongThu = BigDecimal.ZERO;
        if (tongChi == null)
            tongChi = BigDecimal.ZERO;
        this.soDu = tongThu.subtract(tongChi);
    }

    // Status helper methods
    public boolean isLoi() {
        return soDu.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLo() {
        return soDu.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isHoaVon() {
        return soDu.compareTo(BigDecimal.ZERO) == 0;
    }

    public String getTrangThaiTaiChinh() {
        if (isLoi()) {
            return "Lời";
        } else if (isLo()) {
            return "Lỗ";
        } else {
            return "Hòa vốn";
        }
    }

    // Calculation methods for statistics
    public BigDecimal getTyLeThu() {
        BigDecimal tongTien = tongThu.add(tongChi);
        if (tongTien.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return tongThu.divide(tongTien, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal getTyLeChi() {
        BigDecimal tongTien = tongThu.add(tongChi);
        if (tongTien.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return tongChi.divide(tongTien, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public void reset() {
        this.tongThu = BigDecimal.ZERO;
        this.tongChi = BigDecimal.ZERO;
        this.soDu = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "TaiChinhNam{" +
                "nam=" + nam +
                ", tongThu=" + tongThu +
                ", tongChi=" + tongChi +
                ", soDu=" + soDu +
                ", trangThai='" + getTrangThaiTaiChinh() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaiChinhNam))
            return false;
        TaiChinhNam that = (TaiChinhNam) o;
        return nam != null && nam.equals(that.nam);
    }

    @Override
    public int hashCode() {
        return nam != null ? nam.hashCode() : 0;
    }
}