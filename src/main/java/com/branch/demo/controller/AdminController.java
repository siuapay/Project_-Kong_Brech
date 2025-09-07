package com.branch.demo.controller;

import com.branch.demo.service.AdminService;
import com.branch.demo.service.FileUploadService;
import com.branch.demo.dto.SuKienDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("pageTitle", "Tổng Quan Hệ Thống");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
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
        return "admin/tin-huu/form";
    }

    @GetMapping("/tin-huu/edit/{id}")
    public String editTinHuu(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Tin Hữu");
        model.addAttribute("pageTitle", "Chỉnh Sửa Tin Hữu");
        model.addAttribute("activeMenu", "tin-huu");
        model.addAttribute("tinHuu", adminService.getTinHuuById(id));
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
        return "admin/ban-nganh/form";
    }

    @GetMapping("/ban-nganh/edit/{id}")
    public String editBanNganh(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chỉnh Sửa Ban Ngành");
        model.addAttribute("pageTitle", "Chỉnh Sửa Ban Ngành");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganh", adminService.getBanNganhById(id));
        return "admin/ban-nganh/form";
    }

    @PostMapping("/ban-nganh/save")
    public String saveBanNganh(@ModelAttribute com.branch.demo.domain.BanNganh banNganh,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveBanNganh(banNganh);
            redirectAttributes.addFlashAttribute("success", "Ban ngành đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/ban-nganh";
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
        model.addAttribute("title", "Chi Tiết Ban Ngành");
        model.addAttribute("pageTitle", "Chi Tiết Ban Ngành");
        model.addAttribute("activeMenu", "ban-nganh");
        model.addAttribute("banNganh", adminService.getBanNganhById(id));
        return "admin/ban-nganh/view";
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

    @PostMapping("/nhan-su/delete/{id}")
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

    // API endpoint để lấy tất cả ban ngành
    @GetMapping("/api/all-ban-nganh")
    @ResponseBody
    public java.util.List<com.branch.demo.dto.BanNganhDTO> getAllBanNganh() {
        return adminService.getAllActiveBanNganhDTO();
    }

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
            Model model) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 10);

        Page<com.branch.demo.domain.SuKien> suKienPage = adminService.getDeletedSuKienPage(page, search);

        model.addAttribute("title", "Thùng Rác Sự Kiện");
        model.addAttribute("pageTitle", "Thùng Rác Sự Kiện");
        model.addAttribute("activeMenu", "su-kien");
        model.addAttribute("suKienPage", suKienPage);
        model.addAttribute("search", search);

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
            RedirectAttributes redirectAttributes) {
        try {
            adminService.saveDiemNhom(diemNhom);
            redirectAttributes.addFlashAttribute("success", "Điểm nhóm đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/diem-nhom";
    }

    @PostMapping("/diem-nhom/delete/{id}")
    public String deleteDiemNhom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteDiemNhom(id);
            redirectAttributes.addFlashAttribute("success", "Điểm nhóm đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa điểm nhóm: " + e.getMessage());
        }
        return "redirect:/admin/diem-nhom";
    }

    @GetMapping("/diem-nhom/view/{id}")
    public String viewDiemNhom(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Chi Tiết Điểm Nhóm");
        model.addAttribute("pageTitle", "Chi Tiết Điểm Nhóm");
        model.addAttribute("activeMenu", "diem-nhom");
        model.addAttribute("diemNhom", adminService.getDiemNhomById(id));
        return "admin/diem-nhom/view";
    }

    @GetMapping("/tai-chinh")
    public String taiChinhManagement(Model model) {
        model.addAttribute("title", "Quản Lý Tài Chính");
        model.addAttribute("pageTitle", "Quản Lý Tài Chính");
        model.addAttribute("activeMenu", "tai-chinh");
        return "admin/tai-chinh";
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
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        org.springframework.data.domain.Page<com.branch.demo.domain.SuKien> suKienPage = 
            adminService.searchSuKien(search, loaiSuKienId, trangThai, fromDate, toDate, pageable);
        
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
    
    @GetMapping("/api/all-loai-su-kien")
    @ResponseBody
    public java.util.List<com.branch.demo.domain.LoaiSuKien> getAllLoaiSuKien() {
        return adminService.getAllActiveLoaiSuKien();
    }

}
