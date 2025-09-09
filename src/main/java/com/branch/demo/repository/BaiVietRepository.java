package com.branch.demo.repository;

import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.BaiViet.TrangThaiBaiViet;
import com.branch.demo.domain.DanhMuc;
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
public interface BaiVietRepository extends JpaRepository<BaiViet, Long> {
    
    // Tìm theo slug
    Optional<BaiViet> findBySlug(String slug);
    
    // Tìm theo slug và đã xuất bản
    @Query("SELECT bv FROM BaiViet bv WHERE bv.slug = :slug AND bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    Optional<BaiViet> findBySlugAndPublished(@Param("slug") String slug);
    
    // Tìm theo danh mục
    List<BaiViet> findByDanhMuc(DanhMuc danhMuc);
    
    // Tìm theo danh mục và đã xuất bản
    @Query("SELECT bv FROM BaiViet bv WHERE bv.danhMuc = :danhMuc AND bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    Page<BaiViet> findByDanhMucAndPublished(@Param("danhMuc") DanhMuc danhMuc, Pageable pageable);
    
    // Tìm theo trạng thái
    List<BaiViet> findByTrangThai(TrangThaiBaiViet trangThai);
    
    // Tìm tất cả đã xuất bản
    @Query("SELECT bv FROM BaiViet bv WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    Page<BaiViet> findAllPublished(Pageable pageable);
    
    // Tìm bài viết nổi bật
    @Query("SELECT bv FROM BaiViet bv WHERE bv.noiBat = true AND bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    Page<BaiViet> findFeaturedArticles(Pageable pageable);
    
    // Tìm bài viết mới nhất
    @Query("SELECT bv FROM BaiViet bv WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    List<BaiViet> findLatestArticles(Pageable pageable);
    
    // Tìm bài viết phổ biến (nhiều lượt xem)
    @Query("SELECT bv FROM BaiViet bv WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    List<BaiViet> findPopularArticles(Pageable pageable);
    
    // Tìm tất cả chưa bị xóa
    @Query("SELECT bv FROM BaiViet bv WHERE bv.deletedAt IS NULL")
    Page<BaiViet> findAllNotDeleted(Pageable pageable);
    
    // Tìm tất cả đã bị xóa
    @Query("SELECT bv FROM BaiViet bv WHERE bv.deletedAt IS NOT NULL")
    Page<BaiViet> findAllDeleted(Pageable pageable);
    
    // Tìm kiếm với search
    @Query("SELECT bv FROM BaiViet bv WHERE bv.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(bv.tieuDe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tomTat) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.noiDung) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BaiViet> findWithSearch(@Param("search") String search, Pageable pageable);
    
    // Tìm kiếm công khai (chỉ bài đã xuất bản)
    @Query("SELECT bv FROM BaiViet bv WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(bv.tieuDe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tomTat) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.noiDung) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BaiViet> findPublishedWithSearch(@Param("search") String search, Pageable pageable);
    
    // Tìm kiếm nâng cao (admin)
    @Query("SELECT bv FROM BaiViet bv LEFT JOIN bv.danhMuc dm WHERE bv.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(bv.tieuDe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tomTat) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tacGia) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR bv.trangThai = :trangThai) AND " +
           "(:danhMucId IS NULL OR dm.id = :danhMucId) AND " +
           "(:loaiTacGia IS NULL OR bv.loaiTacGia = :loaiTacGia) AND " +
           "(:noiBat IS NULL OR bv.noiBat = :noiBat) AND " +
           "(:fromDate IS NULL OR bv.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR bv.createdAt <= :toDate)")
    Page<BaiViet> findWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") TrangThaiBaiViet trangThai,
            @Param("danhMucId") Long danhMucId,
            @Param("loaiTacGia") BaiViet.LoaiTacGia loaiTacGia,
            @Param("noiBat") Boolean noiBat,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    
    // Tìm kiếm nâng cao công khai
    @Query("SELECT bv FROM BaiViet bv LEFT JOIN bv.danhMuc dm WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(bv.tieuDe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tomTat) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:danhMucId IS NULL OR dm.id = :danhMucId) AND " +
           "(:fromDate IS NULL OR bv.ngayXuatBan >= :fromDate) AND " +
           "(:toDate IS NULL OR bv.ngayXuatBan <= :toDate)")
    Page<BaiViet> findPublishedWithAdvancedFilters(
            @Param("search") String search,
            @Param("danhMucId") Long danhMucId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    
    // Kiểm tra slug có tồn tại không
    boolean existsBySlug(String slug);
    
    // Kiểm tra slug có tồn tại không (trừ ID hiện tại)
    boolean existsBySlugAndIdNot(String slug, Long id);
    
    // Đếm số bài viết theo trạng thái
    long countByTrangThai(TrangThaiBaiViet trangThai);
    
    // Đếm số bài viết chưa bị xóa
    @Query("SELECT COUNT(bv) FROM BaiViet bv WHERE bv.deletedAt IS NULL")
    long countNotDeleted();
    
    // Đếm số bài viết đã bị xóa
    @Query("SELECT COUNT(bv) FROM BaiViet bv WHERE bv.deletedAt IS NOT NULL")
    long countDeleted();
    
    // Đếm số bài viết đã xuất bản
    @Query("SELECT COUNT(bv) FROM BaiViet bv WHERE bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    long countPublished();
    
    // Thống kê theo tác giả
    @Query("SELECT bv.tacGia, bv.loaiTacGia, COUNT(bv) FROM BaiViet bv " +
           "WHERE bv.deletedAt IS NULL " +
           "GROUP BY bv.tacGia, bv.loaiTacGia " +
           "ORDER BY COUNT(bv) DESC")
    List<Object[]> getThongKeTheoTacGia();
    
    // Thống kê theo danh mục
    @Query("SELECT dm.tenDanhMuc, COUNT(bv) FROM BaiViet bv " +
           "LEFT JOIN bv.danhMuc dm " +
           "WHERE bv.deletedAt IS NULL " +
           "GROUP BY dm.id, dm.tenDanhMuc " +
           "ORDER BY COUNT(bv) DESC")
    List<Object[]> getThongKeTheoDanhMuc();
    
    // Lấy bài viết liên quan (cùng danh mục, trừ bài hiện tại)
    @Query("SELECT bv FROM BaiViet bv WHERE bv.danhMuc = :danhMuc AND bv.id != :currentId AND " +
           "bv.trangThai = 'DA_XUAT_BAN' AND bv.deletedAt IS NULL")
    List<BaiViet> findRelatedArticles(@Param("danhMuc") DanhMuc danhMuc, @Param("currentId") Long currentId, Pageable pageable);
    
    // Tìm kiếm trong thùng rác
    @Query("SELECT bv FROM BaiViet bv WHERE bv.deletedAt IS NOT NULL AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(bv.tieuDe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tomTat) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(bv.tacGia) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BaiViet> findDeletedWithSearch(@Param("search") String search, Pageable pageable);
    
    // Tìm bài viết đã xóa theo khoảng thời gian
    @Query("SELECT bv FROM BaiViet bv WHERE bv.deletedAt IS NOT NULL AND " +
           "(:fromDate IS NULL OR bv.deletedAt >= :fromDate) AND " +
           "(:toDate IS NULL OR bv.deletedAt <= :toDate)")
    Page<BaiViet> findDeletedByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);
    
    // Tìm bài viết theo ID bao gồm cả đã xóa
    @Query("SELECT bv FROM BaiViet bv WHERE bv.id = :id")
    Optional<BaiViet> findByIdIncludingDeleted(@Param("id") Long id);
    
    // Override findById để chỉ tìm bài viết chưa xóa
    @Query("SELECT bv FROM BaiViet bv WHERE bv.id = :id AND bv.deletedAt IS NULL")
    Optional<BaiViet> findByIdNotDeleted(@Param("id") Long id);
    
    // Đếm số bài viết theo danh mục
    @Query("SELECT COUNT(bv) FROM BaiViet bv WHERE bv.danhMuc = :danhMuc AND bv.deletedAt IS NULL")
    long countByDanhMuc(@Param("danhMuc") DanhMuc danhMuc);
}