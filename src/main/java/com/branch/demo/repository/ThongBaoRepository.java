package com.branch.demo.repository;

import com.branch.demo.domain.ThongBao;
import com.branch.demo.domain.ThongBao.LoaiThongBao;
import com.branch.demo.domain.ThongBao.TrangThaiThongBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    
    // Tìm thông báo theo người nhận
    List<ThongBao> findByNguoiNhanOrderByCreatedAtDesc(String nguoiNhan);
    
    // Tìm thông báo chưa đọc theo người nhận
    List<ThongBao> findByNguoiNhanAndTrangThaiOrderByCreatedAtDesc(String nguoiNhan, TrangThaiThongBao trangThai);
    
    // Đếm thông báo chưa đọc
    long countByNguoiNhanAndTrangThai(String nguoiNhan, TrangThaiThongBao trangThai);
    
    // Tìm thông báo theo người nhận với phân trang
    Page<ThongBao> findByNguoiNhanOrderByCreatedAtDesc(String nguoiNhan, Pageable pageable);
    
    // Tìm thông báo theo loại
    List<ThongBao> findByNguoiNhanAndLoaiThongBaoOrderByCreatedAtDesc(String nguoiNhan, LoaiThongBao loaiThongBao);
    
    // Tìm thông báo mới nhất (10 thông báo gần nhất)
    @Query("SELECT tb FROM ThongBao tb WHERE tb.nguoiNhan = :nguoiNhan ORDER BY tb.createdAt DESC")
    List<ThongBao> findTop10ByNguoiNhanOrderByCreatedAtDesc(@Param("nguoiNhan") String nguoiNhan, Pageable pageable);
    
    // Đánh dấu tất cả thông báo là đã đọc
    @Modifying
    @Query("UPDATE ThongBao tb SET tb.trangThai = :trangThai WHERE tb.nguoiNhan = :nguoiNhan AND tb.trangThai = :trangThaiCu")
    int markAllAsRead(@Param("nguoiNhan") String nguoiNhan, 
                     @Param("trangThai") TrangThaiThongBao trangThai,
                     @Param("trangThaiCu") TrangThaiThongBao trangThaiCu);
    
    // Đánh dấu thông báo cụ thể là đã đọc
    @Modifying
    @Query("UPDATE ThongBao tb SET tb.trangThai = :trangThai WHERE tb.id = :id")
    int markAsRead(@Param("id") Long id, @Param("trangThai") TrangThaiThongBao trangThai);
    
    // Xóa thông báo cũ (quá 30 ngày)
    @Modifying
    @Query("DELETE FROM ThongBao tb WHERE tb.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Tìm thông báo theo đối tượng liên quan
    List<ThongBao> findByDoiTuongIdAndDoiTuongType(Long doiTuongId, String doiTuongType);
    
    // Đếm thông báo theo loại và trạng thái
    long countByNguoiNhanAndLoaiThongBaoAndTrangThai(String nguoiNhan, LoaiThongBao loaiThongBao, TrangThaiThongBao trangThai);
    
    // Tìm thông báo trong khoảng thời gian
    @Query("SELECT tb FROM ThongBao tb WHERE tb.nguoiNhan = :nguoiNhan AND tb.createdAt BETWEEN :fromDate AND :toDate ORDER BY tb.createdAt DESC")
    List<ThongBao> findByNguoiNhanAndCreatedAtBetween(@Param("nguoiNhan") String nguoiNhan, 
                                                     @Param("fromDate") LocalDateTime fromDate, 
                                                     @Param("toDate") LocalDateTime toDate);
    
    // Thống kê thông báo theo ngày
    @Query("SELECT DATE(tb.createdAt) as ngay, COUNT(tb) as soLuong FROM ThongBao tb WHERE tb.nguoiNhan = :nguoiNhan GROUP BY DATE(tb.createdAt) ORDER BY ngay DESC")
    List<Object[]> getThongKeTheoNgay(@Param("nguoiNhan") String nguoiNhan);
}