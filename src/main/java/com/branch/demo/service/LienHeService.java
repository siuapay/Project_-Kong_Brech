package com.branch.demo.service;

import com.branch.demo.domain.LienHe;
import com.branch.demo.domain.Account;
import com.branch.demo.repository.LienHeRepository;
import com.branch.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LienHeService {

    @Autowired
    private LienHeRepository lienHeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ThongBaoService thongBaoService;

    // ==================== CLIENT METHODS ====================

    @Transactional
    public LienHe guiLienHe(LienHe lienHe) {
        lienHe.setTrangThai(LienHe.TrangThaiLienHe.CHUA_XU_LY);
        return lienHeRepository.save(lienHe);
    }

    // ==================== MODERATOR METHODS ====================

    // Methods với phân trang
    public Page<LienHe> getLienHeChuaXuLy(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return lienHeRepository.findLienHeChuaXuLy(pageable);
    }

    public Page<LienHe> getLienHeDangXuLyByModerator(Long moderatorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return lienHeRepository.findLienHeDangXuLyByModerator(moderatorId, pageable);
    }

    public Page<LienHe> getLienHeByModerator(Long moderatorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return lienHeRepository.findLienHeDaXuLyByModerator(moderatorId, pageable);
    }

    // public Page<LienHe> getLienHeChoAdminXuLy(int page, int size) {
    // Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,
    // "ngayBaoCao"));
    // return lienHeRepository.findLienHeChoAdminXuLy(pageable);
    // }

    // Phân trang với sắp xếp mới nhất trước
    // public Page<LienHe> getLienHeChuaXuLy(int page, int size) {
    // Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,
    // "createdAt"));
    // return lienHeRepository.findLienHeChuaXuLy(pageable);
    // }

    // Backward compatibility - không phân trang
    public List<LienHe> getLienHeChuaXuLy() {
        try {
            List<LienHe> result = lienHeRepository.findByTrangThai(LienHe.TrangThaiLienHe.CHUA_XU_LY);
            // Sắp xếp mới nhất trước
            result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            return result;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    // Methods không phân trang cho các tab khác
    public List<LienHe> getLienHeDangXuLyByModerator(Long moderatorId) {
        try {
            List<LienHe> result = lienHeRepository.findAll().stream()
                    .filter(lh -> lh.getTrangThai() == LienHe.TrangThaiLienHe.DANG_XU_LY &&
                            lh.getModeratorXuLy() != null &&
                            lh.getModeratorXuLy().getId().equals(moderatorId))
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());
            return result;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<LienHe> getLienHeDaXuLyByModerator(Long moderatorId) {
        try {
            List<LienHe> result = lienHeRepository.findAll().stream()
                    .filter(lh -> (lh.getTrangThai() == LienHe.TrangThaiLienHe.DA_XU_LY ||
                            lh.getTrangThai() == LienHe.TrangThaiLienHe.CHO_ADMIN_XU_LY) &&
                            lh.getModeratorXuLy() != null &&
                            lh.getModeratorXuLy().getId().equals(moderatorId))
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());
            return result;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    // Phân trang cho ADMIN
    public Page<LienHe> getLienHeChoAdminXuLy(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lienHeRepository.findLienHeChoAdminXuLy(pageable);
    }
    
    // Phân trang cho ADMIN với search
    public Page<LienHe> getLienHeChoAdminXuLyWithSearch(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lienHeRepository.findLienHeChoAdminXuLyWithSearch(search, pageable);
    }

    public List<LienHe> getLienHeChoAdminXuLy() {
        try {
            List<LienHe> result = lienHeRepository.findAll().stream()
                    .filter(lh -> lh.getTrangThai() == LienHe.TrangThaiLienHe.CHO_ADMIN_XU_LY &&
                            lh.getCoViPham() != null && lh.getCoViPham())
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());
            return result;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @Transactional
    public LienHe batDauXuLy(Long lienHeId, Long moderatorId) {
        LienHe lienHe = getLienHeById(lienHeId);
        Account moderator = accountRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy moderator"));

        if (lienHe.getTrangThai() != LienHe.TrangThaiLienHe.CHUA_XU_LY) {
            throw new RuntimeException("Liên hệ này đã được xử lý hoặc đang được xử lý");
        }

        // Chỉ set trạng thái DANG_XU_LY và moderator, KHÔNG set ngày xử lý
        lienHe.setTrangThai(LienHe.TrangThaiLienHe.DANG_XU_LY);
        lienHe.setModeratorXuLy(moderator);
        // KHÔNG set ngayXuLy ở đây - chỉ set khi hoàn thành

        return lienHeRepository.save(lienHe);
    }

    @Transactional
    public LienHe xacNhanXuLy(Long lienHeId, Long moderatorId, String ghiChu) {
        LienHe lienHe = getLienHeById(lienHeId);
        Account moderator = accountRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy moderator"));

        // Kiểm tra quyền xử lý
        if (lienHe.getModeratorXuLy() == null || !lienHe.getModeratorXuLy().getId().equals(moderatorId)) {
            throw new RuntimeException("Bạn không có quyền xử lý liên hệ này");
        }

        lienHe.xacNhanXuLy(moderator, ghiChu);
        return lienHeRepository.save(lienHe);
    }

    @Transactional
    public LienHe baoCaoViPham(Long lienHeId, Long moderatorId, String lyDoViPham) {
        LienHe lienHe = getLienHeById(lienHeId);
        Account moderator = accountRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy moderator"));

        // Kiểm tra quyền báo cáo
        if (lienHe.getModeratorXuLy() == null || !lienHe.getModeratorXuLy().getId().equals(moderatorId)) {
            throw new RuntimeException("Bạn không có quyền báo cáo liên hệ này");
        }

        lienHe.baoCaoViPham(moderator, lyDoViPham);
        LienHe savedLienHe = lienHeRepository.save(lienHe);
        
        // Tạo thông báo cho admin
        try {
            thongBaoService.taoThongBaoBaoCaoViPham(savedLienHe, moderator.getUsername(), lyDoViPham);
        } catch (Exception e) {
            System.err.println("Lỗi tạo thông báo báo cáo vi phạm: " + e.getMessage());
        }
        
        return savedLienHe;
    }

    // ==================== ADMIN METHODS ====================

    // public List<LienHe> getLienHeChoAdminXuLy() {
    // return lienHeRepository.findLienHeChoAdminXuLy();
    // }

    @Transactional
    public LienHe adminXuLyViPham(Long lienHeId, Long adminId, LienHe.QuyetDinhAdmin quyetDinh) {
        LienHe lienHe = getLienHeById(lienHeId);
        Account admin = accountRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));

        if (!lienHe.isChoAdminXuLy()) {
            throw new RuntimeException("Liên hệ này không cần admin xử lý");
        }

        lienHe.adminXuLyViPham(admin, quyetDinh);

        // Nếu quyết định xóa, thực hiện xóa khỏi database
        if (quyetDinh == LienHe.QuyetDinhAdmin.XOA_LIEN_HE) {
            lienHeRepository.delete(lienHe);
            return null; // Đã xóa
        }

        return lienHeRepository.save(lienHe);
    }

    // ==================== COMMON METHODS ====================

    public LienHe getLienHeById(Long id) {
        return lienHeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy liên hệ với ID: " + id));
    }

    public Page<LienHe> getAllLienHe(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return lienHeRepository.findAll(pageable);
    }

    public Page<LienHe> searchLienHe(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return lienHeRepository.findAll(pageable);
        }
        return lienHeRepository.findByHoTenContainingIgnoreCaseOrEmailContainingIgnoreCaseOrChuDeContainingIgnoreCase(
                search, search, search, pageable);
    }
    
    /**
     * Lấy tất cả liên hệ cho admin (trừ CHO_ADMIN_XU_LY)
     */
    public Page<LienHe> getAllLienHeForAdmin(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        if (search == null || search.trim().isEmpty()) {
            return lienHeRepository.findByTrangThaiIn(
                java.util.Arrays.asList(
                    LienHe.TrangThaiLienHe.CHUA_XU_LY,
                    LienHe.TrangThaiLienHe.DANG_XU_LY,
                    LienHe.TrangThaiLienHe.DA_XU_LY
                ), pageable);
        } else {
            return lienHeRepository.findByTrangThaiInAndSearchText(
                java.util.Arrays.asList(
                    LienHe.TrangThaiLienHe.CHUA_XU_LY,
                    LienHe.TrangThaiLienHe.DANG_XU_LY,
                    LienHe.TrangThaiLienHe.DA_XU_LY
                ), search, pageable);
        }
    }

    // ==================== STATISTICS ====================

    public long countLienHeChuaXuLy() {
        return lienHeRepository.countLienHeChuaXuLy();
    }

    public long countLienHeChoAdminXuLy() {
        return lienHeRepository.countLienHeChoAdminXuLy();
    }
    
    public long countAllLienHeForAdmin() {
        return lienHeRepository.countByTrangThaiIn(
            java.util.Arrays.asList(
                LienHe.TrangThaiLienHe.CHUA_XU_LY,
                LienHe.TrangThaiLienHe.DANG_XU_LY,
                LienHe.TrangThaiLienHe.DA_XU_LY
            )
        );
    }

    public long countByTrangThai(LienHe.TrangThaiLienHe trangThai) {
        return lienHeRepository.countByTrangThai(trangThai);
    }
    
    /**
     * Xóa liên hệ (hard delete)
     */
    @Transactional
    public void deleteLienHe(Long id) {
        LienHe lienHe = getLienHeById(id);
        lienHeRepository.delete(lienHe);
    }
}