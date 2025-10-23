package com.branch.demo.repository;

import com.branch.demo.domain.BanNganh;
import com.branch.demo.domain.TinHuu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinHuuRepository extends JpaRepository<TinHuu, Long> {

    // Tìm kiếm theo tên
    List<TinHuu> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm kiếm theo tên với phân trang
    Page<TinHuu> findByHoTenContainingIgnoreCase(String hoTen, Pageable pageable);
    
    // Đếm theo giới tính
    long countByGioiTinh(String gioiTinh);

    // Tìm kiếm đa điều kiện với phân trang
    Page<TinHuu> findByHoTenContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDienThoaiContaining(
            String hoTen, String email, String dienThoai, Pageable pageable);

    // Tìm theo nhóm
    List<TinHuu> findByNhomId(Long nhomId);

    // Tìm theo giới tính
    List<TinHuu> findByGioiTinh(String gioiTinh);

    // Tìm theo trạng thái
    List<TinHuu> findByTrangThai(String trangThai);

    // Tìm theo độ tuổi
    @Query("SELECT t FROM TinHuu t WHERE YEAR(CURRENT_DATE) - t.namSinh BETWEEN :minAge AND :maxAge")
    List<TinHuu> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    // Tìm theo email
    TinHuu findByEmail(String email);

    // Tìm theo số điện thoại
    TinHuu findByDienThoai(String dienThoai);

    // Tìm kiếm nâng cao
    @Query("SELECT t FROM TinHuu t WHERE " +
            "(:hoTen IS NULL OR LOWER(t.hoTen) LIKE LOWER(CONCAT('%', :hoTen, '%'))) AND " +
            "(:gioiTinh IS NULL OR t.gioiTinh = :gioiTinh) AND " +
            "(:trangThai IS NULL OR t.trangThai = :trangThai) AND " +
            "(:nhomId IS NULL OR t.nhom.id = :nhomId)")
    Page<TinHuu> findWithFilters(@Param("hoTen") String hoTen,
            @Param("gioiTinh") String gioiTinh,
            @Param("trangThai") String trangThai,
            @Param("nhomId") Long nhomId,
            Pageable pageable);

    // Thống kê theo giới tính
    @Query("SELECT t.gioiTinh, COUNT(t) FROM TinHuu t GROUP BY t.gioiTinh")
    List<Object[]> countByGioiTinh();

    // Thống kê theo trạng thái
    @Query("SELECT t.trangThai, COUNT(t) FROM TinHuu t GROUP BY t.trangThai")
    List<Object[]> countByTrangThai();

    // Thống kê theo nhóm
    @Query("SELECT n.tenNhom, COUNT(t) FROM TinHuu t JOIN t.nhom n GROUP BY n.tenNhom")
    List<Object[]> countByNhom();

    // Thống kê theo độ tuổi
    @Query("SELECT " +
            "CASE " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh < 18 THEN 'Dưới 18' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 18 AND 30 THEN '18-30' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 31 AND 50 THEN '31-50' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 51 AND 65 THEN '51-65' " +
            "ELSE 'Trên 65' END as ageGroup, " +
            "COUNT(t) " +
            "FROM TinHuu t " +
            "WHERE t.namSinh IS NOT NULL " +
            "GROUP BY " +
            "CASE " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh < 18 THEN 'Dưới 18' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 18 AND 30 THEN '18-30' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 31 AND 50 THEN '31-50' " +
            "WHEN YEAR(CURRENT_DATE) - t.namSinh BETWEEN 51 AND 65 THEN '51-65' " +
            "ELSE 'Trên 65' END")
    List<Object[]> countByAgeGroup();

    // Tìm tin hữu có sinh nhật trong tháng
    @Query("SELECT t FROM TinHuu t WHERE MONTH(t.ngayBaoTin) = :month")
    List<TinHuu> findBirthdayInMonth(@Param("month") int month);

    // Tìm tin hữu mới gia nhập trong khoảng thời gian
    @Query("SELECT t FROM TinHuu t WHERE t.ngayGiaNhap BETWEEN :startDate AND :endDate")
    List<TinHuu> findNewMembersInPeriod(@Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate);

    // Đếm tổng số tin hữu theo trạng thái
    @Query("SELECT COUNT(t) FROM TinHuu t WHERE t.trangThai = :trangThai")
    long countByTrangThaiString(@Param("trangThai") String trangThai);

    // ===== SOFT DELETE QUERIES =====
    
    // Tìm tất cả tin hữu chưa bị xóa (active)
    @Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NULL")
    List<TinHuu> findAllActive();
    
    // Tìm tất cả tin hữu đã bị xóa mềm
    @Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NOT NULL")
    List<TinHuu> findAllDeleted();
    
    // Tìm kiếm tin hữu active với phân trang
    @Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "t.dienThoai LIKE CONCAT('%', :search, '%'))")
    Page<TinHuu> findActiveWithSearch(@Param("search") String search, Pageable pageable);
    
    // Tìm kiếm tin hữu active với filter nâng cao
    @Query("SELECT t FROM TinHuu t LEFT JOIN t.nhom n LEFT JOIN n.diemNhom dn WHERE t.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "t.dienThoai LIKE CONCAT('%', :search, '%')) AND " +
           "(:trangThai IS NULL OR :trangThai = '' OR t.trangThai = :trangThai) AND " +
           "(:diemNhomId IS NULL OR dn.id = :diemNhomId) AND " +
           "(:nhomId IS NULL OR n.id = :nhomId) AND " +
           "(:fromDate IS NULL OR t.ngayBaoTin >= :fromDate) AND " +
           "(:toDate IS NULL OR t.ngayBaoTin <= :toDate)")
    Page<TinHuu> findActiveWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") String trangThai,
            @Param("diemNhomId") Long diemNhomId,
            @Param("nhomId") Long nhomId,
            @Param("fromDate") java.time.LocalDate fromDate,
            @Param("toDate") java.time.LocalDate toDate,
            Pageable pageable);
    
    // Tìm kiếm tin hữu đã xóa với phân trang
    @Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NOT NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "t.dienThoai LIKE CONCAT('%', :search, '%'))")
    Page<TinHuu> findDeletedWithSearch(@Param("search") String search, Pageable pageable);
    
    // Đếm tin hữu active
    @Query("SELECT COUNT(t) FROM TinHuu t WHERE t.deletedAt IS NULL")
    long countActive();
    
    // Đếm tin hữu đã xóa
    @Query("SELECT COUNT(t) FROM TinHuu t WHERE t.deletedAt IS NOT NULL")
    long countDeleted();
    
    // Tìm tin hữu chưa có nhóm và chưa bị xóa
    @Query("SELECT t FROM TinHuu t WHERE t.nhom IS NULL AND t.deletedAt IS NULL")
    List<TinHuu> findByNhomIsNullAndDeletedAtIsNull();
    
    // Đếm tín hữu theo ban ngành
    long countByBanNganh(BanNganh banNganh);
    
    // Tìm tín hữu theo ban ngành
    List<TinHuu> findByBanNganh(BanNganh banNganh);
}