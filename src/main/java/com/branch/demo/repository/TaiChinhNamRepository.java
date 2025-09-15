package com.branch.demo.repository;

import com.branch.demo.domain.TaiChinhNam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaiChinhNamRepository extends JpaRepository<TaiChinhNam, Integer> {
    
    // Tìm theo năm
    Optional<TaiChinhNam> findByNam(Integer nam);
    
    // Lấy tất cả năm, sắp xếp theo năm giảm dần
    List<TaiChinhNam> findAllByOrderByNamDesc();
    
    // Lấy tất cả năm, sắp xếp theo năm tăng dần
    List<TaiChinhNam> findAllByOrderByNamAsc();
    
    // Lấy các năm có dữ liệu (tổng thu hoặc tổng chi > 0)
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0 ORDER BY tn.nam DESC")
    List<TaiChinhNam> findNamWithData();
    
    // Lấy các năm có dữ liệu, sắp xếp tăng dần
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0 ORDER BY tn.nam ASC")
    List<TaiChinhNam> findNamWithDataAsc();
    
    // Tính tổng thu của tất cả các năm
    @Query("SELECT COALESCE(SUM(tn.tongThu), 0) FROM TaiChinhNam tn")
    BigDecimal getTotalThu();
    
    // Tính tổng chi của tất cả các năm
    @Query("SELECT COALESCE(SUM(tn.tongChi), 0) FROM TaiChinhNam tn")
    BigDecimal getTotalChi();
    
    // Tính tổng số dư của tất cả các năm
    @Query("SELECT COALESCE(SUM(tn.soDu), 0) FROM TaiChinhNam tn")
    BigDecimal getTotalSoDu();
    
    // Lấy năm có số dư cao nhất
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.soDu = (SELECT MAX(tn2.soDu) FROM TaiChinhNam tn2)")
    Optional<TaiChinhNam> findNamWithMaxSoDu();
    
    // Lấy năm có số dư thấp nhất
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.soDu = (SELECT MIN(tn2.soDu) FROM TaiChinhNam tn2)")
    Optional<TaiChinhNam> findNamWithMinSoDu();
    
    // Lấy năm có tổng thu cao nhất
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongThu = (SELECT MAX(tn2.tongThu) FROM TaiChinhNam tn2)")
    Optional<TaiChinhNam> findNamWithMaxThu();
    
    // Lấy năm có tổng chi cao nhất
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongChi = (SELECT MAX(tn2.tongChi) FROM TaiChinhNam tn2)")
    Optional<TaiChinhNam> findNamWithMaxChi();
    
    // Lấy các năm trong khoảng
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.nam BETWEEN :tuNam AND :denNam ORDER BY tn.nam DESC")
    List<TaiChinhNam> findByNamBetween(@Param("tuNam") Integer tuNam, @Param("denNam") Integer denNam);
    
    // Lấy các năm trong khoảng, sắp xếp tăng dần
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.nam BETWEEN :tuNam AND :denNam ORDER BY tn.nam ASC")
    List<TaiChinhNam> findByNamBetweenAsc(@Param("tuNam") Integer tuNam, @Param("denNam") Integer denNam);
    
    // Kiểm tra năm đã tồn tại
    boolean existsByNam(Integer nam);
    
    // Lấy 5 năm gần nhất có dữ liệu
    @Query(value = "SELECT * FROM tai_chinh_nam WHERE tong_thu > 0 OR tong_chi > 0 ORDER BY nam DESC LIMIT 5", nativeQuery = true)
    List<TaiChinhNam> findTop5RecentYearsWithData();
    
    // Lấy 10 năm gần nhất có dữ liệu
    @Query(value = "SELECT * FROM tai_chinh_nam WHERE tong_thu > 0 OR tong_chi > 0 ORDER BY nam DESC LIMIT 10", nativeQuery = true)
    List<TaiChinhNam> findTop10RecentYearsWithData();
    
    // Lấy các năm có lời (số dư > 0)
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.soDu > 0 ORDER BY tn.nam DESC")
    List<TaiChinhNam> findNamLoi();
    
    // Lấy các năm có lỗ (số dư < 0)
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.soDu < 0 ORDER BY tn.nam DESC")
    List<TaiChinhNam> findNamLo();
    
    // Lấy các năm hòa vốn (số dư = 0)
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.soDu = 0 ORDER BY tn.nam DESC")
    List<TaiChinhNam> findNamHoaVon();
    
    // Đếm số năm có dữ liệu
    @Query("SELECT COUNT(tn) FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0")
    long countNamWithData();
    
    // Đếm số năm có lời
    @Query("SELECT COUNT(tn) FROM TaiChinhNam tn WHERE tn.soDu > 0")
    long countNamLoi();
    
    // Đếm số năm có lỗ
    @Query("SELECT COUNT(tn) FROM TaiChinhNam tn WHERE tn.soDu < 0")
    long countNamLo();
    
    // Lấy năm đầu tiên có dữ liệu
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0 ORDER BY tn.nam ASC")
    Optional<TaiChinhNam> findFirstYearWithData();
    
    // Lấy năm cuối cùng có dữ liệu
    @Query("SELECT tn FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0 ORDER BY tn.nam DESC")
    Optional<TaiChinhNam> findLastYearWithData();
    
    // Tính trung bình thu theo năm
    @Query("SELECT AVG(tn.tongThu) FROM TaiChinhNam tn WHERE tn.tongThu > 0")
    BigDecimal getAverageThu();
    
    // Tính trung bình chi theo năm
    @Query("SELECT AVG(tn.tongChi) FROM TaiChinhNam tn WHERE tn.tongChi > 0")
    BigDecimal getAverageChi();
    
    // Tính trung bình số dư theo năm
    @Query("SELECT AVG(tn.soDu) FROM TaiChinhNam tn")
    BigDecimal getAverageSoDu();
    
    // Lấy thống kê tổng quan
    @Query("SELECT " +
           "COUNT(tn) as tongSoNam, " +
           "COALESCE(SUM(tn.tongThu), 0) as tongThu, " +
           "COALESCE(SUM(tn.tongChi), 0) as tongChi, " +
           "COALESCE(SUM(tn.soDu), 0) as tongSoDu, " +
           "COALESCE(AVG(tn.tongThu), 0) as trungBinhThu, " +
           "COALESCE(AVG(tn.tongChi), 0) as trungBinhChi, " +
           "COALESCE(AVG(tn.soDu), 0) as trungBinhSoDu " +
           "FROM TaiChinhNam tn WHERE tn.tongThu > 0 OR tn.tongChi > 0")
    List<Object[]> getThongKeTongQuan();
    
    // So sánh năm hiện tại với năm trước
    @Query("SELECT tn1.nam as namHienTai, tn1.tongThu as thuHienTai, tn1.tongChi as chiHienTai, tn1.soDu as soDuHienTai, " +
           "tn2.nam as namTruoc, tn2.tongThu as thuTruoc, tn2.tongChi as chiTruoc, tn2.soDu as soDuTruoc " +
           "FROM TaiChinhNam tn1 " +
           "LEFT JOIN TaiChinhNam tn2 ON tn2.nam = tn1.nam - 1 " +
           "WHERE tn1.nam = :nam")
    List<Object[]> soSanhVoiNamTruoc(@Param("nam") Integer nam);
}