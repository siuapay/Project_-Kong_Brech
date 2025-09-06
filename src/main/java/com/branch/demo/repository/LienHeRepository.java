package com.branch.demo.repository;

import com.branch.demo.domain.LienHe;
import com.branch.demo.domain.LienHe.LoaiLienHe;
import com.branch.demo.domain.LienHe.TrangThaiLienHe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LienHeRepository extends JpaRepository<LienHe, Long> {
    
    // Tìm theo họ tên
    List<LienHe> findByHoTenContainingIgnoreCase(String hoTen);
    
    // Tìm theo email
    List<LienHe> findByEmailContainingIgnoreCase(String email);
    
    // Tìm theo chủ đề
    List<LienHe> findByChuDeContainingIgnoreCase(String chuDe);
    
    // Tìm theo loại liên hệ
    List<LienHe> findByLoaiLienHe(LoaiLienHe loaiLienHe);
    
    // Tìm theo trạng thái
    List<LienHe> findByTrangThai(TrangThaiLienHe trangThai);
    
    // Tìm theo trạng thái và sắp xếp theo ngày tạo
    List<LienHe> findByTrangThaiOrderByCreatedAtDesc(TrangThaiLienHe trangThai);
    
    // Tìm tin nhắn chưa đọc
    @Query("SELECT lh FROM LienHe lh WHERE " +
           "lh.trangThai = 'CHUA_DOC' " +
           "ORDER BY lh.createdAt DESC")
    List<LienHe> findTinNhanChuaDoc();
    
    // Tìm tin nhắn cần xử lý
    @Query("SELECT lh FROM LienHe lh WHERE " +
           "lh.trangThai IN ('CHUA_DOC', 'DA_DOC', 'DANG_XU_LY') " +
           "ORDER BY lh.createdAt DESC")
    List<LienHe> findTinNhanCanXuLy();
    
    // Tìm theo khoảng thời gian
    List<LienHe> findByCreatedAtBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    // Tìm người đăng ký nhận tin
    List<LienHe> findByDangKyNhanTinTrue();
    
    // Đếm số tin nhắn theo trạng thái
    long countByTrangThai(TrangThaiLienHe trangThai);
    
    // Đếm số tin nhắn chưa đọc
    @Query("SELECT COUNT(lh) FROM LienHe lh WHERE lh.trangThai = 'CHUA_DOC'")
    long countTinNhanChuaDoc();
    
    // Tìm với phân trang
    Page<LienHe> findByHoTenContainingIgnoreCaseOrEmailContainingIgnoreCaseOrChuDeContainingIgnoreCase(
        String hoTen, String email, String chuDe, Pageable pageable);
    
    // Tìm theo nhiều điều kiện
    @Query("SELECT lh FROM LienHe lh WHERE " +
           "(:hoTen IS NULL OR LOWER(lh.hoTen) LIKE LOWER(CONCAT('%', :hoTen, '%'))) AND " +
           "(:email IS NULL OR LOWER(lh.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:chuDe IS NULL OR LOWER(lh.chuDe) LIKE LOWER(CONCAT('%', :chuDe, '%'))) AND " +
           "(:loaiLienHe IS NULL OR lh.loaiLienHe = :loaiLienHe) AND " +
           "(:trangThai IS NULL OR lh.trangThai = :trangThai) AND " +
           "(:tuNgay IS NULL OR lh.createdAt >= :tuNgay) AND " +
           "(:denNgay IS NULL OR lh.createdAt <= :denNgay)")
    Page<LienHe> findByMultipleConditions(
        @Param("hoTen") String hoTen,
        @Param("email") String email,
        @Param("chuDe") String chuDe,
        @Param("loaiLienHe") LoaiLienHe loaiLienHe,
        @Param("trangThai") TrangThaiLienHe trangThai,
        @Param("tuNgay") LocalDateTime tuNgay,
        @Param("denNgay") LocalDateTime denNgay,
        Pageable pageable);
    
    // Thống kê theo loại liên hệ
    @Query("SELECT lh.loaiLienHe, COUNT(lh) FROM LienHe lh " +
           "GROUP BY lh.loaiLienHe " +
           "ORDER BY COUNT(lh) DESC")
    List<Object[]> getThongKeTheoLoaiLienHe();
    
    // Thống kê theo trạng thái
    @Query("SELECT lh.trangThai, COUNT(lh) FROM LienHe lh " +
           "GROUP BY lh.trangThai " +
           "ORDER BY COUNT(lh) DESC")
    List<Object[]> getThongKeTheoTrangThai();
    
    // Thống kê theo tháng
    @Query("SELECT YEAR(lh.createdAt), MONTH(lh.createdAt), COUNT(lh) FROM LienHe lh " +
           "GROUP BY YEAR(lh.createdAt), MONTH(lh.createdAt) " +
           "ORDER BY YEAR(lh.createdAt) DESC, MONTH(lh.createdAt) DESC")
    List<Object[]> getThongKeTheoThang();
}