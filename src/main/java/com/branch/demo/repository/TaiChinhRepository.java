package com.branch.demo.repository;

import com.branch.demo.domain.TaiChinh;
import com.branch.demo.domain.TaiChinh.LoaiGiaoDich;
import com.branch.demo.domain.TaiChinh.TrangThaiGiaoDich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaiChinhRepository extends JpaRepository<TaiChinh, Long> {
    
    // Tìm theo loại giao dịch
    List<TaiChinh> findByLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    
    // Tìm theo trạng thái
    List<TaiChinh> findByTrangThai(TrangThaiGiaoDich trangThai);
    
    // Tìm theo ngày giao dịch
    List<TaiChinh> findByNgayGiaoDich(LocalDate ngayGiaoDich);
    
    // Tìm theo khoảng thời gian
    List<TaiChinh> findByNgayGiaoDichBetween(LocalDate tuNgay, LocalDate denNgay);
    
    // Tìm theo danh mục
    List<TaiChinh> findByDanhMucContainingIgnoreCase(String danhMuc);
    
    // Tìm theo người thực hiện
    List<TaiChinh> findByNguoiThucHienContainingIgnoreCase(String nguoiThucHien);
    
    // Tính tổng thu theo khoảng thời gian
    @Query("SELECT COALESCE(SUM(tc.soTien), 0) FROM TaiChinh tc WHERE " +
           "tc.loaiGiaoDich = :loaiGiaoDich AND " +
           "tc.trangThai = :trangThai AND " +
           "tc.ngayGiaoDich BETWEEN :tuNgay AND :denNgay")
    BigDecimal tinhTongTheoLoaiVaThoiGian(
        @Param("loaiGiaoDich") LoaiGiaoDich loaiGiaoDich,
        @Param("trangThai") TrangThaiGiaoDich trangThai,
        @Param("tuNgay") LocalDate tuNgay,
        @Param("denNgay") LocalDate denNgay);
    
    // Tính tổng dâng hiến theo tháng
    @Query("SELECT COALESCE(SUM(tc.soTien), 0) FROM TaiChinh tc WHERE " +
           "tc.loaiGiaoDich = 'THU' AND " +
           "tc.trangThai = :trangThai AND " +
           "LOWER(tc.danhMuc) LIKE LOWER('%dâng hiến%') AND " +
           "YEAR(tc.ngayGiaoDich) = :nam AND " +
           "MONTH(tc.ngayGiaoDich) = :thang")
    BigDecimal tinhTongDangHienTheoThang(
        @Param("trangThai") TrangThaiGiaoDich trangThai,
        @Param("nam") int nam,
        @Param("thang") int thang);
    
    // Báo cáo tài chính theo tháng
    @Query("SELECT " +
           "YEAR(tc.ngayGiaoDich) as nam, " +
           "MONTH(tc.ngayGiaoDich) as thang, " +
           "tc.loaiGiaoDich, " +
           "SUM(tc.soTien) as tongTien " +
           "FROM TaiChinh tc " +
           "WHERE tc.trangThai = :trangThai " +
           "GROUP BY YEAR(tc.ngayGiaoDich), MONTH(tc.ngayGiaoDich), tc.loaiGiaoDich " +
           "ORDER BY YEAR(tc.ngayGiaoDich) DESC, MONTH(tc.ngayGiaoDich) DESC")
    List<Object[]> getBaoCaoTheoThang(@Param("trangThai") TrangThaiGiaoDich trangThai);
    
    // Thống kê theo danh mục
    @Query("SELECT tc.danhMuc, tc.loaiGiaoDich, COUNT(tc), SUM(tc.soTien) " +
           "FROM TaiChinh tc " +
           "WHERE tc.trangThai = :trangThai AND " +
           "tc.ngayGiaoDich BETWEEN :tuNgay AND :denNgay " +
           "GROUP BY tc.danhMuc, tc.loaiGiaoDich " +
           "ORDER BY SUM(tc.soTien) DESC")
    List<Object[]> getThongKeTheoDanhMuc(
        @Param("trangThai") TrangThaiGiaoDich trangThai,
        @Param("tuNgay") LocalDate tuNgay,
        @Param("denNgay") LocalDate denNgay);
    
    // Tìm với phân trang
    Page<TaiChinh> findByMoTaContainingIgnoreCaseOrDanhMucContainingIgnoreCase(
        String moTa, String danhMuc, Pageable pageable);
    
    // Tìm theo nhiều điều kiện
    @Query("SELECT tc FROM TaiChinh tc WHERE " +
           "(:loaiGiaoDich IS NULL OR tc.loaiGiaoDich = :loaiGiaoDich) AND " +
           "(:danhMuc IS NULL OR LOWER(tc.danhMuc) LIKE LOWER(CONCAT('%', :danhMuc, '%'))) AND " +
           "(:trangThai IS NULL OR tc.trangThai = :trangThai) AND " +
           "(:tuNgay IS NULL OR tc.ngayGiaoDich >= :tuNgay) AND " +
           "(:denNgay IS NULL OR tc.ngayGiaoDich <= :denNgay) AND " +
           "(:soTienMin IS NULL OR tc.soTien >= :soTienMin) AND " +
           "(:soTienMax IS NULL OR tc.soTien <= :soTienMax)")
    Page<TaiChinh> findByMultipleConditions(
        @Param("loaiGiaoDich") LoaiGiaoDich loaiGiaoDich,
        @Param("danhMuc") String danhMuc,
        @Param("trangThai") TrangThaiGiaoDich trangThai,
        @Param("tuNgay") LocalDate tuNgay,
        @Param("denNgay") LocalDate denNgay,
        @Param("soTienMin") BigDecimal soTienMin,
        @Param("soTienMax") BigDecimal soTienMax,
        Pageable pageable);
}