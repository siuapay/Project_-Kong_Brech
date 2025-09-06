package com.branch.demo.repository;

import com.branch.demo.domain.DiemNhom;
import com.branch.demo.domain.DiemNhom.TrangThaiDiemNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiemNhomRepository extends JpaRepository<DiemNhom, Long> {
    
    // Tìm theo tên điểm nhóm
    List<DiemNhom> findByTenDiemNhomContainingIgnoreCase(String tenDiemNhom);
    
    // Tìm theo trạng thái
    List<DiemNhom> findByTrangThai(TrangThaiDiemNhom trangThai);
    
    // Tìm theo trạng thái và sắp xếp theo tên
    List<DiemNhom> findByTrangThaiOrderByTenDiemNhomAsc(TrangThaiDiemNhom trangThai);
    
    // Tìm theo địa chỉ
    List<DiemNhom> findByDiaChiContainingIgnoreCase(String diaChi);
    
    // Tìm theo ban ngành
    List<DiemNhom> findByBanNganhId(Long banNganhId);
    
    // Thống kê tổng số tín hữu theo điểm nhóm
    @Query("SELECT d.tenDiemNhom, " +
           "COUNT(DISTINCT n) as soNhom, " +
           "COUNT(t) as soTinHuu " +
           "FROM DiemNhom d " +
           "LEFT JOIN d.danhSachNhom n " +
           "LEFT JOIN n.danhSachTinHuu t " +
           "WHERE d.trangThai = :trangThai " +
           "GROUP BY d.id, d.tenDiemNhom " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getThongKeTheoTinHuu(@Param("trangThai") TrangThaiDiemNhom trangThai);
    
    // Tìm điểm nhóm có nhân sự phụ trách
    @Query("SELECT DISTINCT d FROM DiemNhom d " +
           "JOIN d.danhSachNhanSu ns " +
           "WHERE d.trangThai = :trangThai")
    List<DiemNhom> findDiemNhomCoNhanSu(@Param("trangThai") TrangThaiDiemNhom trangThai);
    
    // Tìm kiếm điểm nhóm với filter nâng cao
    @Query("SELECT d FROM DiemNhom d WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(d.tenDiemNhom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.diaChi) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:trangThai IS NULL OR d.trangThai = :trangThai) AND " +
           "(:fromDate IS NULL OR d.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR d.createdAt <= :toDate)")
    org.springframework.data.domain.Page<DiemNhom> findWithAdvancedFilters(
            @Param("search") String search,
            @Param("trangThai") TrangThaiDiemNhom trangThai,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            org.springframework.data.domain.Pageable pageable);
}