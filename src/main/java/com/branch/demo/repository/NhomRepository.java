package com.branch.demo.repository;

import com.branch.demo.domain.Nhom;
import com.branch.demo.domain.Nhom.TrangThaiNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhomRepository extends JpaRepository<Nhom, Long> {
    
    // Tìm theo tên nhóm
    List<Nhom> findByTenNhomContainingIgnoreCase(String tenNhom);
    
    // Tìm theo điểm nhóm
    List<Nhom> findByDiemNhomId(Long diemNhomId);
    
    // Tìm theo trạng thái
    List<Nhom> findByTrangThai(TrangThaiNhom trangThai);
    
    // Tìm nhóm có hoạt động
    List<Nhom> findByTrangThaiOrderByTenNhomAsc(TrangThaiNhom trangThai);
    
    // Đếm số nhóm theo điểm nhóm
    long countByDiemNhomId(Long diemNhomId);
    
    // Thống kê số lượng tín hữu theo nhóm
    @Query("SELECT n.tenNhom, COUNT(t) FROM Nhom n LEFT JOIN n.danhSachTinHuu t " +
           "WHERE n.trangThai = :trangThai " +
           "GROUP BY n.id, n.tenNhom " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getThongKeSoLuongTinHuuTheoNhom(@Param("trangThai") TrangThaiNhom trangThai);
    
    // Tìm nhóm theo điểm nhóm và trạng thái
    List<Nhom> findByDiemNhomIdAndTrangThai(Long diemNhomId, TrangThaiNhom trangThai);
    
    // Tìm kiếm nhóm với filter nâng cao
    @Query("SELECT n FROM Nhom n LEFT JOIN n.diemNhom dn WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(n.tenNhom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.moTa) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR n.trangThai = :trangThai) AND " +
           "(:diemNhomId IS NULL OR dn.id = :diemNhomId) AND " +
           "(:fromDate IS NULL OR n.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR n.createdAt <= :toDate)")
    org.springframework.data.domain.Page<Nhom> findWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") TrangThaiNhom trangThai,
            @Param("diemNhomId") Long diemNhomId,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            org.springframework.data.domain.Pageable pageable);
}