package com.branch.demo.service;

import com.branch.demo.repository.*;
import com.branch.demo.domain.Account;
import com.branch.demo.domain.DanhMuc.TrangThaiDanhMuc;
import com.branch.demo.domain.DiemNhom;
import com.branch.demo.domain.NhanSu.ChucVu;
import com.branch.demo.dto.SuKienDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

      @Autowired
    private PasswordEncoder passwordEncoder;

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

     @Autowired
    private AccountRepository accountRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalTinHuu", tinHuuRepository.countActive());
        } catch (Exception e) {
            stats.put("totalTinHuu", 0L);
        }
        
        try {
            stats.put("deletedTinHuu", tinHuuRepository.countDeleted());
        } catch (Exception e) {
            stats.put("deletedTinHuu", 0L);
        }
        
        try {
            stats.put("totalNhom", nhomRepository.count());
        } catch (Exception e) {
            stats.put("totalNhom", 0L);
        }
        
        try {
            stats.put("totalBanNganh", banNganhRepository.count());
        } catch (Exception e) {
            stats.put("totalBanNganh", 0L);
        }
        
        try {
            stats.put("totalNhanSu", nhanSuRepository.count());
        } catch (Exception e) {
            stats.put("totalNhanSu", 0L);
        }
        
        try {
            stats.put("totalSuKien", suKienRepository.count());
        } catch (Exception e) {
            stats.put("totalSuKien", 0L);
        }
        
        try {
            stats.put("totalTaiChinh", taiChinhRepository.count());
        } catch (Exception e) {
            stats.put("totalTaiChinh", 0L);
        }
        
        try {
            stats.put("totalChapSu", chapSuRepository.count());
        } catch (Exception e) {
            stats.put("totalChapSu", 0L);
        }
        
        try {
            stats.put("totalLienHe", lienHeRepository.count());
        } catch (Exception e) {
            stats.put("totalLienHe", 0L);
        }
        
        try {
            stats.put("totalBaiViet", baiVietRepository.count());
        } catch (Exception e) {
            stats.put("totalBaiViet", 0L);
        }
        
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
        // Nếu là update (có ID), preserve các field quan trọng từ existing record
        if (tinHuu.getId() != null) {
            com.branch.demo.domain.TinHuu existingTinHuu = tinHuuRepository.findById(tinHuu.getId()).orElse(null);
            if (existingTinHuu != null) {
                // Preserve nhom relationship nếu không được set trong form
                if (tinHuu.getNhom() == null && existingTinHuu.getNhom() != null) {
                    tinHuu.setNhom(existingTinHuu.getNhom());
                }

                // Preserve avatar nếu không được update
                if (tinHuu.getAvatarUrl() == null && existingTinHuu.getAvatarUrl() != null) {
                    tinHuu.setAvatarUrl(existingTinHuu.getAvatarUrl());
                }
            }
        }

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

        // Spring Data JPA Auditing sẽ tự động handle createdAt, updatedAt, createdBy,
        // updatedBy
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

    // public Page<com.branch.demo.domain.Nhom> getNhomPage(int page, String search)
    // {
    // Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,
    // "createdAt"));
    // if (search == null || search.trim().isEmpty()) {
    // return nhomRepository.findAll(pageable);
    // }
    // return nhomRepository.findByTenNhomContainingIgnoreCase(search).stream()
    // .collect(java.util.stream.Collectors.collectingAndThen(
    // java.util.stream.Collectors.toList(),
    // list -> new org.springframework.data.domain.PageImpl<>(
    // list.stream().skip(pageable.getOffset()).limit(pageable.getPageSize())
    // .collect(java.util.stream.Collectors.toList()),
    // pageable,
    // list.size())));
    // }

    // public com.branch.demo.domain.Nhom getNhomById(Long id) {
    // return nhomRepository.findById(id)
    // .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm với ID: " +
    // id));
    // }

    // public com.branch.demo.domain.Nhom saveNhom(com.branch.demo.domain.Nhom nhom)
    // {
    // // Xử lý null cho mô tả (field không bắt buộc)
    // if (nhom.getMoTa() != null && nhom.getMoTa().trim().isEmpty()) {
    // nhom.setMoTa(null);
    // }

    // // Nếu có điểm nhóm ID, tìm và set điểm nhóm
    // if (nhom.getDiemNhom() != null && nhom.getDiemNhom().getId() != null) {
    // com.branch.demo.domain.DiemNhom diemNhom =
    // diemNhomRepository.findById(nhom.getDiemNhom().getId())
    // .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));
    // nhom.setDiemNhom(diemNhom);
    // }

    // // Nếu là update (có ID), giữ nguyên createdAt
    // if (nhom.getId() != null) {
    // com.branch.demo.domain.Nhom existingNhom =
    // nhomRepository.findById(nhom.getId()).orElse(null);
    // if (existingNhom != null && existingNhom.getCreatedAt() != null) {
    // nhom.setCreatedAt(existingNhom.getCreatedAt());
    // }
    // }

    // return nhomRepository.save(nhom);
    // }

    // public void deleteNhom(Long id) {
    // com.branch.demo.domain.Nhom nhom = getNhomById(id);

    // // Set nhóm của các tin hữu thành null trước khi xóa nhóm
    // if (!nhom.getDanhSachTinHuu().isEmpty()) {
    // for (com.branch.demo.domain.TinHuu tinHuu : nhom.getDanhSachTinHuu()) {
    // tinHuu.setNhom(null);
    // tinHuuRepository.save(tinHuu);
    // }
    // }

    // nhomRepository.deleteById(id);
    // }

    public java.util.List<com.branch.demo.domain.DiemNhom> getAllActiveDiemNhom() {
        return diemNhomRepository.findByTrangThai(com.branch.demo.domain.DiemNhom.TrangThaiDiemNhom.HOAT_DONG);
    }

    public java.util.List<com.branch.demo.domain.DanhMuc> getAllActiveDanhMuc() {
        return danhMucRepository
                .findByTrangThaiOrderByTenDanhMucAsc(com.branch.demo.domain.DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
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

        // Spring Data JPA Auditing sẽ tự động handle audit fields

        return diemNhomRepository.save(diemNhom);
    }

    public java.util.List<com.branch.demo.domain.Nhom> getAllActiveNhom() {
        return nhomRepository.findAll();
    }

    // ==================== NHÓM MANAGEMENT METHODS ====================

    public java.util.List<com.branch.demo.domain.Nhom> getAllNhom() {
        return nhomRepository.findAll(Sort.by(Sort.Direction.ASC, "tenNhom"));
    }

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

        return nhomRepository.findWithAdvancedFilters(search, trangThaiEnum, diemNhomId, fromDateTime, toDateTime,
                pageable);
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

        // Spring Data JPA Auditing sẽ tự động handle audit fields

        return nhomRepository.save(nhom);
    }

    public void deleteNhom(Long id) {
        com.branch.demo.domain.Nhom nhom = getNhomById(id);

        // Set nhóm của các tin hữu thành null trước khi xóa nhóm
        // Điều này đảm bảo tin hữu không bị xóa theo nhóm
        if (!nhom.getDanhSachTinHuu().isEmpty()) {
            java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = new java.util.ArrayList<>(
                    nhom.getDanhSachTinHuu());
            for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                tinHuu.setNhom(null);
                tinHuuRepository.save(tinHuu);
            }
            // Clear danh sách để tránh cascade issues
            nhom.getDanhSachTinHuu().clear();
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

    public java.util.List<java.util.Map<String, Object>> getNhomByDiemNhomId(Long diemNhomId) {
        com.branch.demo.domain.DiemNhom diemNhom = getDiemNhomById(diemNhomId);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();

        for (com.branch.demo.domain.Nhom nhom : diemNhom.getDanhSachNhom()) {
            java.util.Map<String, Object> nhomData = new java.util.HashMap<>();
            nhomData.put("id", nhom.getId());
            nhomData.put("tenNhom", nhom.getTenNhom());
            nhomData.put("moTa", nhom.getMoTa());
            nhomData.put("thoiGianSinhHoat", nhom.getThoiGianSinhHoat());
            nhomData.put("trangThai", nhom.getTrangThai().name());
            nhomData.put("soLuongTinHuu", nhom.getSoLuongTinHuu());
            result.add(nhomData);
        }

        return result;
    }

    public java.util.Map<String, Object> getAvailableTinHuuForNhom() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        // Lấy danh sách tin hữu chưa có nhóm hoặc nhóm bị xóa
        java.util.List<com.branch.demo.domain.TinHuu> availableTinHuu = tinHuuRepository
                .findByNhomIsNullAndDeletedAtIsNull();

        java.util.List<java.util.Map<String, Object>> tinHuuList = new java.util.ArrayList<>();
        for (com.branch.demo.domain.TinHuu tinHuu : availableTinHuu) {
            java.util.Map<String, Object> tinHuuData = new java.util.HashMap<>();
            tinHuuData.put("id", tinHuu.getId());
            tinHuuData.put("hoTen", tinHuu.getHoTen());
            tinHuuData.put("namSinh", tinHuu.getNamSinh());
            tinHuuData.put("gioiTinh", tinHuu.getGioiTinh());
            tinHuuData.put("avatarUrl", tinHuu.getAvatarUrl());

            // Thêm thông tin điểm nhóm nếu có
            if (tinHuu.getNhom() != null && tinHuu.getNhom().getDiemNhom() != null) {
                tinHuuData.put("diemNhomId", tinHuu.getNhom().getDiemNhom().getId());
                tinHuuData.put("diemNhomTen", tinHuu.getNhom().getDiemNhom().getTenDiemNhom());
            } else {
                tinHuuData.put("diemNhomId", null);
                tinHuuData.put("diemNhomTen", "Chưa phân công");
            }

            tinHuuList.add(tinHuuData);
        }

        // Lấy danh sách điểm nhóm để filter
        java.util.List<com.branch.demo.domain.DiemNhom> diemNhomList = getAllActiveDiemNhom();
        java.util.List<java.util.Map<String, Object>> diemNhomData = new java.util.ArrayList<>();
        for (com.branch.demo.domain.DiemNhom diemNhom : diemNhomList) {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("id", diemNhom.getId());
            data.put("tenDiemNhom", diemNhom.getTenDiemNhom());
            diemNhomData.add(data);
        }

        result.put("tinHuu", tinHuuList);
        result.put("diemNhomList", diemNhomData);

        return result;
    }

    public void addTinHuuToNhom(Long nhomId, java.util.List<Long> tinHuuIds) {
        com.branch.demo.domain.Nhom nhom = getNhomById(nhomId);

        for (Long tinHuuId : tinHuuIds) {
            com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(tinHuuId);

            // Kiểm tra tin hữu đã có nhóm chưa
            if (tinHuu.getNhom() != null) {
                throw new RuntimeException("Tin hữu " + tinHuu.getHoTen() + " đã thuộc nhóm khác");
            }

            // Thêm tin hữu vào nhóm
            tinHuu.setNhom(nhom);
            tinHuuRepository.save(tinHuu);
        }
    }

    public java.util.List<java.util.Map<String, Object>> getAllNhomForTransfer() {
        java.util.List<com.branch.demo.domain.Nhom> nhomList = nhomRepository.findAll();
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();

        for (com.branch.demo.domain.Nhom nhom : nhomList) {
            java.util.Map<String, Object> nhomData = new java.util.HashMap<>();
            nhomData.put("id", nhom.getId());
            nhomData.put("tenNhom", nhom.getTenNhom());
            nhomData.put("diemNhomTen", nhom.getDiemNhom() != null ? nhom.getDiemNhom().getTenDiemNhom() : null);
            nhomData.put("soLuongTinHuu", nhom.getSoLuongTinHuu());
            result.add(nhomData);
        }

        return result;
    }

    public void removeTinHuuFromNhom(Long tinHuuId) {
        com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(tinHuuId);

        if (tinHuu.getNhom() == null) {
            throw new RuntimeException("Tin hữu này chưa thuộc nhóm nào");
        }

        // Xóa tin hữu khỏi nhóm
        tinHuu.setNhom(null);
        tinHuuRepository.save(tinHuu);
    }

    public void transferTinHuuToNhom(Long tinHuuId, Long targetNhomId) {
        com.branch.demo.domain.TinHuu tinHuu = getTinHuuById(tinHuuId);
        com.branch.demo.domain.Nhom targetNhom = getNhomById(targetNhomId);

        // Chuyển tin hữu sang nhóm mới
        tinHuu.setNhom(targetNhom);
        tinHuuRepository.save(tinHuu);
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
        com.branch.demo.domain.BanNganh banNganh = banNganhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ban ngành với ID: " + id));

        // Eager load danh sách nhân sự
        banNganh.getDanhSachNhanSu().size(); // Trigger lazy loading

        return banNganh;
    }

    public com.branch.demo.domain.BanNganh saveBanNganh(com.branch.demo.domain.BanNganh banNganh) {
        // Convert mã ban thành chữ in hoa
        if (banNganh.getMaBan() != null) {
            banNganh.setMaBan(banNganh.getMaBan().toUpperCase().trim());
        }

        // Kiểm tra trùng lặp mã ban
        if (banNganh.getMaBan() != null && !banNganh.getMaBan().trim().isEmpty()) {
            java.util.Optional<com.branch.demo.domain.BanNganh> existingByMaBan = banNganhRepository
                    .findByMaBan(banNganh.getMaBan());

            if (existingByMaBan.isPresent()) {
                // Nếu là update, kiểm tra xem có phải cùng một ban ngành không
                if (banNganh.getId() == null || !existingByMaBan.get().getId().equals(banNganh.getId())) {
                    throw new RuntimeException(
                            "Mã ban '" + banNganh.getMaBan() + "' đã tồn tại. Vui lòng chọn mã ban khác.");
                }
            }
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

        // Spring Data JPA Auditing sẽ tự động handle audit fields

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
    // public Page<com.branch.demo.domain.Nhom> getNhomPageWithFilters(int page,
    // String search,
    // String trangThai, Long diemNhomId,
    // java.time.LocalDate fromDate, java.time.LocalDate toDate) {
    // Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,
    // "createdAt"));

    // // Convert LocalDate to LocalDateTime
    // java.time.LocalDateTime fromDateTime = fromDate != null ?
    // fromDate.atStartOfDay() : null;
    // java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59,
    // 59) : null;

    // // Convert String to Enum
    // com.branch.demo.domain.Nhom.TrangThaiNhom trangThaiEnum = null;
    // if (trangThai != null && !trangThai.isEmpty()) {
    // try {
    // trangThaiEnum = com.branch.demo.domain.Nhom.TrangThaiNhom.valueOf(trangThai);
    // } catch (IllegalArgumentException e) {
    // // Invalid enum value, ignore
    // }
    // }

    // return nhomRepository.findWithAdvancedFilters(search, trangThaiEnum,
    // diemNhomId, fromDateTime, toDateTime,
    // pageable);
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
        if (nhanSu.getChucVu() == null) {
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

        // Spring Data JPA Auditing sẽ tự động handle audit fields

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

    public com.branch.demo.dto.DanhMucDTO convertToDTO(com.branch.demo.domain.DanhMuc danhMuc) {
        com.branch.demo.dto.DanhMucDTO dto = new com.branch.demo.dto.DanhMucDTO();
        dto.setId(danhMuc.getId());
        dto.setTenDanhMuc(danhMuc.getTenDanhMuc());
        dto.setSlug(danhMuc.getSlug());
        dto.setMoTa(danhMuc.getMoTa());
        dto.setThuTu(danhMuc.getThuTu());
        dto.setMauSac(danhMuc.getMauSac());
        dto.setIcon(danhMuc.getIcon());
        dto.setTrangThai(danhMuc.getTrangThai() != null ? danhMuc.getTrangThai().getDisplayName() : null);
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

        // Spring Data JPA Auditing sẽ tự động handle audit fields

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
        Pageable pageable = PageRequest.of(page, 10,
                Sort.by("tenLoai").ascending());

        if (search != null && !search.trim().isEmpty()) {
            return loaiSuKienRepository.searchByKeyword(search.trim(), pageable);
        } else {
            return loaiSuKienRepository.findAll(pageable);
        }
    }

    public java.util.List<com.branch.demo.domain.LoaiSuKien> getAllActiveLoaiSuKien() {
        return loaiSuKienRepository.findByKichHoatTrueOrderByTenLoaiAsc();
    }

    public com.branch.demo.domain.LoaiSuKien getLoaiSuKienById(Long id) {
        return loaiSuKienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loại sự kiện không tồn tại"));
    }

    public com.branch.demo.domain.LoaiSuKien saveLoaiSuKien(com.branch.demo.domain.LoaiSuKien loaiSuKien) {
        // Validate tên loại không trùng
        if (loaiSuKien.getTenLoai() != null && !loaiSuKien.getTenLoai().trim().isEmpty()) {
            boolean exists = false;
            if (loaiSuKien.getId() == null) {
                // Create mode
                exists = loaiSuKienRepository.existsByTenLoai(loaiSuKien.getTenLoai().trim());
            } else {
                // Update mode
                exists = loaiSuKienRepository.existsByTenLoaiAndIdNot(loaiSuKien.getTenLoai().trim(),
                        loaiSuKien.getId());
            }

            if (exists) {
                throw new RuntimeException(
                        "Tên loại sự kiện '" + loaiSuKien.getTenLoai() + "' đã tồn tại. Vui lòng chọn tên khác.");
            }
        }

        // Xử lý null cho các field không bắt buộc
        if (loaiSuKien.getMoTa() != null && loaiSuKien.getMoTa().trim().isEmpty()) {
            loaiSuKien.setMoTa(null);
        }
        if (loaiSuKien.getMauSac() != null && loaiSuKien.getMauSac().trim().isEmpty()) {
            loaiSuKien.setMauSac(null);
        }
        if (loaiSuKien.getIcon() != null && loaiSuKien.getIcon().trim().isEmpty()) {
            loaiSuKien.setIcon(null);
        }

        // Spring Data JPA Auditing sẽ tự động handle audit fields

        return loaiSuKienRepository.save(loaiSuKien);
    }

    public void deleteLoaiSuKien(Long id) {
        com.branch.demo.domain.LoaiSuKien loaiSuKien = getLoaiSuKienById(id);

        // Kiểm tra xem có sự kiện nào đang sử dụng loại này không
        // TODO: Thêm kiểm tra khi có relationship với SuKien

        loaiSuKienRepository.deleteById(id);
    }

    public java.util.List<com.branch.demo.domain.LoaiSuKien> getAllLoaiSuKien() {
        return loaiSuKienRepository.findAll(Sort.by("tenLoai").ascending());
    }

    // ==================== NHOM MANAGEMENT METHODS ====================

    public java.util.List<com.branch.demo.domain.Nhom> getNhomByDiemNhom(Long diemNhomId) {
        com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(diemNhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));
        return diemNhom.getDanhSachNhom();
    }

    @Transactional
    public void transferNhomToDiemNhom(Long nhomId, Long targetDiemNhomId) {
        com.branch.demo.domain.Nhom nhom = nhomRepository.findById(nhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

        com.branch.demo.domain.DiemNhom targetDiemNhom = diemNhomRepository.findById(targetDiemNhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm đích"));

        // Update nhom's diem nhom
        nhom.setDiemNhom(targetDiemNhom);
        nhomRepository.save(nhom);

        // Update tin huu's diem nhom
        java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = tinHuuRepository.findByNhomId(nhom.getId());
        for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
            // tinHuu.setNhom(targetDiemNhom);
        }
        tinHuuRepository.saveAll(tinHuuList);
    }

    @Transactional
    public void deleteNhomWithTinHuuHandling(Long nhomId, String tinHuuAction,
            Long targetNhomId) {
        com.branch.demo.domain.Nhom nhom = nhomRepository.findById(nhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

        java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = tinHuuRepository.findByNhomId(nhom.getId());

        if ("transfer".equals(tinHuuAction) && targetNhomId != null) {
            // Transfer tin huu to another nhom
            com.branch.demo.domain.Nhom targetNhom = nhomRepository.findById(targetNhomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm đích"));

            for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                tinHuu.setNhom(targetNhom);
                // tinHuu.setDiemNhom(targetNhom.getDiemNhom());
            }
            tinHuuRepository.saveAll(tinHuuList);
        } else {
            // Remove tin huu from nhom (set nhom to null)
            for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                tinHuu.setNhom(null);
                // Keep diem nhom or set to null based on business logic
                // tinHuu.setDiemNhom(null);
            }
            tinHuuRepository.saveAll(tinHuuList);
        }

        // Delete the nhom
        nhomRepository.delete(nhom);
    }

    public java.util.List<com.branch.demo.domain.NhanSu> getNhanSuByBanNganh(Long banNganhId) {
        com.branch.demo.domain.BanNganh banNganh = banNganhRepository.findById(banNganhId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ban ngành"));
        return banNganh.getDanhSachNhanSu();
    }

    public java.util.List<java.util.Map<String, Object>> getNhanSuByBanNganhId(Long banNganhId) {
        java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = getNhanSuByBanNganh(banNganhId);
        return nhanSuList.stream().map(nhanSu -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", nhanSu.getId());
            map.put("hoTen", nhanSu.getHoTen());
            map.put("chucVu", nhanSu.getChucVu());
            map.put("email", nhanSu.getEmail());
            map.put("dienThoai", nhanSu.getDienThoai());
            map.put("avatarUrl", nhanSu.getAvatarUrl());
            map.put("trangThai", nhanSu.getTrangThai());
            map.put("ngayBatDau", nhanSu.getNgayBatDauPhucVu());
            map.put("diemNhom", nhanSu.getDiemNhom() != null ? nhanSu.getDiemNhom().getTenDiemNhom() : null);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    // ==================== DIEM NHOM DELETE WITH CASCADE ====================

    @Transactional
    public void deleteDiemNhom(Long diemNhomId) {
        com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(diemNhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));

        // Step 1: Get all nhom in this diem nhom
        java.util.List<com.branch.demo.domain.Nhom> nhomList = diemNhom.getDanhSachNhom();

        // Step 2: For each nhom, set all tin huu's nhom to null
        for (com.branch.demo.domain.Nhom nhom : nhomList) {
            java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = tinHuuRepository.findByNhomId(nhom.getId());
            for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                tinHuu.setNhom(null);
            }
            tinHuuRepository.saveAll(tinHuuList);
        }

        // Step 3: Delete all nhom in this diem nhom
        for (com.branch.demo.domain.Nhom nhom : nhomList) {
            nhomRepository.delete(nhom);
        }

        // Step 4: Set all nhan su's diem nhom to null
        java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = nhanSuRepository.findByDiemNhomId(diemNhom.getId());
        for (com.branch.demo.domain.NhanSu nhanSu : nhanSuList) {
            nhanSu.setDiemNhom(null);
        }
        nhanSuRepository.saveAll(nhanSuList);

        // Step 5: Finally delete the diem nhom
        diemNhomRepository.delete(diemNhom);
    }

    @Transactional
    public void deleteDiemNhomWithTinHuuHandling(Long diemNhomId, String tinHuuAction, Long targetDiemNhomId) {
        com.branch.demo.domain.DiemNhom diemNhom = diemNhomRepository.findById(diemNhomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm"));

        // Get all nhom in this diem nhom
        java.util.List<com.branch.demo.domain.Nhom> nhomList = diemNhom.getDanhSachNhom();

        if ("transfer".equals(tinHuuAction) && targetDiemNhomId != null) {
            // Transfer all tin huu to target diem nhom
            com.branch.demo.domain.DiemNhom targetDiemNhom = diemNhomRepository.findById(targetDiemNhomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm nhóm đích"));

            for (com.branch.demo.domain.Nhom nhom : nhomList) {
                java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = tinHuuRepository.findByNhomId(nhom.getId());
                for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                    tinHuu.setNhom(null); // Remove from current nhom
                }
                tinHuuRepository.saveAll(tinHuuList);
            }
        } else {
            // Remove tin huu from nhom (set both nhom and diem nhom to null)
            for (com.branch.demo.domain.Nhom nhom : nhomList) {
                java.util.List<com.branch.demo.domain.TinHuu> tinHuuList = tinHuuRepository.findByNhomId(nhom.getId());
                for (com.branch.demo.domain.TinHuu tinHuu : tinHuuList) {
                    tinHuu.setNhom(null);
                }
                tinHuuRepository.saveAll(tinHuuList);
            }
        }

        // Delete all nhom in this diem nhom
        for (com.branch.demo.domain.Nhom nhom : nhomList) {
            nhomRepository.delete(nhom);
        }

        // Handle nhan su
        java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = nhanSuRepository.findByDiemNhomId(diemNhom.getId());
        if ("transfer".equals(tinHuuAction) && targetDiemNhomId != null) {
            com.branch.demo.domain.DiemNhom targetDiemNhom = diemNhomRepository.findById(targetDiemNhomId).orElse(null);
            for (com.branch.demo.domain.NhanSu nhanSu : nhanSuList) {
                nhanSu.setDiemNhom(targetDiemNhom);
            }
        } else {
            for (com.branch.demo.domain.NhanSu nhanSu : nhanSuList) {
                nhanSu.setDiemNhom(null);
            }
        }
        nhanSuRepository.saveAll(nhanSuList);

        // Finally delete the diem nhom
        diemNhomRepository.delete(diemNhom);
    }
    // ==================== BÀI VIẾT MANAGEMENT ====================

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    public Page<com.branch.demo.domain.BaiViet> getBaiVietPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        if (search != null && !search.trim().isEmpty()) {
            return baiVietRepository.findWithSearch(search.trim(), pageable);
        }
        return baiVietRepository.findAll(pageable);
    }

    public Page<com.branch.demo.domain.BaiViet> getBaiVietPageWithFilters(int page, String search,
            String trangThai, Long danhMucId, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        com.branch.demo.domain.BaiViet.TrangThaiBaiViet trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.BaiViet.TrangThaiBaiViet.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        return baiVietRepository.findWithAdvancedFilters(search, trangThaiEnum, danhMucId,
                null, null, fromDateTime, toDateTime, pageable);
    }

    public com.branch.demo.domain.BaiViet getBaiVietById(Long id) {
        return baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
    }

    @Transactional
    public com.branch.demo.domain.BaiViet saveBaiViet(com.branch.demo.domain.BaiViet baiViet) {
        // Generate slug if not provided
        if (baiViet.getSlug() == null || baiViet.getSlug().trim().isEmpty()) {
            baiViet.setSlug(generateSlug(baiViet.getTieuDe()));
        }

        // Ensure slug is unique
        String originalSlug = baiViet.getSlug();
        String uniqueSlug = originalSlug;
        int counter = 1;

        while (baiVietRepository.existsBySlugAndIdNot(uniqueSlug, baiViet.getId() != null ? baiViet.getId() : 0L)) {
            uniqueSlug = originalSlug + "-" + counter;
            counter++;
        }
        baiViet.setSlug(uniqueSlug);

        return baiVietRepository.save(baiViet);
    }

    @Transactional
    public void deleteBaiViet(Long id) {
        com.branch.demo.domain.BaiViet baiViet = getBaiVietById(id);
        baiVietRepository.delete(baiViet);
    }

    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "bai-viet-" + System.currentTimeMillis();
        }

        return title.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public com.branch.demo.dto.BaiVietDTO convertToBaiVietDTO(com.branch.demo.domain.BaiViet baiViet) {
        com.branch.demo.dto.BaiVietDTO dto = new com.branch.demo.dto.BaiVietDTO();
        dto.setId(baiViet.getId());
        dto.setTieuDe(baiViet.getTieuDe());
        dto.setSlug(baiViet.getSlug());
        dto.setTomTat(baiViet.getTomTat());
        dto.setNoiDung(baiViet.getNoiDung());
        dto.setNoiDungRich(baiViet.getNoiDungRich());
        dto.setAnhDaiDien(baiViet.getAnhDaiDien());
        dto.setDanhSachHinhAnh(baiViet.getDanhSachHinhAnh());
        dto.setDanhSachVideo(baiViet.getDanhSachVideo());
        dto.setTrangThai(baiViet.getTrangThai());
        dto.setTacGia(baiViet.getTacGia());
        dto.setLoaiTacGia(baiViet.getLoaiTacGia());
        dto.setLuotXem(baiViet.getLuotXem());
        dto.setNoiBat(baiViet.getNoiBat());
        dto.setChoPhepBinhLuan(baiViet.getChoPhepBinhLuan());
        dto.setMetaTitle(baiViet.getMetaTitle());
        dto.setMetaDescription(baiViet.getMetaDescription());
        dto.setMetaKeywords(baiViet.getMetaKeywords());
        dto.setNgayXuatBan(baiViet.getNgayXuatBan());
        dto.setCreatedAt(baiViet.getCreatedAt());
        dto.setUpdatedAt(baiViet.getUpdatedAt());

        if (baiViet.getDanhMuc() != null) {
            dto.setDanhMucId(baiViet.getDanhMuc().getId());
            dto.setTenDanhMuc(baiViet.getDanhMuc().getTenDanhMuc());
        }

        return dto;
    }

    // ==================== THÙNG RÁC BÀI VIẾT ====================

    public Page<com.branch.demo.domain.BaiViet> getTrashedBaiViet(int page, String search, String fromDate,
            String toDate) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("deletedAt").descending());

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            try {
                fromDateTime = LocalDate.parse(fromDate).atStartOfDay();
            } catch (Exception e) {
                // Ignore invalid date
            }
        }

        if (toDate != null && !toDate.isEmpty()) {
            try {
                toDateTime = LocalDate.parse(toDate).atTime(23, 59, 59);
            } catch (Exception e) {
                // Ignore invalid date
            }
        }

        if (search != null && !search.trim().isEmpty()) {
            return baiVietRepository.findDeletedWithSearch(search.trim(), pageable);
        } else if (fromDateTime != null || toDateTime != null) {
            return baiVietRepository.findDeletedByDateRange(fromDateTime, toDateTime, pageable);
        }

        return baiVietRepository.findAllDeleted(pageable);
    }

    @Transactional
    public void softDeleteBaiViet(Long id) {
        com.branch.demo.domain.BaiViet baiViet = getBaiVietById(id);
        baiViet.setDeletedAt(LocalDateTime.now());
        baiVietRepository.save(baiViet);
    }

    @Transactional
    public void restoreBaiViet(Long id) {
        com.branch.demo.domain.BaiViet baiViet = baiVietRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        if (baiViet.getDeletedAt() == null) {
            throw new RuntimeException("Bài viết này chưa bị xóa");
        }

        baiViet.setDeletedAt(null);
        baiVietRepository.save(baiViet);
    }

    @Transactional
    public void permanentDeleteBaiViet(Long id) {
        com.branch.demo.domain.BaiViet baiViet = baiVietRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        if (baiViet.getDeletedAt() == null) {
            throw new RuntimeException("Bài viết này chưa bị xóa mềm");
        }

        baiVietRepository.delete(baiViet);
    }

    @Transactional
    public int emptyBaiVietTrash() {
        Page<com.branch.demo.domain.BaiViet> trashedArticles = baiVietRepository
                .findAllDeleted(PageRequest.of(0, Integer.MAX_VALUE));
        int count = (int) trashedArticles.getTotalElements();

        for (com.branch.demo.domain.BaiViet baiViet : trashedArticles.getContent()) {
            baiVietRepository.delete(baiViet);
        }

        return count;
    }

    public com.branch.demo.domain.BaiViet getTrashedBaiVietById(Long id) {
        return baiVietRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
    }

    // ==================== DANH MỤC MANAGEMENT ====================

    public Page<com.branch.demo.domain.DanhMuc> getDanhMucPage(int page, String search, String trangThai) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        com.branch.demo.domain.DanhMuc.TrangThaiDanhMuc trangThaiEnum = null;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                trangThaiEnum = com.branch.demo.domain.DanhMuc.TrangThaiDanhMuc.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        if (search != null && !search.trim().isEmpty()) {
            return danhMucRepository.findWithSearch(search.trim(), trangThaiEnum, pageable);
        } else if (trangThaiEnum != null) {
            return danhMucRepository.findByTrangThai(trangThaiEnum, pageable);
        }

        return danhMucRepository.findAll(pageable);
    }

    public com.branch.demo.domain.DanhMuc getDanhMucById(Long id) {
        return danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }

    @Transactional
    public com.branch.demo.domain.DanhMuc saveDanhMuc(com.branch.demo.domain.DanhMuc danhMuc) {
        // Generate slug if not provided
        if (danhMuc.getSlug() == null || danhMuc.getSlug().trim().isEmpty()) {
            danhMuc.setSlug(generateSlug(danhMuc.getTenDanhMuc()));
        }

        // Ensure slug is unique
        String originalSlug = danhMuc.getSlug();
        String uniqueSlug = originalSlug;
        int counter = 1;

        while (danhMucRepository.existsBySlugAndIdNot(uniqueSlug, danhMuc.getId() != null ? danhMuc.getId() : 0L)) {
            uniqueSlug = originalSlug + "-" + counter;
            counter++;
        }
        danhMuc.setSlug(uniqueSlug);

        return danhMucRepository.save(danhMuc);
    }

    @Transactional
    public void deleteDanhMuc(Long id) {
        com.branch.demo.domain.DanhMuc danhMuc = getDanhMucById(id);

        // Check if category has articles
        long articleCount = baiVietRepository.countByDanhMuc(danhMuc);
        if (articleCount > 0) {
            throw new RuntimeException("Không thể xóa danh mục này vì còn " + articleCount + " bài viết đang sử dụng");
        }

        danhMucRepository.delete(danhMuc);
    }

    // ==================== ACCOUNT MANAGEMENT METHODS ====================

    public Page<Account> getAccountPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (search == null || search.trim().isEmpty()) {
            return accountRepository.findAll(pageable);
        }
        // Tìm theo username, email, fullName
        return accountRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        search, search, search, pageable);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + id));
    }

    public String saveAccount(Account account, String password) {
        String generatedPassword = null;
        
        if (account.getId() == null) {
            // New account - generate random password
            generatedPassword = com.branch.demo.util.PasswordGenerator.generateRandomPassword();
            account.setPassword(passwordEncoder.encode(generatedPassword));
            account.setFirstPassword(generatedPassword); // Store original password for display
        } else {
            // Update existing account - keep existing password and firstPassword
            Account existingAccount = getAccountById(account.getId());
            account.setPassword(existingAccount.getPassword());
            account.setFirstPassword(existingAccount.getFirstPassword());
        }

        accountRepository.save(account);
        return generatedPassword;
    }
    
    // Keep old method for backward compatibility
    public Account saveAccount(Account account) {
        saveAccount(account, null);
        return account;
    }
    
    public boolean isUsernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }
    
    public boolean isEmailExists(String email) {
        return accountRepository.existsByEmail(email);
    }
    
    public boolean isEmailExistsExcludingId(String email, Long excludeId) {
        return accountRepository.existsByEmailAndIdNot(email, excludeId);
    }

    public void deleteAccount(Long id) {
        Account account = getAccountById(id);

        // Prevent deleting the last admin account
        if (account.getRole() == Account.Role.ADMIN) {
            long adminCount = accountRepository.countActiveAdmins();
            if (adminCount <= 1) {
                throw new RuntimeException("Không thể xóa tài khoản admin cuối cùng");
            }
        }

        accountRepository.deleteById(id);
    }

    public void toggleAccountStatus(Long id) {
        Account account = getAccountById(id);

        // Prevent disabling the last admin account
        if (account.getRole() == Account.Role.ADMIN && account.getStatus() == Account.AccountStatus.ACTIVE) {
            long activeAdminCount = accountRepository.countActiveAdmins();
            if (activeAdminCount <= 1) {
                throw new RuntimeException("Không thể vô hiệu hóa tài khoản admin cuối cùng");
            }
        }

        // Toggle status
        if (account.getStatus() == Account.AccountStatus.ACTIVE) {
            account.setStatus(Account.AccountStatus.INACTIVE);
        } else {
            account.setStatus(Account.AccountStatus.ACTIVE);
        }

        accountRepository.save(account);
    }

    public void resetPassword(Long id, String newPassword) {
        Account account = getAccountById(id);
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public java.util.List<Account> getAllActiveAccounts() {
        return accountRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName"));
    }
}
