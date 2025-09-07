package com.branch.demo.service;

import com.branch.demo.repository.*;
import com.branch.demo.domain.DiemNhom;
import com.branch.demo.dto.SuKienDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private TinHuuRepository tinHuuRepository;

    @Autowired
    private NhomRepository nhomRepository;

    @Autowired
    private BanNganhRepository banNganhRepository;

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @Autowired
    private SuKienRepository suKienRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private TaiChinhRepository taiChinhRepository;

    @Autowired
    private ChapSuRepository chapSuRepository;

    @Autowired
    private LienHeRepository lienHeRepository;

    @Autowired
    private DiemNhomRepository diemNhomRepository;

    @Autowired
    private LoaiSuKienRepository loaiSuKienRepository;

    @Autowired
    private FileUploadService fileUploadService;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTinHuu", tinHuuRepository.countActive());
        stats.put("deletedTinHuu", tinHuuRepository.countDeleted());
        stats.put("totalNhom", nhomRepository.count());
        stats.put("totalBanNganh", banNganhRepository.count());
        stats.put("totalNhanSu", nhanSuRepository.count());
        stats.put("totalSuKien", suKienRepository.count());
        stats.put("totalThongBao", thongBaoRepository.count());
        stats.put("totalTaiChinh", taiChinhRepository.count());
        stats.put("totalChapSu", chapSuRepository.count());
        stats.put("totalLienHe", lienHeRepository.count());
        return stats;
    }

    // Tin Hữu Management Methods
    public Page<com.branch.demo.domain.TinHuu> getTinHuuPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return tinHuuRepository.findActiveWithSearch(search, pageable);
    }

    // Tin Hữu với filter nâng cao
    public Page<com.branch.demo.domain.TinHuu> getTinHuuPageWithFilters(int page, String search,
            String trangThai, Long diemNhomId, Long nhomId,
            java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return tinHuuRepository.findActiveWithAdvancedFilters(search, trangThai, diemNhomId, nhomId, fromDate, toDate,
                pageable);
    }

    public Page<com.branch.demo.domain.TinHuu> getDeletedTinHuuPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "deletedAt"));
        return tinHuuRepository.findDeletedWithSearch(search, pageable);
    }

    public com.branch.demo.domain.TinHuu getTinHuuById(Long id) {
        return tinHuuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin hữu với ID: " + id));
    }

    public com.branch.demo.domain.TinHuu saveTinHuu(com.branch.demo.domain.TinHuu tinHuu) {
        // Xử lý null cho các field không bắt buộc (chỉ hoTen là bắt buộc)
        if (tinHuu.getGioiTinh() != null && tinHuu.getGioiTinh().trim().isEmpty()) {
            tinHuu.setGioiTinh(null);
        }
        if (tinHuu.getEmail() != null && tinHuu.getEmail().trim().isEmpty()) {
            tinHuu.setEmail(null);
        }
        if (tinHuu.getDienThoai() != null && tinHuu.getDienThoai().trim().isEmpty()) {
            tinHuu.setDienThoai(null);
        }
        if (tinHuu.getDiaChi() != null && tinHuu.getDiaChi().trim().isEmpty()) {
            tinHuu.setDiaChi(null);
        }
        if (tinHuu.getNgheNghiep() != null && tinHuu.getNgheNghiep().trim().isEmpty()) {
            tinHuu.setNgheNghiep(null);
        }
        if (tinHuu.getGhiChu() != null && tinHuu.getGhiChu().trim().isEmpty()) {
            tinHuu.setGhiChu(null);
        }
        if (tinHuu.getTinhTrangHonNhan() != null && tinHuu.getTinhTrangHonNhan().trim().isEmpty()) {
            tinHuu.setTinhTrangHonNhan(null);
        }

        // Nếu là update (có ID), giữ nguyên createdAt
        if (tinHuu.getId() != null) {
            com.branch.demo.domain.TinHuu existingTinHuu = tinHuuRepository.findById(tinHuu.getId()).orElse(null);
            if (existingTinHuu != null && existingTinHuu.getCreatedAt() != null) {
                tinHuu.setCreatedAt(existingTinHuu.getCreatedAt());
            }
        }
        return tinHuuRepository.save(tinHuu);
    }

    // Soft Delete Methods
    public void softDeleteTinHuu(Long id) {
        com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(id);
        if (tinHuu.isDeleted()) {
            throw new RuntimeException("Tin hữu đã được xóa trước đó");
        }
        tinHuu.softDelete();
        tinHuuRepository.save(tinHuu);
    }

    public void restoreTinHuu(Long id) {
        com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(id);
        if (!tinHuu.isDeleted()) {
            throw new RuntimeException("Tin hữu chưa bị xóa");
        }
        tinHuu.restore();
        tinHuuRepository.save(tinHuu);
    }

    public void hardDeleteTinHuu(Long id) {
        com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(id);
        if (!tinHuu.isDeleted()) {
            throw new RuntimeException("Chỉ có thể xóa vĩnh viễn tin hữu đã được xóa mềm");
        }
        tinHuuRepository.deleteById(id);
    }

    // Legacy method for backward compatibility
    public void deleteTinHuu(Long id) {
        softDeleteTinHuu(id);
    }

    // // ==================== NHÓM MANAGEMENT METHODS ====================

    // public Page<com.branch.demo.domain.Nhom> getNhomPage(int page, String search) {
    //     Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    //     if (search == null || search.trim().isEmpty()) {
    //         return nhomRepository.findAll(pageable);
    //     }
    //     return nhomRepository.findByTenNhomContainingIgnoreCase(search).stream()
    //             .collect(java.util.stream.Collectors.collectingAndThen(
    //                     java.util.stream.Collectors.toList(),
    //                     list -> new org.springframework.data.domain.PageImpl<>(
    //                             list.stream().skip(pageable.getOffset()).limit(pageable.getPageSize())
    //                                     .collect(java.util.stream.Collectors.toList()),
    //                             pageable,
    //                             list.size())));
    // }

    // public com.branch.demo.domain.Nhom getNhomById(Long id) {
    //     return nhomRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm với ID: " + id));
    // }

    // public com.branch.demo.domain.Nhom saveNhom(com.branch.demo.domain.Nhom nhom) {
    //     // Xử lý null cho mô tả (field không bắt buộc)
    //     if (nhom.getMoTa() != null && nhom.getMoTa().trim().isEmpty()) {
    //         nhom.setMoTa(null);
    //     }

    //     // Nếu có điểm nhóm ID, tìm và set điểm nhóm
    //     if (nhom.getDiemNhom() != null && nhom.getDiemNhom().getId() != null) {
    //         com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(nhom.getDiemNhom().getId())
    //                 .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));
    //         nhom.setDiemNhom(diemNhom);
    //     }

    //     // Nếu là update (có ID), giữ nguyên createdAt
    //     if (nhom.getId() != null) {
    //         com.branch.demo.domain.Nhom existingNhom = nhomRepository.findById(nhom.getId()).orElse(null);
    //         if (existingNhom != null && existingNhom.getCreatedAt() != null) {
    //             nhom.setCreatedAt(existingNhom.getCreatedAt());
    //         }
    //     }

    //     return nhomRepository.save(nhom);
    // }

    // public void deleteNhom(Long id) {
    //     com.branch.demo.domain.Nhom nhom = getNhomById(id);

    //     // Set nhóm của các tin hữu thành null trước khi xóa nhóm
    //     if (!nhom.getDanhSachTinHuu().isEmpty()) {
    //         for (com.branch.demo.domain.TinHuu tinHuu : nhom.getDanhSachTinHuu()) {
    //             tinHuu.setNhom(null);
    //             tinHuuRepository.save(tinHuu);
    //         }
    //     }

    //     nhomRepository.deleteById(id);
    // }

    public java.util.List<com.branch.demo.domain.DiemNhom> getAllActiveDiemNhom() {
        return diemNhomRepository.findByTrangThai(com.branch.demo.domain.DiemNhom.TrangThaiDiemNhom.HOAT_DONG);
    }

    public java.util.List<com.branch.demo.domain.BanNganh> getAllActiveBanNganh() {
        return banNganhRepository.findByTrangThai(com.branch.demo.domain.BanNganh.TrangThaiBanNganh.HOAT_DONG);
    }

    // ==================== ĐIỂM NHÓM MANAGEMENT METHODS ====================

    public Page<com.branch.demo.domain.DiemNhom> getDiemNhomPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return diemNhomRepository.findAll(pageable);
        }
        // Sử dụng query nâng cao để tìm theo tên và địa chỉ
        return diemNhomRepository.findWithAdvancedFilters(search, null, null, null, pageable);
    }

    // Điểm Nhóm với filter nâng cao
    public Page<com.branch.demo.domain.DiemNhom> getDiemNhomPageWithFilters(int page, String search,
            String trangThai, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert LocalDate to LocalDateTime
        java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Convert String to Enum
        com.branch.demo.domain.DiemNhom.TrangThaiDiemNhom trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.DiemNhom.TrangThaiDiemNhom.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Invalid enum value, ignore
            }
        }

        return diemNhomRepository.findWithAdvancedFilters(search, trangThaiEnum, fromDateTime, toDateTime, pageable);
    }

    public com.branch.demo.domain.DiemNhom getDiemNhomById(Long id) {
        return diemNhomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm với ID: " + id));
    }

    public com.branch.demo.domain.DiemNhom saveDiemNhom(com.branch.demo.domain.DiemNhom diemNhom) {
        // Xử lý null cho các field không bắt buộc
        if (diemNhom.getMoTa() != null && diemNhom.getMoTa().trim().isEmpty()) {
            diemNhom.setMoTa(null);
        }

        // Nếu có ban ngành ID, tìm và set ban ngành
        if (diemNhom.getBanNganh() != null && diemNhom.getBanNganh().getId() != null) {
            com.branch.demo.domain.BanNganh banNganh = banNganhRepository.findById(diemNhom.getBanNganh().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ban ngành"));
            diemNhom.setBanNganh(banNganh);
        } else {
            diemNhom.setBanNganh(null);
        }

        // Nếu là update (có ID), giữ nguyên createdAt
        if (diemNhom.getId() != null) {
            com.branch.demo.domain.DiemNhom existingDiemNhom = diemNhomRepository.findById(diemNhom.getId())
                    .orElse(null);
            if (existingDiemNhom != null && existingDiemNhom.getCreatedAt() != null) {
                diemNhom.setCreatedAt(existingDiemNhom.getCreatedAt());
            }
        }

        return diemNhomRepository.save(diemNhom);
    }

    public void deleteDiemNhom(Long id) {
        com.branch.demo.domain.DiemNhom diemNhom = getDiemNhomById(id);

        // Set điểm nhóm của các nhóm thành null trước khi xóa
        if (!diemNhom.getDanhSachNhom().isEmpty()) {
            for (com.branch.demo.domain.Nhom nhom : diemNhom.getDanhSachNhom()) {
                nhom.setDiemNhom(null);
                nhomRepository.save(nhom);
            }
        }

        // Set điểm nhóm của các nhân sự thành null trước khi xóa
        if (!diemNhom.getDanhSachNhanSu().isEmpty()) {
            for (com.branch.demo.domain.NhanSuDiemNhom nhanSu : diemNhom.getDanhSachNhanSu()) {
                nhanSu.setDiemNhom(null);
                // Note: Cần có NhanSuDiemNhomRepository để save
            }
        }

        diemNhomRepository.deleteById(id);
    }

    public java.util.List<com.branch.demo.domain.Nhom> getAllActiveNhom() {
        return nhomRepository.findAll();
    }

    // ==================== NHÓM MANAGEMENT METHODS ====================

    public Page<com.branch.demo.domain.Nhom> getNhomPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return nhomRepository.findAll(pageable);
        }
        // Tìm theo tên nhóm và mô tả
        return nhomRepository.findByTenNhomContainingIgnoreCaseOrMoTaContainingIgnoreCase(search, search, pageable);
    }

    public Page<com.branch.demo.domain.Nhom> getNhomPageWithFilters(int page, String search,
            String trangThai, Long diemNhomId, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert LocalDate to LocalDateTime
        java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Convert String to Enum
        com.branch.demo.domain.Nhom.TrangThaiNhom trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.Nhom.TrangThaiNhom.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Invalid enum value, ignore
            }
        }

        return nhomRepository.findWithAdvancedFilters(search, trangThaiEnum, diemNhomId, fromDateTime, toDateTime, pageable);
    }

    public com.branch.demo.domain.Nhom getNhomById(Long id) {
        return nhomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm với ID: " + id));
    }

    public com.branch.demo.domain.Nhom saveNhom(com.branch.demo.domain.Nhom nhom) {
        // Xử lý null cho các field không bắt buộc
        if (nhom.getMoTa() != null && nhom.getMoTa().trim().isEmpty()) {
            nhom.setMoTa(null);
        }
        if (nhom.getThoiGianSinhHoat() != null && nhom.getThoiGianSinhHoat().trim().isEmpty()) {
            nhom.setThoiGianSinhHoat(null);
        }

        // Nếu có điểm nhóm ID, tìm và set điểm nhóm
        if (nhom.getDiemNhom() != null && nhom.getDiemNhom().getId() != null) {
            com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(nhom.getDiemNhom().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));
            nhom.setDiemNhom(diemNhom);
        } else {
            nhom.setDiemNhom(null);
        }

        // Nếu là update (có ID), giữ nguyên createdAt
        if (nhom.getId() != null) {
            com.branch.demo.domain.Nhom existingNhom = nhomRepository.findById(nhom.getId())
                    .orElse(null);
            if (existingNhom != null && existingNhom.getCreatedAt() != null) {
                nhom.setCreatedAt(existingNhom.getCreatedAt());
            }
        }

        return nhomRepository.save(nhom);
    }

    public void deleteNhom(Long id) {
        com.branch.demo.domain.Nhom nhom = getNhomById(id);

        // Set nhóm của các tin hữu thành null trước khi xóa
        if (!nhom.getDanhSachTinHuu().isEmpty()) {
            for (com.branch.demo.domain.TinHuu tinHuu : nhom.getDanhSachTinHuu()) {
                tinHuu.setNhom(null);
                tinHuuRepository.save(tinHuu);
            }
        }

        nhomRepository.deleteById(id);
    }

    public java.util.List<java.util.Map<String, Object>> getTinHuuByNhomId(Long nhomId) {
        com.branch.demo.domain.Nhom nhom = getNhomById(nhomId);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        for (com.branch.demo.domain.TinHuu tinHuu : nhom.getDanhSachTinHuu()) {
            java.util.Map<String, Object> tinHuuData = new java.util.HashMap<>();
            tinHuuData.put("id", tinHuu.getId());
            tinHuuData.put("hoTen", tinHuu.getHoTen());
            tinHuuData.put("namSinh", tinHuu.getNamSinh());
            tinHuuData.put("gioiTinh", tinHuu.getGioiTinh());
            tinHuuData.put("avatarUrl", tinHuu.getAvatarUrl());
            result.add(tinHuuData);
        }
        
        return result;
    }

    public java.util.List<java.util.Map<String, Object>> getTinHuuByDiemNhomId(Long diemNhomId) {
        com.branch.demo.domain.DiemNhom diemNhom = getDiemNhomById(diemNhomId);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        // Lấy tất cả tin hữu từ tất cả nhóm trong điểm nhóm
        for (com.branch.demo.domain.Nhom nhom : diemNhom.getDanhSachNhom()) {
            for (com.branch.demo.domain.TinHuu tinHuu : nhom.getDanhSachTinHuu()) {
                java.util.Map<String, Object> tinHuuData = new java.util.HashMap<>();
                tinHuuData.put("id", tinHuu.getId());
                tinHuuData.put("hoTen", tinHuu.getHoTen());
                tinHuuData.put("namSinh", tinHuu.getNamSinh());
                tinHuuData.put("gioiTinh", tinHuu.getGioiTinh());
                tinHuuData.put("avatarUrl", tinHuu.getAvatarUrl());
                tinHuuData.put("nhomTen", nhom.getTenNhom()); // Thêm tên nhóm
                result.add(tinHuuData);
            }
        }
        
        return result;
    }

    // ==================== BAN NGÀNH MANAGEMENT METHODS ====================

    public Page<com.branch.demo.domain.BanNganh> getBanNganhPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return banNganhRepository.findAll(pageable);
        }
        // Sử dụng query nâng cao để tìm theo tên ban và mã ban
        return banNganhRepository.findWithAdvancedFilters(search, null, null, null, pageable);
    }

    // Ban Ngành với filter nâng cao
    public Page<com.branch.demo.domain.BanNganh> getBanNganhPageWithFilters(int page, String search,
            String trangThai, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert LocalDate to LocalDateTime
        java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Convert String to Enum
        com.branch.demo.domain.BanNganh.TrangThaiBanNganh trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.BanNganh.TrangThaiBanNganh.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Invalid enum value, ignore
            }
        }

        return banNganhRepository.findWithAdvancedFilters(search, trangThaiEnum, fromDateTime, toDateTime, pageable);
    }

    public com.branch.demo.domain.BanNganh getBanNganhById(Long id) {
        return banNganhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ban ngành với ID: " + id));
    }

    public com.branch.demo.domain.BanNganh saveBanNganh(com.branch.demo.domain.BanNganh banNganh) {
        // Convert mã ban thành chữ in hoa
        if (banNganh.getMaBan() != null) {
            banNganh.setMaBan(banNganh.getMaBan().toUpperCase().trim());
        }

        // Xử lý null cho các field không bắt buộc
        if (banNganh.getMoTa() != null && banNganh.getMoTa().trim().isEmpty()) {
            banNganh.setMoTa(null);
        }
        if (banNganh.getEmailLienHe() != null && banNganh.getEmailLienHe().trim().isEmpty()) {
            banNganh.setEmailLienHe(null);
        }
        if (banNganh.getDienThoaiLienHe() != null && banNganh.getDienThoaiLienHe().trim().isEmpty()) {
            banNganh.setDienThoaiLienHe(null);
        }

        // Nếu là update (có ID), giữ nguyên createdAt
        if (banNganh.getId() != null) {
            com.branch.demo.domain.BanNganh existingBanNganh = banNganhRepository.findById(banNganh.getId())
                    .orElse(null);
            if (existingBanNganh != null && existingBanNganh.getCreatedAt() != null) {
                banNganh.setCreatedAt(existingBanNganh.getCreatedAt());
            }
        }

        return banNganhRepository.save(banNganh);
    }

    public void deleteBanNganh(Long id) {
        com.branch.demo.domain.BanNganh banNganh = getBanNganhById(id);

        // Set ban ngành của các nhân sự thành null trước khi xóa
        if (!banNganh.getDanhSachNhanSu().isEmpty()) {
            for (com.branch.demo.domain.NhanSu nhanSu : banNganh.getDanhSachNhanSu()) {
                nhanSu.setBanNganh(null);
                nhanSuRepository.save(nhanSu);
            }
        }

        banNganhRepository.deleteById(id);
    }

    // // Nhóm với filter nâng cao
    // public Page<com.branch.demo.domain.Nhom> getNhomPageWithFilters(int page, String search,
    //         String trangThai, Long diemNhomId,
    //         java.time.LocalDate fromDate, java.time.LocalDate toDate) {
    //     Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    //     // Convert LocalDate to LocalDateTime
    //     java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
    //     java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

    //     // Convert String to Enum
    //     com.branch.demo.domain.Nhom.TrangThaiNhom trangThaiEnum = null;
    //     if (trangThai != null && !trangThai.isEmpty()) {
    //         try {
    //             trangThaiEnum = com.branch.demo.domain.Nhom.TrangThaiNhom.valueOf(trangThai);
    //         } catch (IllegalArgumentException e) {
    //             // Invalid enum value, ignore
    //         }
    //     }

    //     return nhomRepository.findWithAdvancedFilters(search, trangThaiEnum, diemNhomId, fromDateTime, toDateTime,
    //             pageable);
    // }

    // ==================== NHÂN SỰ MANAGEMENT METHODS ====================

    public Page<com.branch.demo.domain.NhanSu> getNhanSuPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return nhanSuRepository.findAll(pageable);
        }
        return nhanSuRepository.findWithAdvancedFilters(search, null, null, null, null, null, pageable);
    }

    // Nhân Sự với filter nâng cao
    public Page<com.branch.demo.domain.NhanSu> getNhanSuPageWithFilters(int page, String search,
            String trangThai, Long banNganhId, Long diemNhomId,
            java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert LocalDate to LocalDateTime
        java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Convert String to Enum
        com.branch.demo.domain.NhanSu.TrangThaiNhanSu trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.NhanSu.TrangThaiNhanSu.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Invalid enum value, ignore
            }
        }

        return nhanSuRepository.findWithAdvancedFilters(search, trangThaiEnum, banNganhId, diemNhomId, fromDateTime,
                toDateTime, pageable);
    }

    public com.branch.demo.domain.NhanSu getNhanSuById(Long id) {
        return nhanSuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân sự với ID: " + id));
    }

    public com.branch.demo.domain.NhanSu saveNhanSu(com.branch.demo.domain.NhanSu nhanSu) {
        // Xử lý null cho các field không bắt buộc
        if (nhanSu.getEmail() != null && nhanSu.getEmail().trim().isEmpty()) {
            nhanSu.setEmail(null);
        }
        if (nhanSu.getDienThoai() != null && nhanSu.getDienThoai().trim().isEmpty()) {
            nhanSu.setDienThoai(null);
        }
        if (nhanSu.getDiaChi() != null && nhanSu.getDiaChi().trim().isEmpty()) {
            nhanSu.setDiaChi(null);
        }
        if (nhanSu.getChucVu() != null && nhanSu.getChucVu().trim().isEmpty()) {
            nhanSu.setChucVu(null);
        }
        if (nhanSu.getGhiChu() != null && nhanSu.getGhiChu().trim().isEmpty()) {
            nhanSu.setGhiChu(null);
        }
        if (nhanSu.getMoTaCongViec() != null && nhanSu.getMoTaCongViec().trim().isEmpty()) {
            nhanSu.setMoTaCongViec(null);
        }

        // Nếu có ban ngành ID, tìm và set ban ngành
        if (nhanSu.getBanNganh() != null && nhanSu.getBanNganh().getId() != null) {
            com.branch.demo.domain.BanNganh banNganh = banNganhRepository.findById(nhanSu.getBanNganh().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ban ngành"));
            nhanSu.setBanNganh(banNganh);
        }

        // Nếu có điểm nhóm ID, tìm và set điểm nhóm
        if (nhanSu.getDiemNhom() != null && nhanSu.getDiemNhom().getId() != null) {
            com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(nhanSu.getDiemNhom().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));
            nhanSu.setDiemNhom(diemNhom);
        } else {
            nhanSu.setDiemNhom(null);
        }

        // Nếu là update (có ID), giữ nguyên createdAt
        if (nhanSu.getId() != null) {
            com.branch.demo.domain.NhanSu existingNhanSu = nhanSuRepository.findById(nhanSu.getId()).orElse(null);
            if (existingNhanSu != null && existingNhanSu.getCreatedAt() != null) {
                nhanSu.setCreatedAt(existingNhanSu.getCreatedAt());
            }
        }

        return nhanSuRepository.save(nhanSu);
    }

    public void deleteNhanSu(Long id) {
        com.branch.demo.domain.NhanSu nhanSu = getNhanSuById(id);
        nhanSuRepository.deleteById(id);
    }

    public java.util.List<com.branch.demo.domain.DiemNhom> getDiemNhomByBanNganh(Long banNganhId) {
        return diemNhomRepository.findByBanNganhId(banNganhId);
    }

    // Convert DiemNhom entity to DTO for API responses
    public java.util.List<com.branch.demo.dto.DiemNhomDTO> getDiemNhomDTOByBanNganh(Long banNganhId) {
        java.util.List<com.branch.demo.domain.DiemNhom> diemNhomList = diemNhomRepository.findByBanNganhId(banNganhId);
        return diemNhomList.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<com.branch.demo.dto.DiemNhomDTO> getAllActiveDiemNhomDTO() {
        java.util.List<com.branch.demo.domain.DiemNhom> diemNhomList = getAllActiveDiemNhom();
        return diemNhomList.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public com.branch.demo.dto.DiemNhomDTO convertToDTO(com.branch.demo.domain.DiemNhom diemNhom) {
        com.branch.demo.dto.DiemNhomDTO dto = new com.branch.demo.dto.DiemNhomDTO();
        dto.setId(diemNhom.getId());
        dto.setTenDiemNhom(diemNhom.getTenDiemNhom());
        dto.setDiaChi(diemNhom.getDiaChi());
        dto.setMoTa(diemNhom.getMoTa());

        // Convert BanNganh if exists
        if (diemNhom.getBanNganh() != null) {
            com.branch.demo.dto.DiemNhomDTO.BanNganhDTO banNganhDTO = new com.branch.demo.dto.DiemNhomDTO.BanNganhDTO();
            banNganhDTO.setId(diemNhom.getBanNganh().getId());
            banNganhDTO.setTenBan(diemNhom.getBanNganh().getTenBan());
            banNganhDTO.setMaBan(diemNhom.getBanNganh().getMaBan());
            dto.setBanNganh(banNganhDTO);
        }

        return dto;
    }

    // BanNganh DTO methods
    public java.util.List<com.branch.demo.dto.BanNganhDTO> getAllActiveBanNganhDTO() {
        java.util.List<com.branch.demo.domain.BanNganh> banNganhList = getAllActiveBanNganh();
        return banNganhList.stream()
                .map(this::convertToBanNganhDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private com.branch.demo.dto.BanNganhDTO convertToBanNganhDTO(com.branch.demo.domain.BanNganh banNganh) {
        com.branch.demo.dto.BanNganhDTO dto = new com.branch.demo.dto.BanNganhDTO();
        dto.setId(banNganh.getId());
        dto.setTenBan(banNganh.getTenBan());
        dto.setMaBan(banNganh.getMaBan());
        dto.setMoTa(banNganh.getMoTa());
        return dto;
    }

    // ==================== SU KIEN METHODS ====================
    
    // Get all SuKien with pagination
    public Page<com.branch.demo.domain.SuKien> getAllSuKien(Pageable pageable) {
        return suKienRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    public Page<com.branch.demo.domain.SuKien> searchSuKien(String search, String loaiSuKienId, String trangThai, 
                                     String fromDate, String toDate, Pageable pageable) {
        LocalDate fromLocalDate = null;
        LocalDate toLocalDate = null;
        
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                fromLocalDate = LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                toLocalDate = LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Invalid date format, ignore
        }

        Long loaiSuKienIdLong = null;
        if (loaiSuKienId != null && !loaiSuKienId.isEmpty()) {
            try {
                loaiSuKienIdLong = Long.parseLong(loaiSuKienId);
            } catch (NumberFormatException e) {
                // Invalid number format, ignore
            }
        }

        com.branch.demo.domain.SuKien.TrangThaiSuKien trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.SuKien.TrangThaiSuKien.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Invalid enum value, ignore
            }
        }

        return suKienRepository.searchSuKien(search, loaiSuKienIdLong, trangThaiEnum, 
                                           fromLocalDate, toLocalDate, pageable);
    }

    public com.branch.demo.domain.SuKien getSuKienById(Long id) {
        return suKienRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Sự kiện không tồn tại"));
    }

    public com.branch.demo.domain.SuKien saveSuKien(SuKienDTO suKienDTO) {
        com.branch.demo.domain.SuKien suKien;
        
        if (suKienDTO.getId() != null) {
            // Update existing
            suKien = getSuKienById(suKienDTO.getId());
        } else {
            // Create new
            suKien = new com.branch.demo.domain.SuKien();
        }
        
        // Validate and get LoaiSuKien
        if (suKienDTO.getLoaiSuKienId() != null) {
            com.branch.demo.domain.LoaiSuKien loaiSuKien = getLoaiSuKienById(suKienDTO.getLoaiSuKienId());
            if (!loaiSuKien.isKichHoat()) {
                throw new RuntimeException("Loại sự kiện đã bị vô hiệu hóa");
            }
            suKien.setLoaiSuKien(loaiSuKien);
        } else {
            throw new RuntimeException("Vui lòng chọn loại sự kiện");
        }
        
        // Map DTO to entity
        suKien.setTenSuKien(suKienDTO.getTenSuKien());
        suKien.setMoTa(suKienDTO.getMoTa());
        suKien.setNoiDung(suKienDTO.getNoiDung());
        suKien.setNgayDienRa(suKienDTO.getNgayDienRa());
        suKien.setDiaDiem(suKienDTO.getDiaDiem());
        suKien.setTrangThai(suKienDTO.getTrangThai());
        suKien.setSoLuongThamGiaDuKien(suKienDTO.getSoLuongThamGiaDuKien());
        suKien.setSoLuongThamGiaThucTe(suKienDTO.getSoLuongThamGiaThucTe());
        suKien.setGhiChu(suKienDTO.getGhiChu());
        
        // Xử lý null cho các field không bắt buộc
        if (suKien.getMoTa() != null && suKien.getMoTa().trim().isEmpty()) {
            suKien.setMoTa(null);
        }
        if (suKien.getNoiDung() != null && suKien.getNoiDung().trim().isEmpty()) {
            suKien.setNoiDung(null);
        }
        if (suKien.getDiaDiem() != null && suKien.getDiaDiem().trim().isEmpty()) {
            suKien.setDiaDiem(null);
        }
        if (suKien.getGhiChu() != null && suKien.getGhiChu().trim().isEmpty()) {
            suKien.setGhiChu(null);
        }
        
        // Handle image upload
        if (suKienDTO.getHinhAnhFile() != null && !suKienDTO.getHinhAnhFile().isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadFile(suKienDTO.getHinhAnhFile(), "events");
                suKien.setHinhAnhUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi upload hình ảnh: " + e.getMessage());
            }
        }
        
        // Nếu là update (có ID), giữ nguyên createdAt
        if (suKien.getId() != null) {
            com.branch.demo.domain.SuKien existingSuKien = suKienRepository.findById(suKien.getId()).orElse(null);
            if (existingSuKien != null && existingSuKien.getCreatedAt() != null) {
                suKien.setCreatedAt(existingSuKien.getCreatedAt());
            }
        } else {
            suKien.setCreatedAt(LocalDateTime.now());
        }
        suKien.setUpdatedAt(LocalDateTime.now());
        
        return suKienRepository.save(suKien);
    }

    public void deleteSuKien(Long id) {
        com.branch.demo.domain.SuKien suKien = getSuKienById(id);
        suKien.setDeleted(true);
        suKien.setUpdatedAt(LocalDateTime.now());
        suKienRepository.save(suKien);
    }
    
    public Page<com.branch.demo.domain.SuKien> getDeletedSuKienPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "deletedAt"));
        return suKienRepository.findDeletedWithSearch(search, pageable);
    }
    
    public void restoreSuKien(Long id) {
        com.branch.demo.domain.SuKien suKien = suKienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện với ID: " + id));
        if (!suKien.isDeleted()) {
            throw new RuntimeException("Sự kiện chưa bị xóa");
        }
        suKien.setDeleted(false);
        suKien.setUpdatedAt(LocalDateTime.now());
        suKienRepository.save(suKien);
    }
    
    public void hardDeleteSuKien(Long id) {
        com.branch.demo.domain.SuKien suKien = suKienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện với ID: " + id));
        if (!suKien.isDeleted()) {
            throw new RuntimeException("Chỉ có thể xóa vĩnh viễn sự kiện đã được xóa mềm");
        }
        suKienRepository.deleteById(id);
    }
    
    // Get upcoming events
    public java.util.List<com.branch.demo.domain.SuKien> getUpcomingSuKien() {
        return suKienRepository.findUpcomingEvents();
    }
    
    // Get events by month
    public java.util.List<com.branch.demo.domain.SuKien> getSuKienByMonth(int year, int month) {
        return suKienRepository.findSuKienTheoThang(year, month);
    }
    
    // Get event statistics
    public java.util.List<Object[]> getSuKienStatsByType() {
        return suKienRepository.getThongKeTheoLoaiSuKien();
    }
    
    // ==================== LOAI SU KIEN METHODS ====================
    
    public Page<com.branch.demo.domain.LoaiSuKien> getLoaiSuKienPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("thuTu").ascending().and(Sort.by("createdAt").descending()));
        
        if (search != null && !search.trim().isEmpty()) {
            return loaiSuKienRepository.searchByKeyword(search.trim(), pageable);
        } else {
            return loaiSuKienRepository.findByDeletedFalseOrderByThuTuAscCreatedAtDesc(pageable);
        }
    }
    
    public java.util.List<com.branch.demo.domain.LoaiSuKien> getAllActiveLoaiSuKien() {
        return loaiSuKienRepository.findByDeletedFalseAndKichHoatTrueOrderByThuTuAscTenLoaiAsc();
    }
    
    public com.branch.demo.domain.LoaiSuKien getLoaiSuKienById(Long id) {
        return loaiSuKienRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Loại sự kiện không tồn tại"));
    }
    
    public com.branch.demo.domain.LoaiSuKien saveLoaiSuKien(com.branch.demo.dto.LoaiSuKienDTO loaiSuKienDTO) {
        com.branch.demo.domain.LoaiSuKien loaiSuKien;
        
        if (loaiSuKienDTO.getId() != null) {
            // Update existing
            loaiSuKien = getLoaiSuKienById(loaiSuKienDTO.getId());
        } else {
            // Create new
            loaiSuKien = new com.branch.demo.domain.LoaiSuKien();
        }
        
        // Map DTO to entity
        loaiSuKien.setTenLoai(loaiSuKienDTO.getTenLoai());
        loaiSuKien.setMoTa(loaiSuKienDTO.getMoTa());
        loaiSuKien.setMauSac(loaiSuKienDTO.getMauSac());
        loaiSuKien.setIcon(loaiSuKienDTO.getIcon());
        loaiSuKien.setThuTu(loaiSuKienDTO.getThuTu());
        loaiSuKien.setKichHoat(loaiSuKienDTO.isKichHoat());
        
        return loaiSuKienRepository.save(loaiSuKien);
    }
    
    public void deleteLoaiSuKien(Long id) {
        com.branch.demo.domain.LoaiSuKien loaiSuKien = getLoaiSuKienById(id);
        
        // Kiểm tra xem có sự kiện nào đang sử dụng loại này không
        boolean hasEvents = suKienRepository.existsByLoaiSuKienAndDeletedFalse(loaiSuKien);
        
        if (hasEvents) {
            throw new RuntimeException("Không thể xóa loại sự kiện này vì đang có sự kiện sử dụng");
        }
        
        loaiSuKien.setDeleted(true);
        loaiSuKienRepository.save(loaiSuKien);
    }
    
    public Page<com.branch.demo.domain.LoaiSuKien> getDeletedLoaiSuKienPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("deletedAt").descending());
        
        if (search != null && !search.trim().isEmpty()) {
            return loaiSuKienRepository.searchDeletedByKeyword(search.trim(), pageable);
        } else {
            return loaiSuKienRepository.findByDeletedTrueOrderByDeletedAtDesc(pageable);
        }
    }
    
    public void restoreLoaiSuKien(Long id) {
        com.branch.demo.domain.LoaiSuKien loaiSuKien = loaiSuKienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại sự kiện với ID: " + id));
        
        if (!loaiSuKien.isDeleted()) {
            throw new RuntimeException("Loại sự kiện chưa bị xóa");
        }
        
        // Kiểm tra tên có bị trùng với record đang active không
        if (loaiSuKienRepository.existsByTenLoaiAndDeletedFalse(loaiSuKien.getTenLoai())) {
            throw new RuntimeException("Không thể khôi phục vì đã có loại sự kiện khác với tên này");
        }
        
        loaiSuKien.setDeleted(false);
        loaiSuKienRepository.save(loaiSuKien);
    }
    
    public void hardDeleteLoaiSuKien(Long id) {
        com.branch.demo.domain.LoaiSuKien loaiSuKien = loaiSuKienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại sự kiện với ID: " + id));
        
        if (!loaiSuKien.isDeleted()) {
            throw new RuntimeException("Chỉ có thể xóa vĩnh viễn loại sự kiện đã được xóa mềm");
        }
        
        loaiSuKienRepository.deleteById(id);
    }
    
    public long countSuKienByLoaiSuKien(Long loaiSuKienId) {
        return suKienRepository.countByLoaiSuKienId(loaiSuKienId);
    }
}