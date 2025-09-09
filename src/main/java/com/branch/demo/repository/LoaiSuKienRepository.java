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
    
    // Find all records with pagination
    Page<LoaiSuKien> findAll(Pageable pageable);
    
    // Find active records
    List<LoaiSuKien> findByKichHoatTrueOrderByCreatedAtDesc();
    
    List<LoaiSuKien> findByKichHoatTrueOrderByTenLoaiAsc();
    

    
    // Search functionality
    @Query("SELECT l FROM LoaiSuKien l WHERE " +
           "(LOWER(l.tenLoai) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.moTa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LoaiSuKien> searchByKeyword(@Param("search") String search, Pageable pageable);
    
    // Check if name exists (for validation)
    boolean existsByTenLoai(String tenLoai);
    
    boolean existsByTenLoaiAndIdNot(String tenLoai, Long id);
    
    // Count records
    long countByKichHoatTrue();
}