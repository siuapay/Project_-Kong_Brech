package com.branch.demo.repository;

import com.branch.demo.domain.TaiChinhDanhMuc;
import com.branch.demo.domain.TaiChinhDanhMuc.LoaiDanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiChinhDanhMucRepository extends JpaRepository<TaiChinhDanhMuc, Long> {
    
    // Tìm theo loại danh mục
    List<TaiChinhDanhMuc> findByLoai(LoaiDanhMuc loai);
    
    // Tìm theo tên danh mục (case-insensitive)
    List<TaiChinhDanhMuc> findByTenDanhMucContainingIgnoreCase(String tenDanhMuc);
    
    // Tìm theo loại với phân trang
    Page<TaiChinhDanhMuc> findByLoai(LoaiDanhMuc loai, Pageable pageable);
    
    // Tìm kiếm nâng cao với phân trang
    @Query("SELECT dm FROM TaiChinhDanhMuc dm WHERE " +
           "(:tenDanhMuc IS NULL OR LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :tenDanhMuc, '%'))) AND " +
           "(:loai IS NULL OR dm.loai = :loai)")
    Page<TaiChinhDanhMuc> findByMultipleConditions(
        @Param("tenDanhMuc") String tenDanhMuc,
        @Param("loai") LoaiDanhMuc loai,
        Pageable pageable);
    
    // Đếm theo loại
    long countByLoai(LoaiDanhMuc loai);
    
    // Kiểm tra tên danh mục đã tồn tại
    boolean existsByTenDanhMucIgnoreCase(String tenDanhMuc);
    
    // Tìm theo tên danh mục chính xác (case-insensitive)
    Optional<TaiChinhDanhMuc> findByTenDanhMucIgnoreCase(String tenDanhMuc);
    
    // Kiểm tra tên danh mục đã tồn tại (trừ ID hiện tại)
    @Query("SELECT COUNT(dm) > 0 FROM TaiChinhDanhMuc dm WHERE " +
           "LOWER(dm.tenDanhMuc) = LOWER(:tenDanhMuc) AND dm.id != :id")
    boolean existsByTenDanhMucIgnoreCaseAndIdNot(@Param("tenDanhMuc") String tenDanhMuc, @Param("id") Long id);
    
    // Lấy danh mục theo loại, sắp xếp theo tên
    List<TaiChinhDanhMuc> findByLoaiOrderByTenDanhMucAsc(LoaiDanhMuc loai);
    
    // Lấy tất cả danh mục sắp xếp theo loại và tên
    @Query("SELECT dm FROM TaiChinhDanhMuc dm ORDER BY dm.loai ASC, dm.tenDanhMuc ASC")
    List<TaiChinhDanhMuc> findAllOrderByLoaiAndTenDanhMuc();
    
    // Tìm kiếm theo tên với phân trang
    Page<TaiChinhDanhMuc> findByTenDanhMucContainingIgnoreCase(String tenDanhMuc, Pageable pageable);
    
    // Kiểm tra danh mục có đang được sử dụng không
    @Query("SELECT COUNT(gd) > 0 FROM TaiChinhGiaoDich gd WHERE gd.danhMuc.id = :danhMucId")
    boolean isBeingUsed(@Param("danhMucId") Long danhMucId);
    
    // Lấy danh mục được sử dụng nhiều nhất
    @Query("SELECT dm FROM TaiChinhDanhMuc dm " +
           "LEFT JOIN TaiChinhGiaoDich gd ON gd.danhMuc.id = dm.id " +
           "GROUP BY dm.id " +
           "ORDER BY COUNT(gd.id) DESC")
    Page<TaiChinhDanhMuc> findMostUsedCategories(Pageable pageable);
    
    // Thống kê số lượng giao dịch theo danh mục
    @Query("SELECT dm.tenDanhMuc, COUNT(gd.id) FROM TaiChinhDanhMuc dm " +
           "LEFT JOIN TaiChinhGiaoDich gd ON gd.danhMuc.id = dm.id " +
           "GROUP BY dm.id, dm.tenDanhMuc " +
           "ORDER BY COUNT(gd.id) DESC")
    List<Object[]> getCategoryUsageStatistics();
}