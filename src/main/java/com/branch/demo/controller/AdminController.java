package com.branch.demo.controller;

import com.branch.demo.service.AdminService;
import com.branch.demo.service.FileUploadService;
import com.branch.demo.domain.*;
import jakarta.validation.Valid;

import com.branch.demo.dto.ChapSuDTO;
import com.branch.demo.dto.SuKienDTO;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.BanNganhRepository;
import com.branch.demo.repository.ChapSuRepository;
import com.branch.demo.repository.LoaiSuKienRepository;
import com.branch.demo.repository.NhanSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private BanNganhRepository banNganhRepository;

    @Autowired
    private LoaiSuKienRepository loaiSuKienRepository;

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @Autowired
    private ChapSuRepository chapSuRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("pageTitle", "Tổng Quan Hệ Thống");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/nhan-su-chap-su")
    public String nhanSuChapSuSelection(Model model) {
        model.addAttribute("title", "Nhân Sự & Chấp Sự");
        model.addAttribute("pageTitle", "Chọn Danh Sách Quản Lý");
        model.addAttribute("activeMenu", "nhan-su-chap-su");
        return "admin/nhan-su-chap-su/index";
    }

    @GetMapping("/tin-huu")
    public String tinHuuList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long diemNhomId,
            @RequestParam(required = false) Long nhomId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                diemNhomId != null || nhomId != null ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.TinHuu> tinHuuPage;
        if (hasFilters) {
            tinHuuPage = adminService.getTinHuuPageWithFilters(page, search, trangThai,
                    diemNhomId, nhomId, parsedFromDate, parsedToDate);
        } else {
            tinHuuPage = adminService.getTinHuuPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Tin Hữu");
        model.addAttribute("pageTitle", "Quản Lý Tin Hữu");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuuPage", tinHuuPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("diemNhomId", diemNhomId);
        model.addAttribute("nhomId", nhomId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Add filter options
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        model.addAttribute("nhomList", adminService.getAllActiveNhom());

        return "admin/tin-huu/list";
    }

    @GetMapping("/tin-huu/new")
    public String newTinHuu(Model model) {
        model.addAttribute("title", "Thêm Tin Hữu");
        model.addAttribute("pageTitle", "Thêm Tin Hữu Mới");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuu", new com.branch.demo.domain.TinHuu());
        model.addAttribute("nhomList", adminService.getAllNhom());
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        return "admin/tin-huu/form";
    }

    @GetMapping("/tin-huu/edit/{id}")
    public String editTinHuu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Tin Hữu");
        model.addAttribute("pageTitle", "Chỉnh Sửa Tin Hữu");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuu", adminService.getTinHuuById(id));
        model.addAttribute("nhomList", adminService.getAllNhom());
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        return "admin/tin-huu/form";
    }

    @PostMapping("/tin-huu/save")
    public String saveTinHuu(@ModelAttribute com.branch.demo.domain.TinHuu tinHuu,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Nếu đang update và có avatar cũ, xóa avatar cũ
                if (tinHuu.getId() != null) {
                    com.branch.demo.domain.TinHuu existingTinHuu = adminService.getTinHuuById(tinHuu.getId());
                    if (existingTinHuu.getAvatarUrl() != null) {
                        fileUploadService.deleteAvatar(existingTinHuu.getAvatarUrl());
                    }
                }

                // Upload avatar mới
                String avatarUrl = fileUploadService.uploadAvatar(avatarFile);
                tinHuu.setAvatarUrl(avatarUrl);
            } else {
                // Nếu không có file upload
                if (tinHuu.getId() == null) {
                    // Tin hữu mới: set avatarUrl = null
                    tinHuu.setAvatarUrl(null);
                } else {
                    // Tin hữu cũ: giữ nguyên avatarUrl hiện tại
                    com.branch.demo.domain.TinHuu existingTinHuu = adminService.getTinHuuById(tinHuu.getId());
                    tinHuu.setAvatarUrl(existingTinHuu.getAvatarUrl());
                }
            }

            adminService.saveTinHuu(tinHuu);
            redirectAttributes.addFlashAttribute("success", "Tin hữu đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/tin-huu";
    }

    // Soft Delete - Xóa mềm
    @PostMapping("/tin-huu/delete/{id}")
    public String softDeleteTinHuu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.softDeleteTinHuu(id);
            redirectAttributes.addFlashAttribute("success", "Tin hữu đã được chuyển vào thùng rác!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa tin hữu: " + e.getMessage());
        }
        return "redirect:/admin/tin-huu";
    }

    // Hard Delete - Xóa vĩnh viễn
    @PostMapping("/tin-huu/hard-delete/{id}")
    public String hardDeleteTinHuu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Xóa avatar trước khi xóa vĩnh viễn
            com.branch.demo.domain.TinHuu tinHuu = adminService.getTinHuuById(id);
            if (tinHuu.getAvatarUrl() != null) {
                fileUploadService.deleteAvatar(tinHuu.getAvatarUrl());
            }

            adminService.hardDeleteTinHuu(id);
            redirectAttributes.addFlashAttribute("success", "Tin hữu đã được xóa vĩnh viễn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa vĩnh viễn tin hữu: " + e.getMessage());
        }
        return "redirect:/admin/tin-huu/deleted";
    }

    // Restore - Khôi phục
    @PostMapping("/tin-huu/restore/{id}")
    public String restoreTinHuu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.restoreTinHuu(id);
            redirectAttributes.addFlashAttribute("success", "Tin hữu đã được khôi phục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể khôi phục tin hữu: " + e.getMessage());
        }
        return "redirect:/admin/tin-huu/deleted";
    }

    // Danh sách tin hữu đã xóa
    @GetMapping("/tin-huu/deleted")
    public String deletedTinHuuList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search, Model model) {
        model.addAttribute("title", "Thùng Rác - Tin Hữu");
        model.addAttribute("pageTitle", "Thùng Rác - Tin Hữu Đã Xóa");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuuPage", adminService.getDeletedTinHuuPage(page, search));
        model.addAttribute("search", search);
        model.addAttribute("isDeletedView", true);
        return "admin/tin-huu/deleted";
    }

    @GetMapping("/tin-huu/view/{id}")
    public String viewTinHuu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Tin Hữu");
        model.addAttribute("pageTitle", "Chi Tiết Tin Hữu");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuu", adminService.getTinHuuById(id));
        return "admin/tin-huu/view";
    }

    // ==================== NHÓM MANAGEMENT ====================

    @GetMapping("/nhom")
    public String nhomList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long diemNhomId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                diemNhomId != null ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.Nhom> nhomPage;
        if (hasFilters) {
            nhomPage = adminService.getNhomPageWithFilters(page, search, trangThai,
                    diemNhomId, parsedFromDate, parsedToDate);
        } else {
            nhomPage = adminService.getNhomPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Nhóm");
        model.addAttribute("pageTitle", "Quản Lý Nhóm");
        model.addAttribute("activeMenu", "nhom");
        model.addAttribute("nhomPage", nhomPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("diemNhomId", diemNhomId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Add filter options
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());

        return "admin/nhom/list";
    }

    @GetMapping("/nhom/new")
    public String newNhom(Model model) {
        model.addAttribute("title", "Thêm Nhóm");
        model.addAttribute("pageTitle", "Thêm Nhóm Mới");
        model.addAttribute("activeMenu", "nhom");
        model.addAttribute("nhom", new com.branch.demo.domain.Nhom());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        return "admin/nhom/form";
    }

    @GetMapping("/nhom/edit/{id}")
    public String editNhom(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Nhóm");
        model.addAttribute("pageTitle", "Chỉnh Sửa Nhóm");
        model.addAttribute("activeMenu", "nhom");
        model.addAttribute("nhom", adminService.getNhomById(id));
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        return "admin/nhom/form";
    }

    @PostMapping("/nhom/save")
    public String saveNhom(@ModelAttribute com.branch.demo.domain.Nhom nhom,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveNhom(nhom);
            redirectAttributes.addFlashAttribute("success", "Nhóm đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/nhom";
    }

    @PostMapping("/nhom/delete/{id}")
    public String deleteNhom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteNhom(id);
            redirectAttributes.addFlashAttribute("success", "Nhóm đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa nhóm: " + e.getMessage());
        }
        return "redirect:/admin/nhom";
    }

    @GetMapping("/nhom/view/{id}")
    public String viewNhom(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Nhóm");
        model.addAttribute("pageTitle", "Chi Tiết Nhóm");
        model.addAttribute("activeMenu", "nhom");
        model.addAttribute("nhom", adminService.getNhomById(id));
        return "admin/nhom/view";
    }

    @GetMapping("/api/nhom/{id}/tin-huu")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getTinHuuByNhom(@PathVariable Long id) {
        return adminService.getTinHuuByNhomId(id);
    }

    @GetMapping("/api/diem-nhom/{id}/tin-huu")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getTinHuuByDiemNhom(@PathVariable Long id) {
        return adminService.getTinHuuByDiemNhomId(id);
    }

    @GetMapping("/api/tin-huu/available")
    @ResponseBody
    public java.util.Map<String, Object> getAvailableTinHuu() {
        return adminService.getAvailableTinHuuForNhom();
    }

    @PostMapping("/api/nhom/{id}/add-tin-huu")
    @ResponseBody
    public java.util.Map<String, Object> addTinHuuToNhom(@PathVariable Long id,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Integer> tinHuuIds = (java.util.List<Integer>) request.get("tinHuuIds");
            java.util.List<Long> longIds = tinHuuIds.stream()
                    .map(Integer::longValue)
                    .collect(java.util.stream.Collectors.toList());

            adminService.addTinHuuToNhom(id, longIds);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm tin hữu vào nhóm thành công");
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    @GetMapping("/api/nhom/all")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getAllNhom() {
        return adminService.getAllNhomForTransfer();
    }

    @PostMapping("/api/tin-huu/{id}/remove-from-nhom")
    @ResponseBody
    public java.util.Map<String, Object> removeTinHuuFromNhom(@PathVariable Long id) {
        try {
            adminService.removeTinHuuFromNhom(id);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa tin hữu khỏi nhóm thành công");
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    @PostMapping("/api/tin-huu/{id}/transfer-to-nhom")
    @ResponseBody
    public java.util.Map<String, Object> transferTinHuuToNhom(@PathVariable Long id,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Long targetNhomId = Long.valueOf(request.get("targetNhomId").toString());
            adminService.transferTinHuuToNhom(id, targetNhomId);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Đã chuyển tin hữu sang nhóm khác thành công");
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // // API endpoints for searchable dropdowns
    // @GetMapping("/api/all-ban-nganh")
    // @ResponseBody
    // public java.util.List<java.util.Map<String, Object>> getAllBanNganh() {
    // Service.getAllActiveBanNganh().stream()
    //
    // l.HashMap<>();
    //
    //
    //
    //
    //
    // .toList());
    // }

    @GetMapping("/api/all-nhom-with-diem-nhom")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getAllNhomWithDiemNhom() {
        return adminService.getAllNhom().stream()
                .map(nhom -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", nhom.getId());
                    map.put("tenNhom", nhom.getTenNhom());

                    // Thêm thông tin điểm nhóm nếu có
                    if (nhom.getDiemNhom() != null) {
                        java.util.Map<String, Object> diemNhomMap = new java.util.HashMap<>();
                        diemNhomMap.put("id", nhom.getDiemNhom().getId());
                        diemNhomMap.put("tenDiemNhom", nhom.getDiemNhom().getTenDiemNhom());
                        diemNhomMap.put("diaChi", nhom.getDiemNhom().getDiaChi());
                        map.put("diemNhom", diemNhomMap);
                    }

                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/ban-nganh")
    public String banNganhList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.BanNganh> banNganhPage;
        if (hasFilters) {
            banNganhPage = adminService.getBanNganhPageWithFilters(page, search, trangThai,
                    parsedFromDate, parsedToDate);
        } else {
            banNganhPage = adminService.getBanNganhPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Ban Ngành");
        model.addAttribute("pageTitle", "Quản Lý Ban Ngành");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganhPage", banNganhPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/ban-nganh/list";
    }

    @GetMapping("/ban-nganh/new")
    public String newBanNganh(Model model) {
        model.addAttribute("title", "Thêm Ban Ngành");
        model.addAttribute("pageTitle", "Thêm Ban Ngành Mới");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganh", new com.branch.demo.domain.BanNganh());

        // Thêm danh sách nhân sự và chấp sự để chọn trưởng ban/phó ban
        model.addAttribute("nhanSuList", adminService.getAllActiveNhanSu());
        model.addAttribute("chapSuList", adminService.getAllActiveChapSu());

        return "admin/ban-nganh/form";
    }

    @GetMapping("/ban-nganh/edit/{id}")
    public String editBanNganh(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Ban Ngành");
        model.addAttribute("pageTitle", "Chỉnh Sửa Ban Ngành");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganh", adminService.getBanNganhById(id));

        // Thêm danh sách nhân sự và chấp sự để chọn trưởng ban/phó ban
        model.addAttribute("nhanSuList", adminService.getAllActiveNhanSu());
        model.addAttribute("chapSuList", adminService.getAllActiveChapSu());

        return "admin/ban-nganh/form";
    }

    @PostMapping("/ban-nganh/save")
    public String saveBanNganh(@ModelAttribute com.branch.demo.domain.BanNganh banNganh,
            @RequestParam(required = false) String phoBanNhanSuIds,
            @RequestParam(required = false) String phoBanChapSuIds,
            @RequestParam(required = false) String thuQuyNhanSuIds,
            @RequestParam(required = false) String thuQuyChapSuIds,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveBanNganhWithManagement(banNganh, phoBanNhanSuIds, phoBanChapSuIds, thuQuyNhanSuIds, thuQuyChapSuIds);
            redirectAttributes.addFlashAttribute("success", "Ban ngành đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/ban-nganh";
    }

    @GetMapping("/api/check-ma-ban-duplicate")
    @ResponseBody
    public java.util.Map<String, Object> checkMaBanDuplicate(
            @RequestParam String maBan,
            @RequestParam(required = false) String currentId) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        try {
            java.util.Optional<com.branch.demo.domain.BanNganh> existing = banNganhRepository
                    .findByMaBan(maBan.toUpperCase().trim());

            boolean isDuplicate = false;
            if (existing.isPresent()) {
                // If currentId is provided (edit mode), check if it's the same record
                if (currentId == null || currentId.trim().isEmpty() ||
                        !existing.get().getId().toString().equals(currentId)) {
                    isDuplicate = true;
                }
            }

            result.put("isDuplicate", isDuplicate);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @PostMapping("/ban-nganh/delete/{id}")
    public String deleteBanNganh(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteBanNganh(id);
            redirectAttributes.addFlashAttribute("success", "Ban ngành đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa ban ngành: " + e.getMessage());
        }
        return "redirect:/admin/ban-nganh";
    }

    @GetMapping("/ban-nganh/view/{id}")
    public String viewBanNganh(@PathVariable Long id, Model model) {
        com.branch.demo.domain.BanNganh banNganh = adminService.getBanNganhById(id);

        model.addAttribute("title", "Chi Tiết Ban Ngành");
        model.addAttribute("pageTitle", "Chi Tiết Ban Ngành");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganh", banNganh);

        // Build danh sách nhân sự và chấp sự trong ban ngành
        model.addAttribute("danhSachNhanSu", adminService.getNhanSuByBanNganhId(id));
        model.addAttribute("danhSachChapSu", adminService.getChapSuByBanNganhId(id));

        return "admin/ban-nganh/view";
    }

    // API endpoints for searchable dropdowns
    // @GetMapping("/api/all-nhan-su")
    // @ResponseBody
    // public java.util.List<com.branch.demo.domain.NhanSu> getAllNhanSu() {
    // return adminService.getAllActiveNhanSu();
    // }

    @GetMapping("/api/all-chap-su")
    @ResponseBody
    public java.util.List<ChapSuDTO> getAllChapSu() {
        java.util.List<com.branch.demo.domain.ChapSu> chapSuList = adminService.getAllActiveChapSu();
        return chapSuList.stream()
                .map(this::convertToChapSuDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // API để lấy chấp sự chưa có tài khoản (cho form account)
    @GetMapping("/api/chap-su-without-account")
    @ResponseBody
    public java.util.List<ChapSuDTO> getChapSuWithoutAccount() {
        return adminService.getChapSuWithoutAccount();
    }
    
    // API để lấy chấp sự cho edit account (bao gồm người hiện tại)
    @GetMapping("/api/chap-su-for-account-edit")
    @ResponseBody
    public java.util.List<ChapSuDTO> getChapSuForAccountEdit(@RequestParam Long accountId) {
        return adminService.getChapSuForAccountEdit(accountId);
    }
    
    private ChapSuDTO convertToChapSuDTO(com.branch.demo.domain.ChapSu chapSu) {
        ChapSuDTO dto = new ChapSuDTO();
        dto.setId(chapSu.getId());
        dto.setHoTen(chapSu.getHoTen());
        dto.setChucVu(chapSu.getChucVu() != null ? chapSu.getChucVu().getDisplayName() : null);
        dto.setDienThoai(chapSu.getDienThoai());
        dto.setEmail(chapSu.getEmail());
        dto.setAvatarUrl(chapSu.getAvatarUrl());
        // Không include banNganh và diemNhom để tránh lazy loading issues
        return dto;
    }


    // @GetMapping("/api/ban-nganh/{id}/nhan-su")
    // @ResponseBody
    // public java.util.List<com.branch.demo.domain.NhanSu>
    // getBanNganhNhanSu(@PathVariable Long id) {
    // return adminService.getNhanSuByBanNganhId(id);
    // }

    // ==================== CHẤP SỰ MANAGEMENT ====================

    @GetMapping("/chap-su")
    public String chapSuList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long banNganhId,
            @RequestParam(required = false) Long diemNhomId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                banNganhId != null || diemNhomId != null ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.ChapSu> chapSuPage;
        if (hasFilters) {
            chapSuPage = adminService.getChapSuPageWithFilters(page, search, trangThai,
                    banNganhId, diemNhomId, parsedFromDate, parsedToDate);
        } else {
            chapSuPage = adminService.getChapSuPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Chấp Sự");
        model.addAttribute("pageTitle", "Quản Lý Chấp Sự");
        model.addAttribute("activeMenu", "chap-su");
        model.addAttribute("chapSuPage", chapSuPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("banNganhId", banNganhId);
        model.addAttribute("diemNhomId", diemNhomId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Add filter options
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());

        return "admin/chap-su/list";
    }

    @GetMapping("/chap-su/new")
    public String newChapSu(Model model) {
        model.addAttribute("title", "Thêm Chấp Sự");
        model.addAttribute("pageTitle", "Thêm Chấp Sự Mới");
        model.addAttribute("activeMenu", "chap-su");
        model.addAttribute("chapSu", new com.branch.demo.domain.ChapSu());
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        model.addAttribute("chucVuList", com.branch.demo.domain.ChapSu.ChucVu.values());
        return "admin/chap-su/form";
    }

    @GetMapping("/chap-su/{id}/edit")
    public String editChapSu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Chấp Sự");
        model.addAttribute("pageTitle", "Chỉnh Sửa Chấp Sự");
        model.addAttribute("activeMenu", "chap-su");
        model.addAttribute("chapSu", adminService.getChapSuById(id));
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        model.addAttribute("chucVuList", com.branch.demo.domain.ChapSu.ChucVu.values());
        return "admin/chap-su/form";
    }

    @PostMapping("/chap-su/save")
    public String saveChapSu(@ModelAttribute com.branch.demo.domain.ChapSu chapSu,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Nếu đang update và có avatar cũ, xóa avatar cũ
                if (chapSu.getId() != null) {
                    com.branch.demo.domain.ChapSu existingChapSu = adminService.getChapSuById(chapSu.getId());
                    if (existingChapSu.getAvatarUrl() != null) {
                        fileUploadService.deleteAvatar(existingChapSu.getAvatarUrl());
                    }
                }

                // Upload avatar mới
                String avatarUrl = fileUploadService.uploadAvatar(avatarFile);
                chapSu.setAvatarUrl(avatarUrl);
            } else {
                // Nếu không có file upload
                if (chapSu.getId() == null) {
                    // Chấp sự mới: set avatarUrl = null
                    chapSu.setAvatarUrl(null);
                } else {
                    // Chấp sự cũ: giữ nguyên avatarUrl hiện tại
                    com.branch.demo.domain.ChapSu existingChapSu = adminService.getChapSuById(chapSu.getId());
                    chapSu.setAvatarUrl(existingChapSu.getAvatarUrl());
                }
            }

            adminService.saveChapSu(chapSu);
            redirectAttributes.addFlashAttribute("success", "Chấp sự đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/chap-su";
    }

    @PostMapping("/chap-su/delete/{id}")
    public String deleteChapSu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Xóa avatar trước khi xóa chấp sự
            com.branch.demo.domain.ChapSu chapSu = adminService.getChapSuById(id);
            if (chapSu.getAvatarUrl() != null) {
                fileUploadService.deleteAvatar(chapSu.getAvatarUrl());
            }

            adminService.deleteChapSu(id);
            redirectAttributes.addFlashAttribute("success", "Chấp sự đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa chấp sự: " + e.getMessage());
        }
        return "redirect:/admin/chap-su";
    }

    @GetMapping("/chap-su/{id}")
    public String viewChapSu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Chấp Sự");
        model.addAttribute("pageTitle", "Chi Tiết Chấp Sự");
        model.addAttribute("activeMenu", "chap-su");
        model.addAttribute("chapSu", adminService.getChapSuById(id));
        return "admin/chap-su/view";
    }

    // ==================== NHÂN SỰ MANAGEMENT ====================

    @GetMapping("/nhan-su")
    public String nhanSuList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long banNganhId,
            @RequestParam(required = false) Long diemNhomId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                banNganhId != null || diemNhomId != null ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.NhanSu> nhanSuPage;
        if (hasFilters) {
            nhanSuPage = adminService.getNhanSuPageWithFilters(page, search, trangThai,
                    banNganhId, diemNhomId, parsedFromDate, parsedToDate);
        } else {
            nhanSuPage = adminService.getNhanSuPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Nhân Sự");
        model.addAttribute("pageTitle", "Quản Lý Nhân Sự");
        model.addAttribute("activeMenu", "nhan-su");
        model.addAttribute("nhanSuPage", nhanSuPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("banNganhId", banNganhId);
        model.addAttribute("diemNhomId", diemNhomId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Add filter options
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());

        return "admin/nhan-su/list";
    }

    @GetMapping("/nhan-su/new")
    public String newNhanSu(Model model) {
        model.addAttribute("title", "Thêm Nhân Sự");
        model.addAttribute("pageTitle", "Thêm Nhân Sự Mới");
        model.addAttribute("activeMenu", "nhan-su");
        model.addAttribute("nhanSu", new com.branch.demo.domain.NhanSu());
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        model.addAttribute("chucVuList", com.branch.demo.domain.NhanSu.ChucVu.values());
        return "admin/nhan-su/form";
    }

    @GetMapping("/nhan-su/{id}/edit")
    public String editNhanSu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Nhân Sự");
        model.addAttribute("pageTitle", "Chỉnh Sửa Nhân Sự");
        model.addAttribute("activeMenu", "nhan-su");
        model.addAttribute("nhanSu", adminService.getNhanSuById(id));
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        model.addAttribute("diemNhomList", adminService.getAllActiveDiemNhom());
        model.addAttribute("chucVuList", com.branch.demo.domain.NhanSu.ChucVu.values());
        return "admin/nhan-su/form";
    }

    @PostMapping("/nhan-su/save")
    public String saveNhanSu(@ModelAttribute com.branch.demo.domain.NhanSu nhanSu,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Nếu đang update và có avatar cũ, xóa avatar cũ
                if (nhanSu.getId() != null) {
                    com.branch.demo.domain.NhanSu existingNhanSu = adminService.getNhanSuById(nhanSu.getId());
                    if (existingNhanSu.getAvatarUrl() != null) {
                        fileUploadService.deleteAvatar(existingNhanSu.getAvatarUrl());
                    }
                }

                // Upload avatar mới
                String avatarUrl = fileUploadService.uploadAvatar(avatarFile);
                nhanSu.setAvatarUrl(avatarUrl);
            } else {
                // Nếu không có file upload
                if (nhanSu.getId() == null) {
                    // Nhân sự mới: set avatarUrl = null
                    nhanSu.setAvatarUrl(null);
                } else {
                    // Nhân sự cũ: giữ nguyên avatarUrl hiện tại
                    com.branch.demo.domain.NhanSu existingNhanSu = adminService.getNhanSuById(nhanSu.getId());
                    nhanSu.setAvatarUrl(existingNhanSu.getAvatarUrl());
                }
            }

            adminService.saveNhanSu(nhanSu);
            redirectAttributes.addFlashAttribute("success", "Nhân sự đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/nhan-su";
    }

    @PostMapping("/nhan-su/{id}/delete")
    public String deleteNhanSu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Xóa avatar trước khi xóa nhân sự
            com.branch.demo.domain.NhanSu nhanSu = adminService.getNhanSuById(id);
            if (nhanSu.getAvatarUrl() != null) {
                fileUploadService.deleteAvatar(nhanSu.getAvatarUrl());
            }

            adminService.deleteNhanSu(id);
            redirectAttributes.addFlashAttribute("success", "Nhân sự đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa nhân sự: " + e.getMessage());
        }
        return "redirect:/admin/nhan-su";
    }

    @GetMapping("/nhan-su/{id}")
    public String viewNhanSu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Nhân Sự");
        model.addAttribute("pageTitle", "Chi Tiết Nhân Sự");
        model.addAttribute("activeMenu", "nhan-su");
        model.addAttribute("nhanSu", adminService.getNhanSuById(id));
        return "admin/nhan-su/view";
    }

    // API endpoint để lấy điểm nhóm theo ban ngành
    @GetMapping("/api/diem-nhom-by-ban-nganh/{banNganhId}")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.DiemNhomDTO> getDiemNhomByBanNganh(@PathVariable Long banNganhId) {
        java.util.List<com.branch.demo.domain.DiemNhom> diemNhomList = adminService.getDiemNhomByBanNganh(banNganhId);
        return diemNhomList.stream()
                .map(adminService::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    // API endpoint để lấy tất cả điểm nhóm
    @GetMapping("/api/all-diem-nhom")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.DiemNhomDTO> getAllDiemNhom() {
        java.util.List<com.branch.demo.domain.DiemNhom> diemNhomList = adminService.getAllActiveDiemNhom();
        return diemNhomList.stream()
                .map(adminService::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    // API endpoint để lấy tất cả danh mục
    @GetMapping("/api/all-danh-muc")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.DanhMucDTO> getAllDanhMuc() {
        java.util.List<com.branch.demo.domain.DanhMuc> danhMucList = adminService.getAllActiveDanhMuc();
        return danhMucList.stream()
                .map(adminService::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    // API endpoint để lấy tất cả nhân sự
    @GetMapping("/api/all-nhan-su")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.NhanSuDTO> getAllNhanSu() {
        try {
            java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = nhanSuRepository.findAll();
            System.out.println("DEBUG - Found " + nhanSuList.size() + " nhan su records");

            return nhanSuList.stream()
                    .map(ns -> new com.branch.demo.dto.NhanSuDTO(
                            ns.getId(),
                            ns.getHoTen() != null ? ns.getHoTen() : "",
                            ns.getChucVu() != null ? ns.getChucVu().getDisplayName() : "",
                            ns.getBanNganh() != null ? ns.getBanNganh().getTenBan() : "",
                            ns.getEmail() != null ? ns.getEmail() : "",
                            ns.getDienThoai() != null ? ns.getDienThoai() : ""))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("ERROR in getAllNhanSu: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    // API để lấy nhân sự chưa có tài khoản (cho form account)
    @GetMapping("/api/nhan-su-without-account")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.NhanSuDTO> getNhanSuWithoutAccount() {
        return adminService.getNhanSuWithoutAccount();
    }
    
    // API để lấy nhân sự cho edit account (bao gồm người hiện tại)
    @GetMapping("/api/nhan-su-for-account-edit")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.NhanSuDTO> getNhanSuForAccountEdit(@RequestParam Long accountId) {
        return adminService.getNhanSuForAccountEdit(accountId);
    }

    @GetMapping("/api/ban-nganh/{banNganhId}/nhan-su")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getNhanSuByBanNganh(@PathVariable Long banNganhId) {
        java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = adminService.getNhanSuByBanNganh(banNganhId);
        return nhanSuList.stream()
                .map(nhanSu -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", nhanSu.getId());
                    map.put("hoTen", nhanSu.getHoTen());
                    map.put("chucVu", nhanSu.getChucVu());
                    map.put("email", nhanSu.getEmail());
                    map.put("dienThoai", nhanSu.getDienThoai());
                    map.put("avatarUrl", nhanSu.getAvatarUrl());
                    map.put("trangThai", nhanSu.getTrangThai().name());
                    map.put("diemNhomTen", nhanSu.getDiemNhom() != null ? nhanSu.getDiemNhom().getTenDiemNhom() : null);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/api/diem-nhom/{diemNhomId}/nhom")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getNhomByDiemNhom(@PathVariable Long diemNhomId) {
        java.util.List<com.branch.demo.domain.Nhom> nhomList = adminService.getNhomByDiemNhom(diemNhomId);
        return nhomList.stream()
                .map(nhom -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", nhom.getId());
                    map.put("tenNhom", nhom.getTenNhom());
                    map.put("soLuongTinHuu", nhom.getSoLuongTinHuu());
                    map.put("trangThai", nhom.getTrangThai().name());
                    map.put("thoiGianSinhHoat", nhom.getThoiGianSinhHoat());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/api/nhom-by-diem-nhom/{diemNhomId}")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getNhomByDiemNhom_2(@PathVariable Long diemNhomId) {
        java.util.List<com.branch.demo.domain.Nhom> nhomList = adminService.getNhomByDiemNhom(diemNhomId);
        return nhomList.stream()
                .map(nhom -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", nhom.getId());
                    map.put("tenNhom", nhom.getTenNhom());
                    map.put("soLuongTinHuu", nhom.getSoLuongTinHuu());
                    map.put("trangThai", nhom.getTrangThai().name());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    // @PostMapping("/api/nhom/transfer")
    // @ResponseBody
    // public java.util.Map<String, Object> transferNhom(@RequestBody
    // java.util.Map<String, Object> request) {
    // java.util.Map<String, Object> response = new java.util.HashMap<>();
    // try {
    // Long nhomId = Long.valueOf(request.get("nhomId").toString());
    // Long targetDiemNhomId =
    // Long.valueOf(request.get("targetDiemNhomId").toString());

    // adminService.transferNhomToDiemNhom(nhomId, targetDiemNhomId);

    // response.put("success", true);
    // response.put("message", "Chuyển nhóm thành công");
    // } catch (Exception e) {
    // response.put("success", false);
    // response.put("message", e.getMessage());
    // }
    // return response;
    // }

    // @PostMapping("/api/nhom/delete")
    // @ResponseBody
    // public java.util.Map<String, Object> deleteNhomWithTinHuu(@RequestBody
    // java.util.Map<String, Object> request) {
    // java.util.Map<String, Object> response = new java.util.HashMap<>();
    // try {
    // Long nhomId = Long.valueOf(request.get("nhomId").toString());
    // String tinHuuAction = request.get("tinHuuAction").toString();
    // Long targetNhomId = request.get("targetNhomId") != null ?
    // Long.valueOf(request.get("targetNhomId").toString()) : null;

    // adminService.deleteNhomWithTinHuuHandling(nhomId, tinHuuAction,
    // targetNhomId);

    // response.put("success", true);
    // response.put("message", "Xóa nhóm thành công");
    // } catch (Exception e) {
    // response.put("success", false);
    // response.put("message", e.getMessage());
    // }
    // return response;
    // }

    @PostMapping("/su-kien/{id}/delete")
    public String deleteSuKien(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteSuKien(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sự kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa sự kiện: " + e.getMessage());
        }

        return "redirect:/admin/su-kien";
    }

    @GetMapping("/su-kien/deleted")
    public String deletedSuKienList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        Page<com.branch.demo.domain.SuKien> deletedSuKienPage = adminService.getDeletedSuKienPage(page, search, fromDate, toDate);

        model.addAttribute("title", "Thùng Rác Sự Kiện");
        model.addAttribute("pageTitle", "Thùng Rác Sự Kiện");
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("deletedSuKienPage", deletedSuKienPage);
        model.addAttribute("search", search);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/su-kien/deleted";
    }

    @PostMapping("/su-kien/{id}/restore")
    public String restoreSuKien(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.restoreSuKien(id);
            redirectAttributes.addFlashAttribute("success", "Khôi phục sự kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi khôi phục sự kiện: " + e.getMessage());
        }

        return "redirect:/admin/su-kien/deleted";
    }

    @PostMapping("/su-kien/{id}/hard-delete")
    public String hardDeleteSuKien(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.hardDeleteSuKien(id);
            redirectAttributes.addFlashAttribute("success", "Xóa vĩnh viễn sự kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa vĩnh viễn sự kiện: " + e.getMessage());
        }

        return "redirect:/admin/su-kien/deleted";
    }

    @PostMapping("/su-kien/empty-trash")
    public String emptySuKienTrash(RedirectAttributes redirectAttributes) {
        try {
            adminService.emptyDeletedSuKienTrash();
            redirectAttributes.addFlashAttribute("success", "Đã dọn sạch thùng rác sự kiện!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi dọn thùng rác: " + e.getMessage());
        }
        return "redirect:/admin/su-kien/deleted";
    }

    @GetMapping("/thong-bao")
    public String thongBaoManagement(Model model) {
        model.addAttribute("title", "Quản Lý Thông Báo");
        model.addAttribute("pageTitle", "Quản Lý Thông Báo");
        model.addAttribute("activeMenu", "thong-bao");
        return "admin/thong-bao";
    }

    // ==================== ĐIỂM NHÓM MANAGEMENT ====================

    @GetMapping("/diem-nhom")
    public String diemNhomList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.DiemNhom> diemNhomPage;
        if (hasFilters) {
            diemNhomPage = adminService.getDiemNhomPageWithFilters(page, search, trangThai,
                    parsedFromDate, parsedToDate);
        } else {
            diemNhomPage = adminService.getDiemNhomPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Điểm Nhóm");
        model.addAttribute("pageTitle", "Quản Lý Điểm Nhóm");
        model.addAttribute("activeMenu", "diem-nhom");
        model.addAttribute("diemNhomPage", diemNhomPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/diem-nhom/list";
    }

    @GetMapping("/diem-nhom/new")
    public String newDiemNhom(Model model) {
        model.addAttribute("title", "Thêm Điểm Nhóm");
        model.addAttribute("pageTitle", "Thêm Điểm Nhóm Mới");
        model.addAttribute("activeMenu", "diem-nhom");
        model.addAttribute("diemNhom", new com.branch.demo.domain.DiemNhom());
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        return "admin/diem-nhom/form";
    }

    @GetMapping("/diem-nhom/edit/{id}")
    public String editDiemNhom(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Điểm Nhóm");
        model.addAttribute("pageTitle", "Chỉnh Sửa Điểm Nhóm");
        model.addAttribute("activeMenu", "diem-nhom");
        model.addAttribute("diemNhom", adminService.getDiemNhomById(id));
        model.addAttribute("banNganhList", adminService.getAllActiveBanNganh());
        return "admin/diem-nhom/form";
    }

    @PostMapping("/diem-nhom/save")
    public String saveDiemNhom(@ModelAttribute com.branch.demo.domain.DiemNhom diemNhom,
            @RequestParam(required = false) String selectedBanNganh,
            RedirectAttributes redirectAttributes) {
        try {
            // Process selected ban nganh
            if (selectedBanNganh != null && !selectedBanNganh.trim().isEmpty()) {
                String[] banNganhIds = selectedBanNganh.split(",");
                java.util.List<com.branch.demo.domain.BanNganh> banNganhList = new java.util.ArrayList<>();

                for (String idStr : banNganhIds) {
                    try {
                        Long id = Long.parseLong(idStr.trim());
                        com.branch.demo.domain.BanNganh banNganh = adminService.getBanNganhById(id);
                        if (banNganh != null) {
                            banNganhList.add(banNganh);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
                diemNhom.setDanhSachBanNganh(banNganhList);
            }

            adminService.saveDiemNhom(diemNhom);
            redirectAttributes.addFlashAttribute("success", "Điểm nhóm đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/diem-nhom";
    }

    // API endpoint để lấy tất cả ban ngành
    @GetMapping("/api/all-ban-nganh")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.BanNganhDTO> getAllBanNganh() {
        return adminService.getAllActiveBanNganhDTO();
    }

    @GetMapping("/api/diem-nhom/{id}/ban-nganh")
    @ResponseBody
    public java.util.Map<String, Object> getDiemNhomBanNganh(@PathVariable Long id) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        try {
            com.branch.demo.domain.DiemNhom diemNhom = adminService.getDiemNhomById(id);

            // Debug info
            result.put("diemNhomId", diemNhom.getId());
            result.put("tenDiemNhom", diemNhom.getTenDiemNhom());
            result.put("danhSachBanNganhSize",
                    diemNhom.getDanhSachBanNganh() != null ? diemNhom.getDanhSachBanNganh().size() : 0);

            // Convert to simple list
            java.util.List<java.util.Map<String, Object>> banNganhList = new java.util.ArrayList<>();
            if (diemNhom.getDanhSachBanNganh() != null) {
                for (com.branch.demo.domain.BanNganh banNganh : diemNhom.getDanhSachBanNganh()) {
                    java.util.Map<String, Object> banNganhData = new java.util.HashMap<>();
                    banNganhData.put("id", banNganh.getId());
                    banNganhData.put("tenBan", banNganh.getTenBan());
                    banNganhData.put("maBan", banNganh.getMaBan());
                    banNganhList.add(banNganhData);
                }
            }

            result.put("banNganhList", banNganhList);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @GetMapping("/diem-nhom/view/{id}")
    public String viewDiemNhom(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Điểm Nhóm");
        model.addAttribute("pageTitle", "Chi Tiết Điểm Nhóm");
        model.addAttribute("activeMenu", "diem-nhom");
        model.addAttribute("diemNhom", adminService.getDiemNhomById(id));
        return "admin/diem-nhom/view";
    }

    @PostMapping("/diem-nhom/delete/{id}")
    public String deleteDiemNhom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteDiemNhom(id);
            redirectAttributes.addFlashAttribute("success",
                    "Điểm nhóm đã được xóa thành công! Tất cả nhóm và tin hữu liên quan đã được xử lý.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa điểm nhóm: " + e.getMessage());
        }
        return "redirect:/admin/diem-nhom";
    }

    @PostMapping("/diem-nhom/delete-with-handling/{id}")
    public String deleteDiemNhomWithHandling(@PathVariable Long id,
            @RequestParam String tinHuuAction,
            @RequestParam(required = false) Long targetDiemNhomId,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteDiemNhomWithTinHuuHandling(id, tinHuuAction, targetDiemNhomId);

            String message = "Điểm nhóm đã được xóa thành công! ";
            if ("transfer".equals(tinHuuAction) && targetDiemNhomId != null) {
                message += "Tất cả tin hữu đã được chuyển sang điểm nhóm khác.";
            } else {
                message += "Tất cả tin hữu đã được đặt về trạng thái chưa có nhóm.";
            }

            redirectAttributes.addFlashAttribute("success", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa điểm nhóm: " + e.getMessage());
        }
        return "redirect:/admin/diem-nhom";
    }

    // ==================== SU KIEN MANAGEMENT ====================

    @GetMapping("/su-kien")
    public String suKienList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String loaiSuKienId,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Xác định field và hướng sắp xếp
        String sortField;
        org.springframework.data.domain.Sort.Direction direction;

        switch (sortBy) {
            case "ngayDienRa":
                sortField = "ngayDienRa";
                direction = org.springframework.data.domain.Sort.Direction.ASC;
                break;
            case "tenSuKien":
                sortField = "tenSuKien";
                direction = org.springframework.data.domain.Sort.Direction.ASC;
                break;
            case "createdAt":
            default:
                sortField = "createdAt";
                direction = org.springframework.data.domain.Sort.Direction.DESC; // Mới nhất trước
                break;
        }

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(direction, sortField);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);

        org.springframework.data.domain.Page<com.branch.demo.domain.SuKien> suKienPage = adminService
                .searchSuKien(search, loaiSuKienId, trangThai, fromDate, toDate, pageable);

        model.addAttribute("suKienPage", suKienPage);
        model.addAttribute("loaiSuKienList", adminService.getAllActiveLoaiSuKien());
        model.addAttribute("danhSachTrangThai", com.branch.demo.domain.SuKien.TrangThaiSuKien.values());
        model.addAttribute("search", search);
        model.addAttribute("loaiSuKienId", loaiSuKienId);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("title", "Quản Lý Sự Kiện");
        model.addAttribute("pageTitle", "Quản Lý Sự Kiện");

        return "admin/su-kien/list";
    }

    @GetMapping("/su-kien/new")
    public String newSuKien(Model model) {
        SuKienDTO suKienDTO = new SuKienDTO();
        model.addAttribute("suKien", suKienDTO);
        model.addAttribute("danhSachLoaiSuKien", adminService.getAllActiveLoaiSuKien());
        model.addAttribute("danhSachTrangThai", com.branch.demo.domain.SuKien.TrangThaiSuKien.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("title", "Quản Lý Sự Kiện");
        model.addAttribute("pageTitle", "Quản Lý Sự Kiện");

        return "admin/su-kien/form";
    }

    @GetMapping("/su-kien/{id}/edit")
    public String editSuKien(@PathVariable Long id, Model model) {
        try {
            com.branch.demo.domain.SuKien suKien = adminService.getSuKienById(id);
            SuKienDTO suKienDTO = new SuKienDTO(suKien);

            model.addAttribute("suKien", suKienDTO);
            model.addAttribute("danhSachLoaiSuKien", adminService.getAllActiveLoaiSuKien());
            model.addAttribute("danhSachTrangThai", com.branch.demo.domain.SuKien.TrangThaiSuKien.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "su-kien");
            model.addAttribute("title", "Quản Lý Sự Kiện");
            model.addAttribute("pageTitle", "Quản Lý Sự Kiện");

            return "admin/su-kien/form";
        } catch (Exception e) {
            return "redirect:/admin/su-kien?error=notfound";
        }
    }

    @PostMapping("/su-kien/save")
    public String saveSuKien(@ModelAttribute("suKien") SuKienDTO suKienDTO, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG - Received SuKienDTO: " + suKienDTO.getTenSuKien());
            System.out.println("DEBUG - LoaiSuKienId: " + suKienDTO.getLoaiSuKienId());

            adminService.saveSuKien(suKienDTO);
            redirectAttributes.addFlashAttribute("success",
                    suKienDTO.getId() != null ? "Cập nhật sự kiện thành công!" : "Thêm sự kiện thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/su-kien";
    }

    @GetMapping("/su-kien/{id}")
    public String viewSuKien(@PathVariable Long id, Model model) {
        try {
            com.branch.demo.domain.SuKien suKien = adminService.getSuKienById(id);
            model.addAttribute("suKien", suKien);
            model.addAttribute("activeMenu", "su-kien");
            model.addAttribute("title", "Quản Lý Sự Kiện");
            model.addAttribute("pageTitle", "Quản Lý Sự Kiện");

            return "admin/su-kien/view";
        } catch (Exception e) {
            return "redirect:/admin/su-kien?error=notfound";
        }
    }

    // ==================== API ENDPOINTS ====================



    // ==================== LOẠI SỰ KIỆN MANAGEMENT ====================

    @GetMapping("/loai-su-kien")
    public String listLoaiSuKien(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search) {
        Page<com.branch.demo.domain.LoaiSuKien> loaiSuKienPage = adminService.getLoaiSuKienPage(page, search);

        // Tạo Map chứa số lượng sự kiện cho mỗi loại sự kiện
        java.util.Map<Long, Long> suKienCountMap = new java.util.HashMap<>();
        for (com.branch.demo.domain.LoaiSuKien loaiSuKien : loaiSuKienPage.getContent()) {
            long count = adminService.countSuKienByLoaiSuKien(loaiSuKien.getId());
            suKienCountMap.put(loaiSuKien.getId(), count);
        }

        model.addAttribute("loaiSuKienPage", loaiSuKienPage);
        model.addAttribute("suKienCountMap", suKienCountMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("totalPages", loaiSuKienPage.getTotalPages());
        model.addAttribute("totalElements", loaiSuKienPage.getTotalElements());
        model.addAttribute("title", "Quản Lý Loại Sự Kiện");
        model.addAttribute("pageTitle", "Quản Lý Loại Sự Kiện");

        return "admin/loai-su-kien/list";
    }

    @GetMapping("/loai-su-kien/new")
    public String newLoaiSuKien(Model model) {
        model.addAttribute("loaiSuKien", new com.branch.demo.domain.LoaiSuKien());
        model.addAttribute("activeMenu", "loai-su-kien");
        model.addAttribute("title", "Thêm Loại Sự Kiện");
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("pageTitle", "Thêm Loại Sự Kiện Mới");
        return "admin/loai-su-kien/form";
    }

    @GetMapping("/loai-su-kien/{id}/edit")
    public String editLoaiSuKien(@PathVariable Long id, Model model) {
        try {
            com.branch.demo.domain.LoaiSuKien loaiSuKien = adminService.getLoaiSuKienById(id);
            model.addAttribute("loaiSuKien", loaiSuKien);
            model.addAttribute("activeMenu", "loai-su-kien");
            model.addAttribute("title", "Chỉnh Sửa Loại Sự Kiện");
            model.addAttribute("activeMenu", "su-kien");
            model.addAttribute("pageTitle", "Chỉnh Sửa Loại Sự Kiện");
            return "admin/loai-su-kien/form";
        } catch (Exception e) {
            return "redirect:/admin/loai-su-kien?error=notfound";
        }
    }

    @PostMapping("/loai-su-kien/save")
    public String saveLoaiSuKien(@ModelAttribute com.branch.demo.domain.LoaiSuKien loaiSuKien,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveLoaiSuKien(loaiSuKien);
            redirectAttributes.addFlashAttribute("success", "Loại sự kiện đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/loai-su-kien";
    }

    @PostMapping("/loai-su-kien/delete/{id}")
    public String deleteLoaiSuKien(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteLoaiSuKien(id);
            redirectAttributes.addFlashAttribute("success", "Loại sự kiện đã được xóa thành công!");
        } catch (RuntimeException e) {
            // Hiển thị thông báo lỗi thân thiện từ service
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa loại sự kiện. Vui lòng thử lại.");
        }
        return "redirect:/admin/loai-su-kien";
    }

    // ==================== BÀI VIẾT MANAGEMENT ====================

    @GetMapping("/bai-viet")
    public String baiVietList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Parse dates
        java.time.LocalDate parsedFromDate = null;
        java.time.LocalDate parsedToDate = null;
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                parsedFromDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                parsedToDate = java.time.LocalDate.parse(toDate);
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        // Check if any filters are applied
        boolean hasFilters = (trangThai != null && !trangThai.isEmpty()) ||
                danhMucId != null ||
                parsedFromDate != null || parsedToDate != null;

        Page<com.branch.demo.domain.BaiViet> baiVietPage;
        if (hasFilters) {
            baiVietPage = adminService.getBaiVietPageWithFilters(page, search, trangThai,
                    danhMucId, parsedFromDate, parsedToDate);
        } else {
            baiVietPage = adminService.getBaiVietPage(page, search);
        }

        model.addAttribute("title", "Quản Lý Bài Viết");
        model.addAttribute("pageTitle", "Quản Lý Bài Viết");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("baiVietPage", baiVietPage);
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("danhMucId", danhMucId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Add filter options
        model.addAttribute("danhMucList", adminService.getAllActiveDanhMuc());

        return "admin/bai-viet/list";
    }

    @GetMapping("/bai-viet/new")
    public String newBaiViet(Model model) {
        model.addAttribute("title", "Thêm Bài Viết");
        model.addAttribute("pageTitle", "Thêm Bài Viết Mới");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("baiViet", new com.branch.demo.domain.BaiViet());
        model.addAttribute("danhMucList", adminService.getAllActiveDanhMuc());
        return "admin/bai-viet/form";
    }

    @GetMapping("/bai-viet/edit/{id}")
    public String editBaiViet(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Bài Viết");
        model.addAttribute("pageTitle", "Chỉnh Sửa Bài Viết");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("baiViet", adminService.getBaiVietById(id));
        model.addAttribute("danhMucList", adminService.getAllActiveDanhMuc());
        return "admin/bai-viet/form";
    }

    @PostMapping("/bai-viet/save")
    public String saveBaiViet(@ModelAttribute com.branch.demo.domain.BaiViet baiViet,
            @RequestParam(value = "anhDaiDienFile", required = false) MultipartFile anhDaiDienFile,
            @RequestParam(value = "hinhAnhFiles", required = false) MultipartFile[] hinhAnhFiles,
            RedirectAttributes redirectAttributes) {
        try {
            // Nếu đang update (có ID), preserve các thông tin quan trọng
            if (baiViet.getId() != null) {
                com.branch.demo.domain.BaiViet existingBaiViet = adminService.getBaiVietById(baiViet.getId());

                // Preserve ngày tạo và lượt xem
                baiViet.setCreatedAt(existingBaiViet.getCreatedAt());
                baiViet.setLuotXem(existingBaiViet.getLuotXem());

                // Preserve ảnh đại diện nếu không upload ảnh mới
                if (anhDaiDienFile == null || anhDaiDienFile.isEmpty()) {
                    baiViet.setAnhDaiDien(existingBaiViet.getAnhDaiDien());
                } else {
                    // Xóa ảnh cũ nếu có ảnh mới
                    if (existingBaiViet.getAnhDaiDien() != null) {
                        try {
                            fileUploadService.deleteImage(existingBaiViet.getAnhDaiDien());
                        } catch (Exception e) {
                            // Log error but continue
                            System.err.println("Could not delete old image: " + e.getMessage());
                        }
                    }
                    String anhDaiDienUrl = fileUploadService.uploadImage(anhDaiDienFile);
                    baiViet.setAnhDaiDien(anhDaiDienUrl);
                }

                // Preserve hình ảnh bổ sung nếu không upload hình mới
                if (hinhAnhFiles == null || hinhAnhFiles.length == 0 ||
                        (hinhAnhFiles.length == 1 && hinhAnhFiles[0].isEmpty())) {
                    baiViet.setDanhSachHinhAnh(existingBaiViet.getDanhSachHinhAnh());
                } else {
                    // Xử lý upload hình ảnh bổ sung mới
                    java.util.List<String> hinhAnhUrls = new java.util.ArrayList<>();
                    for (MultipartFile file : hinhAnhFiles) {
                        if (!file.isEmpty()) {
                            String imageUrl = fileUploadService.uploadImage(file);
                            hinhAnhUrls.add(imageUrl);
                        }
                    }
                    if (!hinhAnhUrls.isEmpty()) {
                        baiViet.setDanhSachHinhAnh(hinhAnhUrls);
                    } else {
                        // Giữ lại hình ảnh cũ nếu không có hình mới
                        baiViet.setDanhSachHinhAnh(existingBaiViet.getDanhSachHinhAnh());
                    }
                }

                // Preserve video list
                if (baiViet.getDanhSachVideo() == null || baiViet.getDanhSachVideo().isEmpty()) {
                    baiViet.setDanhSachVideo(existingBaiViet.getDanhSachVideo());
                }
            } else {
                // Bài viết mới - xử lý upload bình thường
                if (anhDaiDienFile != null && !anhDaiDienFile.isEmpty()) {
                    String anhDaiDienUrl = fileUploadService.uploadImage(anhDaiDienFile);
                    baiViet.setAnhDaiDien(anhDaiDienUrl);
                }

                // Xử lý upload hình ảnh bổ sung
                if (hinhAnhFiles != null && hinhAnhFiles.length > 0) {
                    java.util.List<String> hinhAnhUrls = new java.util.ArrayList<>();
                    for (MultipartFile file : hinhAnhFiles) {
                        if (!file.isEmpty()) {
                            String imageUrl = fileUploadService.uploadImage(file);
                            hinhAnhUrls.add(imageUrl);
                        }
                    }
                    if (!hinhAnhUrls.isEmpty()) {
                        baiViet.setDanhSachHinhAnh(hinhAnhUrls);
                    }
                }
            }

            adminService.saveBaiViet(baiViet);
            redirectAttributes.addFlashAttribute("success", "Bài viết đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet";
    }

    @PostMapping("/bai-viet/delete/{id}")
    public String deleteBaiViet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.softDeleteBaiViet(id);
            redirectAttributes.addFlashAttribute("success", "Bài viết đã được chuyển vào thùng rác!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa bài viết: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet";
    }

    @GetMapping("/bai-viet/view/{id}")
    public String viewBaiViet(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Bài Viết");
        model.addAttribute("pageTitle", "Chi Tiết Bài Viết");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("baiViet", adminService.getBaiVietById(id));
        return "admin/bai-viet/view";
    }

    // ==================== THÙNG RÁC BÀI VIẾT ====================

    @GetMapping("/bai-viet/trash")
    public String baiVietTrash(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        try {
            Page<com.branch.demo.domain.BaiViet> trashedPage = adminService.getTrashedBaiViet(page, search, fromDate,
                    toDate);

            model.addAttribute("title", "Thùng Rác Bài Viết");
            model.addAttribute("pageTitle", "Thùng Rác Bài Viết");
            model.addAttribute("activeMenu", "bai-viet");
            model.addAttribute("baiVietPage", trashedPage);
            model.addAttribute("search", search);
            model.addAttribute("fromDate", fromDate);
            model.addAttribute("toDate", toDate);

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải thùng rác: " + e.getMessage());
        }

        return "admin/bai-viet/trash";
    }

    @PostMapping("/bai-viet/soft-delete/{id}")
    public String softDeleteBaiViet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.softDeleteBaiViet(id);
            redirectAttributes.addFlashAttribute("success", "Đã chuyển bài viết vào thùng rác");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa bài viết: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet";
    }

    @PostMapping("/bai-viet/restore/{id}")
    public String restoreBaiViet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.restoreBaiViet(id);
            redirectAttributes.addFlashAttribute("success", "Đã khôi phục bài viết thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể khôi phục bài viết: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet/trash";
    }

    @PostMapping("/bai-viet/permanent-delete/{id}")
    public String permanentDeleteBaiViet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.permanentDeleteBaiViet(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa vĩnh viễn bài viết");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa vĩnh viễn bài viết: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet/trash";
    }

    @PostMapping("/bai-viet/empty-trash")
    public String emptyTrash(RedirectAttributes redirectAttributes) {
        try {
            int deletedCount = adminService.emptyBaiVietTrash();
            redirectAttributes.addFlashAttribute("success",
                    "Đã xóa vĩnh viễn " + deletedCount + " bài viết khỏi thùng rác");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể làm trống thùng rác: " + e.getMessage());
        }
        return "redirect:/admin/bai-viet/trash";
    }

    // ==================== DANH MỤC MANAGEMENT ====================

    @GetMapping("/danh-muc")
    public String danhMucList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String trangThai,
            Model model) {

        try {
            Page<com.branch.demo.domain.DanhMuc> danhMucPage = adminService.getDanhMucPage(page, search, trangThai);

            model.addAttribute("title", "Quản Lý Danh Mục");
            model.addAttribute("pageTitle", "Quản Lý Danh Mục");
            model.addAttribute("activeMenu", "bai-viet");
            model.addAttribute("danhMucPage", danhMucPage);
            model.addAttribute("search", search);
            model.addAttribute("trangThai", trangThai);

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách danh mục: " + e.getMessage());
        }

        return "admin/danh-muc/list";
    }

    @GetMapping("/danh-muc/new")
    public String newDanhMuc(Model model) {
        model.addAttribute("title", "Thêm Danh Mục");
        model.addAttribute("pageTitle", "Thêm Danh Mục Mới");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("danhMuc", new com.branch.demo.domain.DanhMuc());
        return "admin/danh-muc/form";
    }

    @GetMapping("/danh-muc/edit/{id}")
    public String editDanhMuc(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Danh Mục");
        model.addAttribute("pageTitle", "Chỉnh Sửa Danh Mục");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("danhMuc", adminService.getDanhMucById(id));
        return "admin/danh-muc/form";
    }

    @PostMapping("/danh-muc/save")
    public String saveDanhMuc(@ModelAttribute com.branch.demo.domain.DanhMuc danhMuc,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveDanhMuc(danhMuc);
            redirectAttributes.addFlashAttribute("success",
                    danhMuc.getId() != null ? "Cập nhật danh mục thành công!" : "Thêm danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/danh-muc";
    }

    @PostMapping("/danh-muc/delete/{id}")
    public String deleteDanhMuc(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteDanhMuc(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/danh-muc";
    }

    @GetMapping("/danh-muc/view/{id}")
    public String viewDanhMuc(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Danh Mục");
        model.addAttribute("pageTitle", "Chi Tiết Danh Mục");
        model.addAttribute("activeMenu", "bai-viet");
        model.addAttribute("danhMuc", adminService.getDanhMucById(id));
        return "admin/danh-muc/view";
    }

    @GetMapping("/api/check-slug-duplicate")
    @ResponseBody
    public java.util.Map<String, Object> checkSlugDuplicate(
            @RequestParam String slug,
            @RequestParam(required = false) String currentId) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        try {
            boolean isDuplicate = false;
            if (currentId == null || currentId.trim().isEmpty()) {
                // New article
                isDuplicate = baiVietRepository.existsBySlug(slug);
            } else {
                // Edit existing article
                Long id = Long.valueOf(currentId);
                isDuplicate = baiVietRepository.existsBySlugAndIdNot(slug, id);
            }

            result.put("isDuplicate", isDuplicate);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    // ==================== ACCOUNT MANAGEMENT ====================

    @GetMapping("/account")
    public String accountList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            Model model) {
        model.addAttribute("title", "Quản Lý Tài Khoản");
        model.addAttribute("pageTitle", "Danh Sách Tài Khoản");
        model.addAttribute("activeMenu", "account");
        model.addAttribute("accountPage", adminService.getAccountPage(page, search));
        model.addAttribute("search", search);
        return "admin/account/list";
    }

    @GetMapping("/account/new")
    public String newAccount(Model model) {
        model.addAttribute("title", "Thêm Tài Khoản");
        model.addAttribute("pageTitle", "Thêm Tài Khoản Mới");
        model.addAttribute("activeMenu", "account");
        model.addAttribute("account", new Account());
        return "admin/account/form";
    }

    @GetMapping("/account/edit/{id}")
    public String editAccount(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Tài Khoản");
        model.addAttribute("pageTitle", "Chỉnh Sửa Tài Khoản");
        model.addAttribute("activeMenu", "account");
        model.addAttribute("account", adminService.getAccountById(id));
        return "admin/account/form";
    }

    @PostMapping("/account/save")
    public String saveAccount(@ModelAttribute Account account,
            @RequestParam(value = "nhanSu.id", required = false) Long nhanSuId,
            @RequestParam(value = "chapSu.id", required = false) Long chapSuId,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Set up model attributes for potential error return
        String title = account.getId() != null ? "Chỉnh Sửa Tài Khoản" : "Thêm Tài Khoản";
        String pageTitle = account.getId() != null ? "Chỉnh Sửa Tài Khoản" : "Thêm Tài Khoản Mới";

        try {
            // Debug: Print received data
            System.out.println("Received account data:");
            System.out.println("ID: " + account.getId());
            System.out.println("Username: " + account.getUsername());
            System.out.println("Email: " + account.getEmail());
            System.out.println("FullName: " + account.getFullName());
            System.out.println("Role: " + account.getRole());
            System.out.println("Status: " + account.getStatus());

            // Check if role and status are null
            if (account.getRole() == null) {
                System.out.println("WARNING: Role is null!");
                account.setRole(Account.Role.ADMIN); // Set default
            }
            if (account.getStatus() == null) {
                System.out.println("WARNING: Status is null!");
                account.setStatus(Account.AccountStatus.ACTIVE); // Set default
            }

            // Xử lý logic nhân sự và chấp sự trước khi validation
            if (account.getNhanSu() != null && account.getNhanSu().getId() != null && account.getNhanSu().getId() > 0) {
                try {
                    account.setNhanSu(adminService.getNhanSuById(account.getNhanSu().getId()));
                } catch (Exception e) {
                    account.setNhanSu(null);
                }
            } else {
                account.setNhanSu(null);
            }
            
            if (account.getChapSu() != null && account.getChapSu().getId() != null && account.getChapSu().getId() > 0) {
                try {
                    account.setChapSu(adminService.getChapSuById(account.getChapSu().getId()));
                } catch (Exception e) {
                    account.setChapSu(null);
                }
            } else {
                account.setChapSu(null);
            }
            
            // Đảm bảo chỉ chọn một trong hai
            if (account.getNhanSu() != null && account.getChapSu() != null) {
                account.setChapSu(null);
            }

            // Determine validation group based on profile link
            boolean hasProfileLink = account.getNhanSu() != null || account.getChapSu() != null;
            Class<?> validationGroup = hasProfileLink ? 
                com.branch.demo.validation.ValidationGroups.WithProfileLink.class : 
                com.branch.demo.validation.ValidationGroups.WithoutProfileLink.class;

            // Perform validation using appropriate group
            jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            java.util.Set<jakarta.validation.ConstraintViolation<Account>> violations = validator.validate(account, validationGroup);

            if (!violations.isEmpty()) {
                StringBuilder errorMsg = new StringBuilder();
                for (jakarta.validation.ConstraintViolation<Account> violation : violations) {
                    errorMsg.append(violation.getMessage()).append(" ");
                }
                
                model.addAttribute("account", account);
                model.addAttribute("title", title);
                model.addAttribute("pageTitle", pageTitle);
                model.addAttribute("activeMenu", "account");
                model.addAttribute("error", errorMsg.toString());
                return "admin/account/form";
            }

            // Validate duplicate username/email for new accounts
            if (account.getId() == null) {
                if (adminService.isUsernameExists(account.getUsername())) {
                    model.addAttribute("account", account);
                    model.addAttribute("title", title);
                    model.addAttribute("pageTitle", pageTitle);
                    model.addAttribute("activeMenu", "account");
                    model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                    return "admin/account/form";
                }

                if (account.getEmail() != null && !account.getEmail().trim().isEmpty() && 
                    adminService.isEmailExists(account.getEmail())) {
                    model.addAttribute("account", account);
                    model.addAttribute("title", title);
                    model.addAttribute("pageTitle", pageTitle);
                    model.addAttribute("activeMenu", "account");
                    model.addAttribute("error", "Email đã tồn tại!");
                    return "admin/account/form";
                }
            } else {
                // For updates, check email uniqueness excluding current account
                if (account.getEmail() != null && !account.getEmail().trim().isEmpty() && 
                    adminService.isEmailExistsExcludingId(account.getEmail(), account.getId())) {
                    model.addAttribute("account", account);
                    model.addAttribute("title", title);
                    model.addAttribute("pageTitle", pageTitle);
                    model.addAttribute("activeMenu", "account");
                    model.addAttribute("error", "Email đã tồn tại!");
                    return "admin/account/form";
                }
            }


            String generatedPassword = adminService.saveAccount(account, null);

            if (generatedPassword != null) {
                redirectAttributes.addFlashAttribute("success",
                        "Tài khoản đã được tạo thành công! Mật khẩu được tạo tự động: " + generatedPassword);
            } else {
                redirectAttributes.addFlashAttribute("success", "Tài khoản đã được cập nhật thành công!");
            }
        } catch (Exception e) {
            model.addAttribute("account", account);
            model.addAttribute("title", title);
            model.addAttribute("pageTitle", pageTitle);
            model.addAttribute("activeMenu", "account");
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "admin/account/form";
        }
        return "redirect:/admin/account";
    }

    @PostMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteAccount(id);
            redirectAttributes.addFlashAttribute("success", "Tài khoản đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    @PostMapping("/account/toggle-status/{id}")
    public String toggleAccountStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleAccountStatus(id);
            redirectAttributes.addFlashAttribute("success", "Trạng thái tài khoản đã được cập nhật!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    @PostMapping("/account/reset-password/{id}")
    public String resetPassword(@PathVariable Long id,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (newPassword == null || newPassword.trim().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
                return "redirect:/admin/account";
            }

            adminService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("success", "Mật khẩu đã được reset thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể reset mật khẩu: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    @GetMapping("/account/view/{id}")
    public String viewAccount(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Tài Khoản");
        model.addAttribute("pageTitle", "Chi Tiết Tài Khoản");
        model.addAttribute("activeMenu", "account");
        model.addAttribute("account", adminService.getAccountById(id));
        return "admin/account/view";
    }

    // ==================== API ENDPOINTS FOR BAN NGANH ====================

    // @GetMapping("/api/all-nhan-su")
    // @ResponseBody
    // public java.util.List<com.branch.demo.domain.NhanSu> getAllNhanSu() {
    //     return adminService.getAllActiveNhanSu();
    // }

    // @GetMapping("/api/all-chap-su")
    // @ResponseBody
    // public java.util.List<com.branch.demo.domain.ChapSu> getAllChapSu() {
    //     return adminService.getAllActiveChapSu();
    // }

    // @GetMapping("/api/ban-nganh/{id}/nhan-su")
    // @ResponseBody
    // public java.util.List<java.util.Map<String, Object>> getBanNganhNhanSu(@PathVariable Long id) {
    //     java.util.List<com.branch.demo.domain.NhanSu> nhanSuList = adminService.getNhanSuByBanNganhId(id);
    //     return nhanSuList.stream()
    //             .map(nhanSu -> {
    //                 java.util.Map<String, Object> map = new java.util.HashMap<>();
    //                 map.put("id", nhanSu.getId());
    //                 map.put("hoTen", nhanSu.getHoTen());
    //                 map.put("chucVu", nhanSu.getChucVu() != null ? nhanSu.getChucVu().getDisplayName() : "");
    //                 map.put("email", nhanSu.getEmail());
    //                 map.put("dienThoai", nhanSu.getDienThoai());
    //                 map.put("avatarUrl", nhanSu.getAvatarUrl());
    //                 map.put("trangThai", nhanSu.getTrangThai().name());
    //                 map.put("diemNhomTen", nhanSu.getDiemNhom() != null ? nhanSu.getDiemNhom().getTenDiemNhom() : null);
    //                 return map;
    //             })
    //             .collect(java.util.stream.Collectors.toList());
    // }

    @GetMapping("/api/ban-nganh/{id}/chap-su")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getBanNganhChapSu(@PathVariable Long id) {
        java.util.List<com.branch.demo.domain.ChapSu> chapSuList = adminService.getChapSuByBanNganhId(id);
        return chapSuList.stream()
                .map(chapSu -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", chapSu.getId());
                    map.put("hoTen", chapSu.getHoTen());
                    map.put("chucVu", chapSu.getChucVu() != null ? chapSu.getChucVu().getDisplayName() : "");
                    map.put("email", chapSu.getEmail());
                    map.put("dienThoai", chapSu.getDienThoai());
                    map.put("avatarUrl", chapSu.getAvatarUrl());
                    map.put("trangThai", chapSu.getTrangThai().name());
                    map.put("diemNhomTen", chapSu.getDiemNhom() != null ? chapSu.getDiemNhom().getTenDiemNhom() : null);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    // ==================== API ENDPOINTS FOR DIEM NHOM ====================
    
    /**
     * API endpoint to get NhanSu list by DiemNhom ID
     */
    @GetMapping("/api/diem-nhom/{diemNhomId}/nhan-su")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getNhanSuByDiemNhom(@PathVariable Long diemNhomId) {
        return nhanSuRepository.findWithAdvancedFilters(null, null, null, diemNhomId, null, null, 
                org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream()
                .map(nhanSu -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", nhanSu.getId());
                    map.put("hoTen", nhanSu.getHoTen());
                    map.put("chucVu", nhanSu.getChucVu() != null ? nhanSu.getChucVu().getDisplayName() : "");
                    map.put("email", nhanSu.getEmail());
                    map.put("soDienThoai", nhanSu.getDienThoai());
                    map.put("avatarUrl", nhanSu.getAvatarUrl());
                    map.put("trangThai", nhanSu.getTrangThai().name());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * API endpoint to get ChapSu list by DiemNhom ID
     */
    @GetMapping("/api/diem-nhom/{diemNhomId}/chap-su")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getChapSuByDiemNhom(@PathVariable Long diemNhomId) {
        return chapSuRepository.findWithAdvancedFilters(null, null, null, diemNhomId, null, null, 
                org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream()
                .map(chapSu -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", chapSu.getId());
                    map.put("hoTen", chapSu.getHoTen());
                    map.put("chucVu", chapSu.getChucVu() != null ? chapSu.getChucVu().getDisplayName() : "");
                    map.put("email", chapSu.getEmail());
                    map.put("soDienThoai", chapSu.getDienThoai());
                    map.put("avatarUrl", chapSu.getAvatarUrl());
                    map.put("trangThai", chapSu.getTrangThai().name());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
