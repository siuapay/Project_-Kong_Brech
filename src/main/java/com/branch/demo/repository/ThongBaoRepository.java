package com.branch.demo.repository;

import com.branch.demo.domain.ThongBao;
import com.branch.demo.domain.ThongBao.LoaiThongBao;
import com.branch.demo.domain.ThongBao.MucDoUuTien;
import com.branch.demo.domain.ThongBao.TrangThaiThongBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    
    // Tìm theo tiêu đề
    List<ThongBao> findByTieuDeContainingIgnoreCase(String tieuDe);
    
    // Tìm theo loại thông báo
    List<ThongBao> findByLoaiThongBao(LoaiThongBao loaiThongBao);
    
    // Tìm theo mức độ ưu tiên
    List<ThongBao> findByMucDoUuTien(MucDoUuTien mucDoUuTien);
    
    // Tìm theo trạng thái
    List<ThongBao> findByTrangThai(TrangThaiThongBao trangThai);
    
    // Tìm theo người gửi
    List<ThongBao> findByNguoiGuiContainingIgnoreCase(String nguoiGui);
    
    // Tìm thông báo đã gửi và chưa hết hạn
    @Query("SELECT tb FROM ThongBao tb WHERE " +
           "tb.trangThai = 'DA_GUI' AND " +
           "(tb.ngayHetHan IS NULL OR tb.ngayHetHan > CURRENT_TIMESTAMP) " +
           "ORDER BY tb.mucDoUuTien DESC, tb.ngayGui DESC")
    List<ThongBao> findThongBaoHienThi();
    
    // Tìm thông báo ưu tiên cao
    @Query("SELECT tb FROM ThongBao tb WHERE " +
           "tb.trangThai = 'DA_GUI' AND " +
           "tb.mucDoUuTien IN ('CAO', 'KHAN_CAP') AND " +
           "(tb.ngayHetHan IS NULL OR tb.ngayHetHan > CURRENT_TIMESTAMP) " +
           "ORDER BY tb.mucDoUuTien DESC, tb.ngayGui DESC")
    List<ThongBao> findThongBaoUuTienCao();
    
    // Tìm thông báo hết hạn
    @Query("SELECT tb FROM ThongBao tb WHERE " +
           "tb.ngayHetHan IS NOT NULL AND " +
           "tb.ngayHetHan <= CURRENT_TIMESTAMP AND " +
           "tb.trangThai = 'DA_GUI'")
    List<ThongBao> findThongBaoHetHan();
    
    // Tìm thông báo theo khoảng thời gian
    List<ThongBao> findByNgayGuiBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    // Tìm thông báo theo đối tượng nhận
    List<ThongBao> findByDoiTuongNhanContainingIgnoreCase(String doiTuongNhan);
    
    // Tìm với phân trang
    Page<ThongBao> findByTieuDeContainingIgnoreCaseOrNoiDungContainingIgnoreCase(
        String tieuDe, String noiDung, Pageable pageable);
    
    // Tìm theo nhiều điều kiện
    @Query("SELECT tb FROM ThongBao tb WHERE " +
           "(:tieuDe IS NULL OR LOWER(tb.tieuDe) LIKE LOWER(CONCAT('%', :tieuDe, '%'))) AND " +
           "(:loaiThongBao IS NULL OR tb.loaiThongBao = :loaiThongBao) AND " +
           "(:mucDoUuTien IS NULL OR tb.mucDoUuTien = :mucDoUuTien) AND " +
           "(:trangThai IS NULL OR tb.trangThai = :trangThai) AND " +
           "(:nguoiGui IS NULL OR LOWER(tb.nguoiGui) LIKE LOWER(CONCAT('%', :nguoiGui, '%'))) AND " +
           "(:tuNgay IS NULL OR tb.ngayGui >= :tuNgay) AND " +
           "(:denNgay IS NULL OR tb.ngayGui <= :denNgay)")
    Page<ThongBao> findByMultipleConditions(
        @Param("tieuDe") String tieuDe,
        @Param("loaiThongBao") LoaiThongBao loaiThongBao,
        @Param("mucDoUuTien") MucDoUuTien mucDoUuTien,
        @Param("trangThai") TrangThaiThongBao trangThai,
        @Param("nguoiGui") String nguoiGui,
        @Param("tuNgay") LocalDateTime tuNgay,
        @Param("denNgay") LocalDateTime denNgay,
        Pageable pageable);
    
    // Thống kê theo loại thông báo
    @Query("SELECT tb.loaiThongBao, COUNT(tb), AVG(tb.soLuotXem) " +
           "FROM ThongBao tb " +
           "WHERE tb.trangThai = :trangThai " +
           "GROUP BY tb.loaiThongBao " +
           "ORDER BY COUNT(tb) DESC")
    List<Object[]> getThongKeTheoLoai(@Param("trangThai") TrangThaiThongBao trangThai);
    
    // Top thông báo được xem nhiều nhất
    @Query("SELECT tb FROM ThongBao tb WHERE " +
           "tb.trangThai = :trangThai " +
           "ORDER BY tb.soLuotXem DESC")
    List<ThongBao> findTopThongBaoXemNhieu(@Param("trangThai") TrangThaiThongBao trangThai, Pageable pageable);
}