package com.branch.demo.repository;

import com.branch.demo.domain.ChapSu;
import com.branch.demo.domain.ChapSu.TrangThaiChapSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapSuRepository extends JpaRepository<ChapSu, Long> {
    
    // Tìm theo tên
    List<ChapSu> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm theo chức vụ
    List<ChapSu> findByChucVuContainingIgnoreCase(String chucVu);
    
    // Tìm theo cấp bậc
    List<ChapSu> findByCapBacContainingIgnoreCase(String capBac);
    
    // Tìm theo trạng thái
    List<ChapSu> findByTrangThai(TrangThaiChapSu trangThai);
    
    // Tìm theo trạng thái và sắp xếp theo thứ tự hiển thị
    List<ChapSu> findByTrangThaiOrderByThuTuHienThiAscHoTenAsc(TrangThaiChapSu trangThai);
    
    // Tìm chấp sự trưởng
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.chucVu) LIKE LOWER('%trưởng%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.thuTuHienThi ASC")
    Optional<ChapSu> findChapSuTruong(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Tìm mục sư
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.capBac) LIKE LOWER('%mục sư%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.thuTuHienThi ASC")
    List<ChapSu> findMucSu(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Tìm truyền đạo
    @Query("SELECT cs FROM ChapSu cs WHERE " +
           "LOWER(cs.capBac) LIKE LOWER('%truyền đạo%') AND " +
           "cs.trangThai = :trangThai " +
           "ORDER BY cs.thuTuHienThi ASC")
    List<ChapSu> findTruyenDao(@Param("trangThai") TrangThaiChapSu trangThai);
    
    // Đếm số lượng theo trạng thái
    long countByTrangThai(TrangThaiChapSu trangThai);
    
    // Thống kê theo cấp bậc
    @Query("SELECT cs.capBac, COUNT(cs) FROM ChapSu cs " +
           "WHERE cs.trangThai = :trangThai " +
           "GROUP BY cs.capBac " +
           "ORDER BY COUNT(cs) DESC")
    List<Object[]> getThongKeTheoCapBac(@Param("trangThai") TrangThaiChapSu trangThai);
}