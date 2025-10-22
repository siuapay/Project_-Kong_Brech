package com.branch.demo.repository;

import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.TaiChinhGiaoDich.LoaiGiaoDich;
import com.branch.demo.domain.TaiChinhDanhMuc;
import com.branch.demo.domain.NhanSu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaiChinhGiaoDichRepository extends JpaRepository<TaiChinhGiaoDich, Long> {
    
    // Tìm theo loại giao dịch
    List<TaiChinhGiaoDich> findByLoai(LoaiGiaoDich loai);
    Page<TaiChinhGiaoDich> findByLoai(LoaiGiaoDich loai, Pageable pageable);
    
    // Tìm theo năm
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE EXTRACT(YEAR FROM gd.thoiGian) = :nam ORDER BY gd.thoiGian DESC")
    List<TaiChinhGiaoDich> findByNam(@Param("nam") Integer nam);
    
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE EXTRACT(YEAR FROM gd.thoiGian) = :nam ORDER BY gd.thoiGian DESC")
    Page<TaiChinhGiaoDich> findByNam(@Param("nam") Integer nam, Pageable pageable);
    
    // Tìm theo danh mục
    List<TaiChinhGiaoDich> findByDanhMuc(TaiChinhDanhMuc danhMuc);
    Page<TaiChinhGiaoDich> findByDanhMuc(TaiChinhDanhMuc danhMuc, Pageable pageable);
    
    // Tìm theo người phụ trách
    List<TaiChinhGiaoDich> findByNguoiPhuTrach(NhanSu nguoiPhuTrach);
    Page<TaiChinhGiaoDich> findByNguoiPhuTrach(NhanSu nguoiPhuTrach, Pageable pageable);
    
    // Tìm theo khoảng thời gian
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE gd.thoiGian BETWEEN :tuNgay AND :denNgay ORDER BY gd.thoiGian DESC")
    List<TaiChinhGiaoDich> findByThoiGianBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
    
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE gd.thoiGian BETWEEN :tuNgay AND :denNgay ORDER BY gd.thoiGian DESC")
    Page<TaiChinhGiaoDich> findByThoiGianBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay, Pageable pageable);
    
    // Tìm kiếm nâng cao với nhiều điều kiện
    @Query(value = "SELECT gd FROM TaiChinhGiaoDich gd WHERE " +
           "(:loai IS NULL OR gd.loai = :loai) AND " +
           "(:danhMucId IS NULL OR gd.danhMuc.id = :danhMucId) AND " +
           "(:nguoiPhuTrachId IS NULL OR gd.nguoiPhuTrach.id = :nguoiPhuTrachId) AND " +
           "(:noiDung IS NULL OR LOWER(gd.noiDung) LIKE LOWER(CONCAT('%', :noiDung, '%'))) AND " +
           "(:tuNgay IS NULL OR gd.thoiGian >= :tuNgay) AND " +
           "(:denNgay IS NULL OR gd.thoiGian <= :denNgay) AND " +
           "(:soTienMin IS NULL OR gd.soTien >= :soTienMin) AND " +
           "(:soTienMax IS NULL OR gd.soTien <= :soTienMax) " +
           "ORDER BY gd.thoiGian DESC",
           countQuery = "SELECT COUNT(gd) FROM TaiChinhGiaoDich gd WHERE " +
           "(:loai IS NULL OR gd.loai = :loai) AND " +
           "(:danhMucId IS NULL OR gd.danhMuc.id = :danhMucId) AND " +
           "(:nguoiPhuTrachId IS NULL OR gd.nguoiPhuTrach.id = :nguoiPhuTrachId) AND " +
           "(:noiDung IS NULL OR LOWER(gd.noiDung) LIKE LOWER(CONCAT('%', :noiDung, '%'))) AND " +
           "(:tuNgay IS NULL OR gd.thoiGian >= :tuNgay) AND " +
           "(:denNgay IS NULL OR gd.thoiGian <= :denNgay) AND " +
           "(:soTienMin IS NULL OR gd.soTien >= :soTienMin) AND " +
           "(:soTienMax IS NULL OR gd.soTien <= :soTienMax)")
    Page<TaiChinhGiaoDich> findByMultipleConditions(
        @Param("loai") LoaiGiaoDich loai,
        @Param("danhMucId") Long danhMucId,
        @Param("nguoiPhuTrachId") Long nguoiPhuTrachId,
        @Param("noiDung") String noiDung,
        @Param("tuNgay") LocalDateTime tuNgay,
        @Param("denNgay") LocalDateTime denNgay,
        @Param("soTienMin") BigDecimal soTienMin,
        @Param("soTienMax") BigDecimal soTienMax,
        Pageable pageable);
    
    // Find all records without pagination (for export)
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE " +
           "(:loai IS NULL OR gd.loai = :loai) AND " +
           "(:danhMucId IS NULL OR gd.danhMuc.id = :danhMucId) AND " +
           "(:nguoiPhuTrachId IS NULL OR gd.nguoiPhuTrach.id = :nguoiPhuTrachId) AND " +
           "(:noiDung IS NULL OR LOWER(gd.noiDung) LIKE LOWER(CONCAT('%', :noiDung, '%'))) AND " +
           "(:tuNgay IS NULL OR gd.thoiGian >= :tuNgay) AND " +
           "(:denNgay IS NULL OR gd.thoiGian <= :denNgay) AND " +
           "(:soTienMin IS NULL OR gd.soTien >= :soTienMin) AND " +
           "(:soTienMax IS NULL OR gd.soTien <= :soTienMax) " +
           "ORDER BY gd.thoiGian DESC")
    List<TaiChinhGiaoDich> findAllByMultipleConditions(
        @Param("loai") LoaiGiaoDich loai,
        @Param("danhMucId") Long danhMucId,
        @Param("nguoiPhuTrachId") Long nguoiPhuTrachId,
        @Param("noiDung") String noiDung,
        @Param("tuNgay") LocalDateTime tuNgay,
        @Param("denNgay") LocalDateTime denNgay,
        @Param("soTienMin") BigDecimal soTienMin,
        @Param("soTienMax") BigDecimal soTienMax);
    
    // Tính tổng thu theo năm
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE " +
           "EXTRACT(YEAR FROM gd.thoiGian) = :nam AND gd.loai = 'THU'")
    BigDecimal getTongThuByNam(@Param("nam") Integer nam);
    
    // Tính tổng chi theo năm
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE " +
           "EXTRACT(YEAR FROM gd.thoiGian) = :nam AND gd.loai = 'CHI'")
    BigDecimal getTongChiByNam(@Param("nam") Integer nam);
    
    // Tính tổng theo danh mục và năm
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE " +
           "EXTRACT(YEAR FROM gd.thoiGian) = :nam AND gd.danhMuc.id = :danhMucId")
    BigDecimal getTongByDanhMucAndNam(@Param("danhMucId") Long danhMucId, @Param("nam") Integer nam);
    
    // Tính tổng theo danh mục, loại và năm
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE " +
           "EXTRACT(YEAR FROM gd.thoiGian) = :nam AND gd.danhMuc.id = :danhMucId AND gd.loai = :loai")
    BigDecimal getTongByDanhMucLoaiAndNam(@Param("danhMucId") Long danhMucId, @Param("loai") LoaiGiaoDich loai, @Param("nam") Integer nam);
    
    // Lấy các năm có giao dịch
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM gd.thoiGian) FROM TaiChinhGiaoDich gd ORDER BY EXTRACT(YEAR FROM gd.thoiGian) DESC")
    List<Integer> findDistinctYears();
    
    // Đếm giao dịch theo loại và năm
    @Query("SELECT COUNT(gd) FROM TaiChinhGiaoDich gd WHERE " +
           "EXTRACT(YEAR FROM gd.thoiGian) = :nam AND gd.loai = :loai")
    long countByLoaiAndNam(@Param("loai") LoaiGiaoDich loai, @Param("nam") Integer nam);
    
    // Lấy giao dịch gần nhất
    @Query("SELECT gd FROM TaiChinhGiaoDich gd ORDER BY gd.thoiGian DESC")
    Page<TaiChinhGiaoDich> findRecentTransactions(Pageable pageable);
    
    // Lấy top giao dịch theo số tiền
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE gd.loai = :loai ORDER BY gd.soTien DESC")
    Page<TaiChinhGiaoDich> findTopByLoaiOrderBySoTienDesc(@Param("loai") LoaiGiaoDich loai, Pageable pageable);
    
    // Thống kê theo tháng trong năm
    @Query("SELECT EXTRACT(MONTH FROM gd.thoiGian) as thang, " +
           "COALESCE(SUM(CASE WHEN gd.loai = 'THU' THEN gd.soTien ELSE 0 END), 0) as tongThu, " +
           "COALESCE(SUM(CASE WHEN gd.loai = 'CHI' THEN gd.soTien ELSE 0 END), 0) as tongChi " +
           "FROM TaiChinhGiaoDich gd WHERE EXTRACT(YEAR FROM gd.thoiGian) = :nam " +
           "GROUP BY EXTRACT(MONTH FROM gd.thoiGian) ORDER BY EXTRACT(MONTH FROM gd.thoiGian)")
    List<Object[]> getThongKeTheoThang(@Param("nam") Integer nam);
    
    // Thống kê theo danh mục trong năm (chỉ lấy danh mục có giao dịch)
    @Query("SELECT dm.tenDanhMuc, gd.loai, SUM(gd.soTien), COUNT(gd.id) " +
           "FROM TaiChinhGiaoDich gd JOIN gd.danhMuc dm " +
           "WHERE EXTRACT(YEAR FROM gd.thoiGian) = :nam " +
           "GROUP BY dm.tenDanhMuc, gd.loai " +
           "ORDER BY gd.loai, SUM(gd.soTien) DESC")
    List<Object[]> getThongKeTheoDanhMuc(@Param("nam") Integer nam);
    
    // Tìm giao dịch theo khoảng số tiền
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE " +
           "gd.soTien BETWEEN :soTienMin AND :soTienMax " +
           "ORDER BY gd.soTien DESC")
    Page<TaiChinhGiaoDich> findBySoTienBetween(@Param("soTienMin") BigDecimal soTienMin, @Param("soTienMax") BigDecimal soTienMax, Pageable pageable);
    
    // Tìm giao dịch theo nội dung
    Page<TaiChinhGiaoDich> findByNoiDungContainingIgnoreCase(String noiDung, Pageable pageable);
    
    // Lấy tổng số tiền theo loại
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE gd.loai = :loai")
    BigDecimal getTongTienByLoai(@Param("loai") LoaiGiaoDich loai);
    
    // Lấy giao dịch lớn nhất theo loại
    @Query("SELECT gd FROM TaiChinhGiaoDich gd WHERE gd.loai = :loai AND gd.soTien = " +
           "(SELECT MAX(gd2.soTien) FROM TaiChinhGiaoDich gd2 WHERE gd2.loai = :loai)")
    List<TaiChinhGiaoDich> findMaxTransactionByLoai(@Param("loai") LoaiGiaoDich loai);
    
    // Đếm tổng số giao dịch
    @Query("SELECT COUNT(gd) FROM TaiChinhGiaoDich gd")
    long getTotalTransactionCount();
    
    // Đếm giao dịch theo năm
    @Query("SELECT COUNT(gd) FROM TaiChinhGiaoDich gd WHERE EXTRACT(YEAR FROM gd.thoiGian) = :nam")
    long countByNam(@Param("nam") Integer nam);
    
    // Đếm giao dịch theo danh mục
    @Query("SELECT COUNT(gd) FROM TaiChinhGiaoDich gd WHERE gd.danhMuc.id = :danhMucId")
    long countByDanhMucId(@Param("danhMucId") Long danhMucId);
    
    // Tính tổng theo loại giao dịch và khoảng ngày
    @Query("SELECT COALESCE(SUM(gd.soTien), 0) FROM TaiChinhGiaoDich gd WHERE " +
           "gd.loai = :loai AND DATE(gd.thoiGian) BETWEEN :tuNgay AND :denNgay")
    BigDecimal sumByLoaiGiaoDichAndNgayGiaoDichBetween(@Param("loai") LoaiGiaoDich loai, 
                                                       @Param("tuNgay") java.time.LocalDate tuNgay, 
                                                       @Param("denNgay") java.time.LocalDate denNgay);
}