package com.branch.demo.repository;

import com.branch.demo.domain.NhanSuDiemNhom;
import com.branch.demo.domain.NhanSuDiemNhom.TrangThaiNhanSuDiemNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanSuDiemNhomRepository extends JpaRepository<NhanSuDiemNhom, Long> {
    
    // Tìm theo tên
    List<NhanSuDiemNhom> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm theo điểm nhóm
    List<NhanSuDiemNhom> findByDiemNhomId(Long diemNhomId);
    
    // Tìm theo chức vụ
    List<NhanSuDiemNhom> findByChucVuContainingIgnoreCase(String chucVu);
    
    // Tìm theo trạng thái
    List<NhanSuDiemNhom> findByTrangThai(TrangThaiNhanSuDiemNhom trangThai);
    
    // Tìm theo điểm nhóm và trạng thái
    List<NhanSuDiemNhom> findByDiemNhomIdAndTrangThai(Long diemNhomId, TrangThaiNhanSuDiemNhom trangThai);
    
    // Tìm trưởng nhóm theo điểm nhóm
    @Query("SELECT ns FROM NhanSuDiemNhom ns WHERE " +
           "ns.diemNhom.id = :diemNhomId AND " +
           "LOWER(ns.chucVu) LIKE LOWER('%trưởng%') AND " +
           "ns.trangThai = :trangThai")
    List<NhanSuDiemNhom> findTruongNhomByDiemNhom(
        @Param("diemNhomId") Long diemNhomId, 
        @Param("trangThai") TrangThaiNhanSuDiemNhom trangThai);
    
    // Tìm phó nhóm theo điểm nhóm
    @Query("SELECT ns FROM NhanSuDiemNhom ns WHERE " +
           "ns.diemNhom.id = :diemNhomId AND " +
           "LOWER(ns.chucVu) LIKE LOWER('%phó%') AND " +
           "ns.trangThai = :trangThai")
    List<NhanSuDiemNhom> findPhoNhomByDiemNhom(
        @Param("diemNhomId") Long diemNhomId, 
        @Param("trangThai") TrangThaiNhanSuDiemNhom trangThai);
    
    // Đếm số nhân sự theo điểm nhóm
    long countByDiemNhomId(Long diemNhomId);
    
    // Đếm số nhân sự theo trạng thái
    long countByTrangThai(TrangThaiNhanSuDiemNhom trangThai);
    
    // Tìm theo nhiều điều kiện
    @Query("SELECT ns FROM NhanSuDiemNhom ns WHERE " +
           "(:hoTen IS NULL OR LOWER(ns.hoTen) LIKE LOWER(CONCAT('%', :hoTen, '%'))) AND " +
           "(:diemNhomId IS NULL OR ns.diemNhom.id = :diemNhomId) AND " +
           "(:chucVu IS NULL OR LOWER(ns.chucVu) LIKE LOWER(CONCAT('%', :chucVu, '%'))) AND " +
           "(:trangThai IS NULL OR ns.trangThai = :trangThai)")
    List<NhanSuDiemNhom> findByMultipleConditions(
        @Param("hoTen") String hoTen,
        @Param("diemNhomId") Long diemNhomId,
        @Param("chucVu") String chucVu,
        @Param("trangThai") TrangThaiNhanSuDiemNhom trangThai);
    
    // Thống kê nhân sự theo điểm nhóm
    @Query("SELECT dn.tenDiemNhom, COUNT(ns) FROM DiemNhom dn " +
           "LEFT JOIN dn.danhSachNhanSu ns " +
           "WHERE ns.trangThai = :trangThai OR ns.trangThai IS NULL " +
           "GROUP BY dn.id, dn.tenDiemNhom " +
           "ORDER BY COUNT(ns) DESC")
    List<Object[]> getThongKeNhanSuTheoDiemNhom(@Param("trangThai") TrangThaiNhanSuDiemNhom trangThai);
    
    // Thống kê theo chức vụ
    @Query("SELECT ns.chucVu, COUNT(ns) FROM NhanSuDiemNhom ns " +
           "WHERE ns.trangThai = :trangThai " +
           "GROUP BY ns.chucVu " +
           "ORDER BY COUNT(ns) DESC")
    List<Object[]> getThongKeTheoChucVu(@Param("trangThai") TrangThaiNhanSuDiemNhom trangThai);
}