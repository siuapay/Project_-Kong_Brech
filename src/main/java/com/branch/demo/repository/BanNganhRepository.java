package com.branch.demo.repository;

import com.branch.demo.domain.BanNganh;
import com.branch.demo.domain.BanNganh.TrangThaiBanNganh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BanNganhRepository extends JpaRepository<BanNganh, Long> {

       // Tìm theo tên ban
       List<BanNganh> findByTenBanContainingIgnoreCase(String tenBan);

       // Tìm theo mã ban
       Optional<BanNganh> findByMaBan(String maBan);

       // Tìm theo trạng thái
       List<BanNganh> findByTrangThai(TrangThaiBanNganh trangThai);

       // Tìm theo trạng thái và sắp xếp theo tên
       List<BanNganh> findByTrangThaiOrderByTenBanAsc(TrangThaiBanNganh trangThai);

       // Kiểm tra mã ban có tồn tại không
       boolean existsByMaBan(String maBan);

       // Thống kê số lượng nhân sự theo ban ngành
       @Query("SELECT bn.tenBan, COUNT(ns) FROM BanNganh bn " +
                     "LEFT JOIN bn.danhSachNhanSu ns " +
                     "WHERE bn.trangThai = :trangThai " +
                     "GROUP BY bn.id, bn.tenBan " +
                     "ORDER BY COUNT(ns) DESC")
       List<Object[]> getThongKeSoLuongNhanSu(@Param("trangThai") TrangThaiBanNganh trangThai);

       // Tìm ban ngành có nhân sự
       @Query("SELECT DISTINCT bn FROM BanNganh bn " +
                     "JOIN bn.danhSachNhanSu ns " +
                     "WHERE bn.trangThai = :trangThai")
       List<BanNganh> findBanNganhCoNhanSu(@Param("trangThai") TrangThaiBanNganh trangThai);

       // Tìm kiếm ban ngành với filter nâng cao (chỉ tên ban và mã ban)
       @Query("SELECT bn FROM BanNganh bn WHERE " +
                     "(:search IS NULL OR :search = '' OR " +
                     "LOWER(bn.tenBan) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(bn.maBan) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                     "(:trangThai IS NULL OR bn.trangThai = :trangThai) AND " +
                     "(:fromDate IS NULL OR bn.createdAt >= :fromDate) AND " +
                     "(:toDate IS NULL OR bn.createdAt <= :toDate)")
       org.springframework.data.domain.Page<BanNganh> findWithAdvancedFilters(
                     @Param("search") String search,
                     @Param("trangThai") TrangThaiBanNganh trangThai,
                     @Param("fromDate") java.time.LocalDateTime fromDate,
                     @Param("toDate") java.time.LocalDateTime toDate,
                     org.springframework.data.domain.Pageable pageable);
}