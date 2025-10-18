package com.branch.demo.repository;

import com.branch.demo.domain.ChapSu;
import com.branch.demo.domain.ChapSu.TrangThaiChapSu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapSuRepository extends JpaRepository<ChapSu, Long> {
    
    // Tìm theo tên
    List<ChapSu> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm theo tên hoặc chức vụ với pagination - SỬA: Chỉ tìm theo tên để tránh lỗi enum
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.hoTen) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ChapSu> findByHoTenContainingIgnoreCaseOrChucVuContainingIgnoreCase(@Param("search") String hoTen, @Param("chucVu") String chucVu, Pageable pageable);
    
    // Advanced filters với pagination - SỬA: Bỏ tìm kiếm theo chức vụ để tránh lỗi enum
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(cs.hoTen) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR cs.trangThai = :trangThai) AND " +
           "(:banNganhId IS NULL OR cs.banNganh.id = :banNganhId) AND " +
           "(:diemNhomId IS NULL OR cs.diemNhom.id = :diemNhomId) AND " +
           "(:fromDate IS NULL OR cs.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR cs.createdAt <= :toDate)")
    Page<ChapSu> findWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") TrangThaiChapSu trangThai,
            @Param("banNganhId") Long banNganhId,
            @Param("diemNhomId") Long diemNhomId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    
    // Tìm theo trạng thái
    List<ChapSu> findByTrangThai(TrangThaiChapSu trangThai);
    
    // Tìm theo trạng thái và sắp xếp theo họ tên (thay thế thứ tự hiển thị)
    List<ChapSu> findByTrangThaiOrderByHoTenAsc(TrangThaiChapSu trangThai);
    
    // Tìm chấp sự trưởng - SỬA: Bỏ ORDER BY thuTuHienThi
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.chucVu) LIKE LOWER('%trưởng%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.hoTen ASC")  // ← Đổi thành sắp xếp theo họ tên
    Optional<ChapSu> findChapSuTruong(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Tìm mục sư - SỬA: Bỏ ORDER BY thuTuHienThi
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.chucVu) LIKE LOWER('%mục sư%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.hoTen ASC")  // ← Đổi thành sắp xếp theo họ tên
    List<ChapSu> findMucSu(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Tìm truyền đạo - SỬA: Bỏ ORDER BY thuTuHienThi
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.chucVu) LIKE LOWER('%truyền đạo%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.hoTen ASC")  // ← Đổi thành sắp xếp theo họ tên
    List<ChapSu> findTruyenDao(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Đếm số lượng theo trạng thái
    long countByTrangThai(TrangThaiChapSu trangThai);
    
    // Thống kê theo chức vụ (không thay đổi)
    @Query("SELECT cs.chucVu, COUNT(cs) FROM ChapSu cs " +
           "WHERE cs.trangThai = :trangThai " +
           "GROUP BY cs.chucVu " +
           "ORDER BY COUNT(cs) DESC")
    List<Object[]> getThongKeTheoChucVu(@Param("trangThai") TrangThaiChapSu trangThai);
}