package com.branch.demo.repository;

import com.branch.demo.domain.DanhMuc;
import com.branch.demo.domain.DanhMuc.TrangThaiDanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    
    // Tìm theo slug
    Optional<DanhMuc> findBySlug(String slug);
    
    // Tìm theo slug và chưa bị xóa
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.slug = :slug AND dm.deletedAt IS NULL")
    Optional<DanhMuc> findBySlugAndNotDeleted(@Param("slug") String slug);
    
    // Tìm theo tên danh mục
    List<DanhMuc> findByTenDanhMucContainingIgnoreCase(String tenDanhMuc);
    
    // Tìm theo trạng thái
    List<DanhMuc> findByTrangThai(TrangThaiDanhMuc trangThai);
    
    // Tìm theo trạng thái và chưa bị xóa
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.trangThai = :trangThai AND dm.deletedAt IS NULL ORDER BY dm.thuTu ASC, dm.tenDanhMuc ASC")
    List<DanhMuc> findByTrangThaiAndNotDeleted(@Param("trangThai") TrangThaiDanhMuc trangThai);
    
    // Tìm tất cả chưa bị xóa
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NULL")
    List<DanhMuc> findAllNotDeleted();
    
    // Tìm tất cả chưa bị xóa với pagination
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NULL")
    Page<DanhMuc> findAllNotDeleted(Pageable pageable);
    
    // Tìm tất cả đã bị xóa
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NOT NULL")
    Page<DanhMuc> findAllDeleted(Pageable pageable);
    
    // Tìm kiếm với search
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(dm.moTa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DanhMuc> findWithSearch(@Param("search") String search, Pageable pageable);
    
    // Tìm kiếm với search và trạng thái
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(dm.moTa) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR dm.trangThai = :trangThai)")
    Page<DanhMuc> findWithSearch(@Param("search") String search, @Param("trangThai") TrangThaiDanhMuc trangThai, Pageable pageable);
    
    // Tìm theo trạng thái với pagination
    Page<DanhMuc> findByTrangThai(TrangThaiDanhMuc trangThai, Pageable pageable);
    
    // Tìm kiếm nâng cao
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(dm.moTa) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR dm.trangThai = :trangThai) AND " +
           "(:fromDate IS NULL OR dm.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR dm.createdAt <= :toDate)")
    Page<DanhMuc> findWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") TrangThaiDanhMuc trangThai,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            Pageable pageable);
    
    // Kiểm tra slug có tồn tại không
    boolean existsBySlug(String slug);
    
    // Kiểm tra slug có tồn tại không (trừ ID hiện tại)
    boolean existsBySlugAndIdNot(String slug, Long id);
    
    // Đếm số danh mục theo trạng thái
    long countByTrangThai(TrangThaiDanhMuc trangThai);
    
    // Đếm số danh mục chưa bị xóa
    @Query("SELECT COUNT(dm) FROM DanhMuc dm WHERE dm.deletedAt IS NULL")
    long countNotDeleted();
    
    // Đếm số danh mục đã bị xóa
    @Query("SELECT COUNT(dm) FROM DanhMuc dm WHERE dm.deletedAt IS NOT NULL")
    long countDeleted();
    
    // Thống kê số bài viết theo danh mục
    @Query("SELECT dm.tenDanhMuc, COUNT(bv) FROM DanhMuc dm " +
           "LEFT JOIN dm.danhSachBaiViet bv " +
           "WHERE dm.deletedAt IS NULL AND (bv.deletedAt IS NULL OR bv.deletedAt IS NULL) " +
           "GROUP BY dm.id, dm.tenDanhMuc " +
           "ORDER BY COUNT(bv) DESC")
    List<Object[]> getThongKeBaiVietTheoDanhMuc();
    
    // Lấy danh mục có nhiều bài viết nhất
    @Query("SELECT dm FROM DanhMuc dm " +
           "LEFT JOIN dm.danhSachBaiViet bv " +
           "WHERE dm.deletedAt IS NULL AND dm.trangThai = :trangThai " +
           "GROUP BY dm.id " +
           "ORDER BY COUNT(bv) DESC")
    List<DanhMuc> findTopDanhMucByBaiVietCount(@Param("trangThai") TrangThaiDanhMuc trangThai, Pageable pageable);

    List<DanhMuc> findByTrangThaiOrderByTenDanhMucAsc(TrangThaiDanhMuc trangThai);
}