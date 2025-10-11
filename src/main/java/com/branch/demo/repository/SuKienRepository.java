package com.branch.demo.repository;

import com.branch.demo.domain.SuKien;
import com.branch.demo.domain.LoaiSuKien;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuKienRepository extends JpaRepository<SuKien, Long> {
    
    // Basic soft delete queries - Order by created date descending (newest first)
    Page<SuKien> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    // Search with ordering by created date descending
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = false AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(sk.tenSuKien) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(sk.moTa) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(sk.diaDiem) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY sk.createdAt DESC")
    Page<SuKien> findByDeletedFalseAndSearchOrderByCreatedAtDesc(@Param("search") String search, Pageable pageable);
    
    Optional<SuKien> findByIdAndDeletedFalse(Long id);
    
    // Tìm theo tên sự kiện
    List<SuKien> findByTenSuKienContainingIgnoreCaseAndDeletedFalse(String tenSuKien);
    
    // Tìm theo loại sự kiện (entity)
    List<SuKien> findByLoaiSuKienAndDeletedFalse(LoaiSuKien loaiSuKien);
    
    // Tìm theo ID loại sự kiện
    List<SuKien> findByLoaiSuKien_IdAndDeletedFalse(Long loaiSuKienId);
    
    // Tìm theo trạng thái
    List<SuKien> findByTrangThaiAndDeletedFalse(SuKien.TrangThaiSuKien trangThai);
    
    // Advanced search with multiple conditions - Dynamic ordering via Pageable
    @Query("SELECT sk FROM SuKien sk LEFT JOIN sk.loaiSuKien lsk WHERE sk.deleted = false AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(sk.tenSuKien) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(sk.moTa) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(sk.diaDiem) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(lsk.tenLoai) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:loaiSuKienId IS NULL OR sk.loaiSuKien.id = :loaiSuKienId) AND " +
           "(:trangThai IS NULL OR sk.trangThai = :trangThai) AND " +
           "(:fromDate IS NULL OR CAST(sk.ngayDienRa AS DATE) >= :fromDate) AND " +
           "(:toDate IS NULL OR CAST(sk.ngayDienRa AS DATE) <= :toDate)")
    Page<SuKien> searchSuKien(
        @Param("search") String search,
        @Param("loaiSuKienId") Long loaiSuKienId,
        @Param("trangThai") SuKien.TrangThaiSuKien trangThai,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        Pageable pageable);
    
    // Tìm sự kiện sắp diễn ra
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = false AND " +
           "sk.ngayDienRa >= CURRENT_TIMESTAMP AND " +
           "sk.trangThai IN ('DANG_CHUAN_BI', 'DANG_DIEN_RA') " +
           "ORDER BY sk.ngayDienRa ASC")
    List<SuKien> findUpcomingEvents();
    
    // Tìm 10 sự kiện gần nhất theo ngày diễn ra (bao gồm cả quá khứ và tương lai)
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = false " +
           "ORDER BY sk.ngayDienRa DESC")
    List<SuKien> findRecentEventsByDate(org.springframework.data.domain.Pageable pageable);
    
    // Tìm sự kiện trong khoảng thời gian (7 ngày gần nhất)
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = false AND " +
           "sk.ngayDienRa BETWEEN :startDate AND :endDate " +
           "ORDER BY sk.ngayDienRa DESC")
    List<SuKien> findRecentEvents(@Param("startDate") java.time.LocalDateTime startDate, 
                                  @Param("endDate") java.time.LocalDateTime endDate,
                                  org.springframework.data.domain.Pageable pageable);
    
    // Tìm sự kiện theo tháng
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = false AND " +
           "YEAR(sk.ngayDienRa) = :year AND " +
           "MONTH(sk.ngayDienRa) = :month " +
           "ORDER BY sk.ngayDienRa ASC")
    List<SuKien> findSuKienTheoThang(@Param("year") int year, @Param("month") int month);
    
    // Thống kê theo loại sự kiện
    @Query("SELECT lsk.tenLoai, COUNT(sk) FROM SuKien sk " +
           "LEFT JOIN sk.loaiSuKien lsk " +
           "WHERE sk.deleted = false AND lsk IS NOT NULL " +
           "GROUP BY lsk.id, lsk.tenLoai " +
           "ORDER BY COUNT(sk) DESC")
    List<Object[]> getThongKeTheoLoaiSuKien();
    
    // Đếm sự kiện theo loại sự kiện
    @Query("SELECT COUNT(sk) FROM SuKien sk WHERE sk.deleted = false AND sk.loaiSuKien.id = :loaiSuKienId")
    long countByLoaiSuKienId(@Param("loaiSuKienId") Long loaiSuKienId);
    
    // Kiểm tra có sự kiện nào đang sử dụng loại sự kiện này không
    boolean existsByLoaiSuKienAndDeletedFalse(LoaiSuKien loaiSuKien);
    
    // Count active events
    @Query("SELECT COUNT(sk) FROM SuKien sk WHERE sk.deleted = false")
    long countActive();
    
    // Count deleted events
    @Query("SELECT COUNT(sk) FROM SuKien sk WHERE sk.deleted = true")
    long countDeleted();
    
    // Find deleted events with search
    @Query("SELECT sk FROM SuKien sk WHERE sk.deleted = true AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(sk.tenSuKien) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(sk.moTa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SuKien> findDeletedWithSearch(@Param("search") String search, Pageable pageable);
}