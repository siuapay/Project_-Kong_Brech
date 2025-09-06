package com.branch.demo.repository;

import com.branch.demo.domain.LoaiSuKien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoaiSuKienRepository extends JpaRepository<LoaiSuKien, Long> {
    
    // Find active (not deleted) records
    Page<LoaiSuKien> findByDeletedFalseOrderByThuTuAscCreatedAtDesc(Pageable pageable);
    
    List<LoaiSuKien> findByDeletedFalseAndKichHoatTrueOrderByThuTuAscCreatedAtDesc();
    
    List<LoaiSuKien> findByDeletedFalseAndKichHoatTrueOrderByThuTuAscTenLoaiAsc();
    
    List<LoaiSuKien> findByKichHoatTrueOrderByTenLoaiAsc();
    
    List<LoaiSuKien> findByDeletedFalseOrderByThuTuAscCreatedAtDesc();
    
    // Find deleted records
    Page<LoaiSuKien> findByDeletedTrueOrderByDeletedAtDesc(Pageable pageable);
    
    // Search functionality
    @Query("SELECT l FROM LoaiSuKien l WHERE l.deleted = false AND " +
           "(LOWER(l.tenLoai) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.moTa) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY l.thuTu ASC, l.createdAt DESC")
    Page<LoaiSuKien> searchByKeyword(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT l FROM LoaiSuKien l WHERE l.deleted = true AND " +
           "(LOWER(l.tenLoai) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.moTa) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY l.deletedAt DESC")
    Page<LoaiSuKien> searchDeletedByKeyword(@Param("search") String search, Pageable pageable);
    
    // Check if name exists (for validation)
    boolean existsByTenLoaiAndDeletedFalse(String tenLoai);
    
    boolean existsByTenLoaiAndDeletedFalseAndIdNot(String tenLoai, Long id);
    
    // Find by ID (not deleted)
    Optional<LoaiSuKien> findByIdAndDeletedFalse(Long id);
    
    // Count active records
    long countByDeletedFalse();
    
    long countByDeletedFalseAndKichHoatTrue();
}