package com.branch.demo.repository;

import com.branch.demo.domain.NhanSu;
import com.branch.demo.domain.NhanSu.TrangThaiNhanSu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanSuRepository extends JpaRepository<NhanSu, Long> {
    
    // Tìm theo tên
    List<NhanSu> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm theo ban ngành
    List<NhanSu> findByBanNganhId(Long banNganhId);
    
    // Tìm theo chức vụ
    List<NhanSu> findByChucVuContainingIgnoreCase(String chucVu);
    
    // Tìm theo trạng thái
    List<NhanSu> findByTrangThai(TrangThaiNhanSu trangThai);
    
    // Tìm theo ban ngành và trạng thái
    List<NhanSu> findByBanNganhIdAndTrangThai(Long banNganhId, TrangThaiNhanSu trangThai);
    
    // Đếm số nhân sự theo ban ngành
    long countByBanNganhId(Long banNganhId);
    
    // Đếm số nhân sự theo trạng thái
    long countByTrangThai(TrangThaiNhanSu trangThai);
    
    // Tìm trưởng ban
    @Query("SELECT ns FROM NhanSu ns WHERE " +
           "LOWER(ns.chucVu) LIKE LOWER('%trưởng%') AND " +
           "ns.trangThai = :trangThai")
    List<NhanSu> findTruongBan(@Param("trangThai") TrangThaiNhanSu trangThai);
    
    // Tìm nhân sự theo nhiều điều kiện
    @Query("SELECT ns FROM NhanSu ns WHERE " +
           "(:hoTen IS NULL OR LOWER(ns.hoTen) LIKE LOWER(CONCAT('%', :hoTen, '%'))) AND " +
           "(:banNganhId IS NULL OR ns.banNganh.id = :banNganhId) AND " +
           "(:chucVu IS NULL OR LOWER(ns.chucVu) LIKE LOWER(CONCAT('%', :chucVu, '%'))) AND " +
           "(:trangThai IS NULL OR ns.trangThai = :trangThai)")
    List<NhanSu> findByMultipleConditions(
        @Param("hoTen") String hoTen,
        @Param("banNganhId") Long banNganhId,
        @Param("chucVu") String chucVu,
        @Param("trangThai") TrangThaiNhanSu trangThai);
    
    // Thống kê nhân sự theo ban ngành
    @Query("SELECT bn.tenBan, COUNT(ns) FROM BanNganh bn " +
           "LEFT JOIN bn.danhSachNhanSu ns " +
           "WHERE ns.trangThai = :trangThai OR ns.trangThai IS NULL " +
           "GROUP BY bn.id, bn.tenBan " +
           "ORDER BY COUNT(ns) DESC")
    List<Object[]> getThongKeNhanSuTheoBanNganh(@Param("trangThai") TrangThaiNhanSu trangThai);

    // Tìm theo điểm nhóm
    List<NhanSu> findByDiemNhomId(Long diemNhomId);
    
    // Tìm theo điểm nhóm với pagination
    Page<NhanSu> findByDiemNhomId(Long diemNhomId, Pageable pageable);
    
    // Tìm theo ban ngành thông qua điểm nhóm với pagination
    @Query("SELECT ns FROM NhanSu ns WHERE ns.diemNhom.banNganh.id = :banNganhId")
    Page<NhanSu> findByDiemNhomBanNganhId(@Param("banNganhId") Long banNganhId, Pageable pageable);

    // Advanced search với pagination
    @Query("SELECT ns FROM NhanSu ns WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(ns.hoTen) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(ns.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " ns.dienThoai LIKE CONCAT('%', :search, '%')) AND " +
           "(:trangThai IS NULL OR ns.trangThai = :trangThai) AND " +
           "(:banNganhId IS NULL OR ns.banNganh.id = :banNganhId) AND " +
           "(:diemNhomId IS NULL OR ns.diemNhom.id = :diemNhomId) AND " +
           "(:fromDate IS NULL OR ns.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR ns.createdAt <= :toDate)")
    Page<NhanSu> findWithAdvancedFilters(
        @Param("search") String search,
        @Param("trangThai") TrangThaiNhanSu trangThai,
        @Param("banNganhId") Long banNganhId,
        @Param("diemNhomId") Long diemNhomId,
        @Param("fromDate") java.time.LocalDateTime fromDate,
        @Param("toDate") java.time.LocalDateTime toDate,
        Pageable pageable);
}