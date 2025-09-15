package com.branch.demo.controller;

import com.branch.demo.domain.TaiChinhDanhMuc;
import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.TaiChinhNam;
import com.branch.demo.domain.NhanSu;
import com.branch.demo.dto.*;
import com.branch.demo.service.TaiChinhService;
import com.branch.demo.service.ExcelExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/tai-chinh")
public class TaiChinhController {

    @Autowired
    private TaiChinhService taiChinhService;

    @Autowired
    private ExcelExportService excelExportService;

    // ==================== DANH MỤC MANAGEMENT ====================

    @GetMapping("/danh-muc")
    public String danhMucList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String loai,
            Model model) {

        TaiChinhDanhMuc.LoaiDanhMuc loaiEnum = null;
        if (loai != null && !loai.isEmpty()) {
            try {
                loaiEnum = TaiChinhDanhMuc.LoaiDanhMuc.valueOf(loai);
            } catch (IllegalArgumentException e) {
                // Invalid loai, ignore
            }
        }

        Page<TaiChinhDanhMuc> danhMucPage = taiChinhService.getDanhMucPageWithFilters(page, search, loaiEnum);

        model.addAttribute("title", "Quản lý Danh mục Tài chính");
        model.addAttribute("pageTitle", "Danh mục Tài chính");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "danh-muc");

        model.addAttribute("danhMucPage", danhMucPage);
        model.addAttribute("search", search);
        model.addAttribute("loai", loai);
        model.addAttribute("currentPage", page);

        // Add enum values for filter
        model.addAttribute("loaiDanhMucValues", TaiChinhDanhMuc.LoaiDanhMuc.values());

        return "admin/tai-chinh/danh-muc/list";
    }

    @GetMapping("/danh-muc/add")
    public String danhMucAddForm(Model model) {
        model.addAttribute("title", "Thêm Danh mục Tài chính");
        model.addAttribute("pageTitle", "Thêm Danh mục");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "danh-muc");

        model.addAttribute("danhMucDTO", new TaiChinhDanhMucDTO());
        model.addAttribute("loaiDanhMucValues", TaiChinhDanhMuc.LoaiDanhMuc.values());
        model.addAttribute("isEdit", false);

        return "admin/tai-chinh/danh-muc/form";
    }

    @GetMapping("/danh-muc/edit/{id}")
    public String danhMucEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaiChinhDanhMuc danhMuc = taiChinhService.getDanhMucById(id);
            TaiChinhDanhMucDTO danhMucDTO = new TaiChinhDanhMucDTO(danhMuc);

            model.addAttribute("title", "Sửa Danh mục Tài chính");
            model.addAttribute("pageTitle", "Sửa Danh mục: " + danhMuc.getTenDanhMuc());
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "danh-muc");

            model.addAttribute("danhMucDTO", danhMucDTO);
            model.addAttribute("loaiDanhMucValues", TaiChinhDanhMuc.LoaiDanhMuc.values());
            model.addAttribute("isEdit", true);

            return "admin/tai-chinh/danh-muc/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục: " + e.getMessage());
            return "redirect:/admin/tai-chinh/danh-muc";
        }
    }

    @PostMapping("/danh-muc/save")
    public String danhMucSave(@Valid @ModelAttribute TaiChinhDanhMucDTO danhMucDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", danhMucDTO.getId() == null ? "Thêm Danh mục" : "Sửa Danh mục");
            model.addAttribute("pageTitle", danhMucDTO.getId() == null ? "Thêm Danh mục" : "Sửa Danh mục");
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "danh-muc");
            model.addAttribute("loaiDanhMucValues", TaiChinhDanhMuc.LoaiDanhMuc.values());
            model.addAttribute("isEdit", danhMucDTO.getId() != null);
            return "admin/tai-chinh/danh-muc/form";
        }

        try {
            TaiChinhDanhMuc danhMuc;
            if (danhMucDTO.getId() != null) {
                // Update existing
                danhMuc = taiChinhService.getDanhMucById(danhMucDTO.getId());
                danhMucDTO.updateEntity(danhMuc);
            } else {
                // Create new
                danhMuc = danhMucDTO.toEntity();
            }

            taiChinhService.saveDanhMuc(danhMuc);

            String message = danhMucDTO.getId() == null ? "Thêm danh mục thành công!" : "Cập nhật danh mục thành công!";
            redirectAttributes.addFlashAttribute("success", message);

            return "redirect:/admin/tai-chinh/danh-muc";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("title", danhMucDTO.getId() == null ? "Thêm Danh mục" : "Sửa Danh mục");
            model.addAttribute("pageTitle", danhMucDTO.getId() == null ? "Thêm Danh mục" : "Sửa Danh mục");
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "danh-muc");
            model.addAttribute("loaiDanhMucValues", TaiChinhDanhMuc.LoaiDanhMuc.values());
            model.addAttribute("isEdit", danhMucDTO.getId() != null);
            return "admin/tai-chinh/danh-muc/form";
        }
    }

    @GetMapping("/danh-muc/view/{id}")
    public String danhMucView(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaiChinhDanhMuc danhMuc = taiChinhService.getDanhMucById(id);
            TaiChinhDanhMucDTO danhMucDTO = new TaiChinhDanhMucDTO(danhMuc);

            // Check if being used
            boolean dangSuDung = taiChinhService.isDanhMucBeingUsed(id);
            danhMucDTO.setDangSuDung(dangSuDung);

            model.addAttribute("title", "Chi tiết Danh mục");
            model.addAttribute("pageTitle", "Chi tiết: " + danhMuc.getTenDanhMuc());
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "danh-muc");

            model.addAttribute("danhMucDTO", danhMucDTO);

            return "admin/tai-chinh/danh-muc/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục: " + e.getMessage());
            return "redirect:/admin/tai-chinh/danh-muc";
        }
    }

    @PostMapping("/danh-muc/delete/{id}")
    public String danhMucDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taiChinhService.deleteDanhMuc(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/tai-chinh/danh-muc";
    }

    // ==================== API ENDPOINTS FOR DROPDOWNS ====================

    @GetMapping("/api/danh-muc")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllDanhMuc() {
        List<TaiChinhDanhMuc> danhMucList = taiChinhService.getAllDanhMuc();
        List<Map<String, Object>> result = danhMucList.stream().map(dm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dm.getId());
            map.put("tenDanhMuc", dm.getTenDanhMuc());
            map.put("loai", dm.getLoai().name());
            map.put("loaiDisplayName", dm.getLoai().getDisplayName());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/danh-muc/loai/{loai}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDanhMucByLoai(@PathVariable String loai) {
        try {
            TaiChinhDanhMuc.LoaiDanhMuc loaiEnum = TaiChinhDanhMuc.LoaiDanhMuc.valueOf(loai.toUpperCase());
            List<TaiChinhDanhMuc> danhMucList = taiChinhService.getDanhMucByLoai(loaiEnum);

            List<Map<String, Object>> result = danhMucList.stream().map(dm -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", dm.getId());
                map.put("tenDanhMuc", dm.getTenDanhMuc());
                map.put("loai", dm.getLoai().name());
                map.put("loaiDisplayName", dm.getLoai().getDisplayName());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/nhan-su")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllNhanSu() {
        List<NhanSu> nhanSuList = taiChinhService.getActiveNhanSu();
        List<Map<String, Object>> result = nhanSuList.stream().map(ns -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ns.getId());
            map.put("hoTen", ns.getHoTen());
            map.put("chucVu", ns.getChucVu() != null ? ns.getChucVu().getDisplayName() : "");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/danh-muc/{id}/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDanhMucTransactionCount(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            long count = taiChinhService.countGiaoDichByDanhMuc(id);
            result.put("count", count);
            result.put("success", true);
        } catch (Exception e) {
            result.put("count", 0);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // ==================== GIAO DỊCH MANAGEMENT ====================

    @GetMapping("/giao-dich")
    public String giaoDichList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) Long nguoiPhuTrachId,
            @RequestParam(required = false) String tuNgay,
            @RequestParam(required = false) String denNgay,
            @RequestParam(required = false) BigDecimal soTienMin,
            @RequestParam(required = false) BigDecimal soTienMax,
            @RequestParam(required = false) Integer nam,
            Model model) {

        // Parse dates
        LocalDateTime tuNgayParsed = null;
        LocalDateTime denNgayParsed = null;

        try {
            if (tuNgay != null && !tuNgay.isEmpty()) {
                tuNgayParsed = LocalDateTime.parse(tuNgay + "T00:00:00");
            }
            if (denNgay != null && !denNgay.isEmpty()) {
                denNgayParsed = LocalDateTime.parse(denNgay + "T23:59:59");
            }
        } catch (Exception e) {
            // Invalid date format, ignore
        }

        // Parse loai
        TaiChinhGiaoDich.LoaiGiaoDich loaiEnum = null;
        if (loai != null && !loai.isEmpty()) {
            try {
                loaiEnum = TaiChinhGiaoDich.LoaiGiaoDich.valueOf(loai);
            } catch (IllegalArgumentException e) {
                // Invalid loai, ignore
            }
        }

        Page<TaiChinhGiaoDich> giaoDichPage = taiChinhService.getGiaoDichPageWithFilters(
                page, search, loaiEnum, danhMucId, nguoiPhuTrachId,
                tuNgayParsed, denNgayParsed, soTienMin, soTienMax);

        // Convert to DTOs
        Page<TaiChinhGiaoDichDTO> giaoDichDTOPage = giaoDichPage.map(TaiChinhGiaoDichDTO::new);

        model.addAttribute("title", "Quản lý Giao dịch Tài chính");
        model.addAttribute("pageTitle", "Giao dịch Tài chính");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "giao-dich");

        model.addAttribute("giaoDichPage", giaoDichDTOPage);
        model.addAttribute("search", search);
        model.addAttribute("loai", loai);
        model.addAttribute("danhMucId", danhMucId);
        model.addAttribute("nguoiPhuTrachId", nguoiPhuTrachId);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        model.addAttribute("soTienMin", soTienMin);
        model.addAttribute("soTienMax", soTienMax);
        model.addAttribute("nam", nam);
        model.addAttribute("currentPage", page);

        // Add data for filters
        model.addAttribute("loaiGiaoDichValues", TaiChinhGiaoDich.LoaiGiaoDich.values());
        model.addAttribute("danhMucList", taiChinhService.getAllDanhMuc());
        model.addAttribute("nhanSuList", taiChinhService.getActiveNhanSu());
        model.addAttribute("availableYears", taiChinhService.getAvailableYears());

        return "admin/tai-chinh/giao-dich/list";
    }

    @GetMapping("/giao-dich/export")
    public ResponseEntity<byte[]> exportGiaoDichToExcel(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) Long nhanSuId,
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) Integer thang) throws IOException {

        // Create search criteria
        GiaoDichSearchCriteria criteria = new GiaoDichSearchCriteria();
        criteria.setSearch(search);
        criteria.setLoai(loai);
        criteria.setDanhMucId(danhMucId);
        criteria.setNhanSuId(nhanSuId);
        criteria.setNam(nam);
        criteria.setThang(thang);

        // Get all transactions (no pagination for export)
        List<TaiChinhGiaoDich> giaoDichList = taiChinhService.getAllGiaoDichWithFilters(criteria);

        // Create title based on filters
        StringBuilder titleBuilder = new StringBuilder("Danh sách giao dịch tài chính");
        if (nam != null) {
            titleBuilder.append(" - Năm ").append(nam);
            if (thang != null) {
                titleBuilder.append(" - Tháng ").append(thang);
            }
        }
        if (loai != null && !loai.isEmpty()) {
            titleBuilder.append(" - Loại: ").append(loai.equals("THU") ? "Thu" : "Chi");
        }

        // Generate Excel file
        byte[] excelData = excelExportService.exportGiaoDichToExcel(giaoDichList, titleBuilder.toString());

        // Create filename with timestamp
        String filename = "giao-dich-tai-chinh-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) +
                ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @GetMapping("/giao-dich/add")
    public String giaoDichAddForm(Model model) {
        model.addAttribute("title", "Thêm Giao dịch Tài chính");
        model.addAttribute("pageTitle", "Thêm Giao dịch");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "giao-dich");

        TaiChinhGiaoDichDTO giaoDichDTO = new TaiChinhGiaoDichDTO();
        giaoDichDTO.setThoiGian(LocalDateTime.now());

        model.addAttribute("giaoDichDTO", giaoDichDTO);
        model.addAttribute("loaiGiaoDichValues", TaiChinhGiaoDich.LoaiGiaoDich.values());
        model.addAttribute("danhMucList", taiChinhService.getAllDanhMuc());
        model.addAttribute("nhanSuList", taiChinhService.getActiveNhanSu());
        model.addAttribute("isEdit", false);

        return "admin/tai-chinh/giao-dich/form";
    }

    @GetMapping("/giao-dich/edit/{id}")
    public String giaoDichEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaiChinhGiaoDich giaoDich = taiChinhService.getGiaoDichById(id);
            TaiChinhGiaoDichDTO giaoDichDTO = new TaiChinhGiaoDichDTO(giaoDich);

            model.addAttribute("title", "Sửa Giao dịch Tài chính");
            model.addAttribute("pageTitle", "Sửa Giao dịch #" + id);
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "giao-dich");

            model.addAttribute("giaoDichDTO", giaoDichDTO);
            model.addAttribute("loaiGiaoDichValues", TaiChinhGiaoDich.LoaiGiaoDich.values());
            model.addAttribute("danhMucList", taiChinhService.getAllDanhMuc());
            model.addAttribute("nhanSuList", taiChinhService.getActiveNhanSu());
            model.addAttribute("isEdit", true);

            return "admin/tai-chinh/giao-dich/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch: " + e.getMessage());
            return "redirect:/admin/tai-chinh/giao-dich";
        }
    }

    @PostMapping("/giao-dich/save")
    public String giaoDichSave(@Valid @ModelAttribute TaiChinhGiaoDichDTO giaoDichDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", giaoDichDTO.getId() == null ? "Thêm Giao dịch" : "Sửa Giao dịch");
            model.addAttribute("pageTitle", giaoDichDTO.getId() == null ? "Thêm Giao dịch" : "Sửa Giao dịch");
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "giao-dich");
            model.addAttribute("giaoDichDTO", giaoDichDTO);
            model.addAttribute("loaiGiaoDichValues", TaiChinhGiaoDich.LoaiGiaoDich.values());
            model.addAttribute("danhMucList", taiChinhService.getAllDanhMuc());
            model.addAttribute("nhanSuList", taiChinhService.getActiveNhanSu());
            model.addAttribute("isEdit", giaoDichDTO.getId() != null);
            return "admin/tai-chinh/giao-dich/form";
        }

        try {
            TaiChinhGiaoDich giaoDich;
            if (giaoDichDTO.getId() != null) {
                // Update existing
                giaoDich = taiChinhService.getGiaoDichById(giaoDichDTO.getId());
                giaoDichDTO.updateEntity(giaoDich);

                // Load related entities
                if (giaoDichDTO.getDanhMucId() != null) {
                    TaiChinhDanhMuc danhMuc = taiChinhService.getDanhMucById(giaoDichDTO.getDanhMucId());
                    giaoDich.setDanhMuc(danhMuc);
                }
                if (giaoDichDTO.getNguoiPhuTrachId() != null) {
                    // Load NhanSu - assuming we have access to it through service
                    List<NhanSu> nhanSuList = taiChinhService.getActiveNhanSu();
                    NhanSu nguoiPhuTrach = nhanSuList.stream()
                            .filter(ns -> ns.getId().equals(giaoDichDTO.getNguoiPhuTrachId()))
                            .findFirst().orElse(null);
                    giaoDich.setNguoiPhuTrach(nguoiPhuTrach);
                }

                taiChinhService.updateGiaoDich(giaoDich);
            } else {
                // Create new
                giaoDich = giaoDichDTO.toEntity();

                // Load related entities
                if (giaoDichDTO.getDanhMucId() != null) {
                    TaiChinhDanhMuc danhMuc = taiChinhService.getDanhMucById(giaoDichDTO.getDanhMucId());
                    giaoDich.setDanhMuc(danhMuc);
                }
                if (giaoDichDTO.getNguoiPhuTrachId() != null) {
                    List<NhanSu> nhanSuList = taiChinhService.getActiveNhanSu();
                    NhanSu nguoiPhuTrach = nhanSuList.stream()
                            .filter(ns -> ns.getId().equals(giaoDichDTO.getNguoiPhuTrachId()))
                            .findFirst().orElse(null);
                    giaoDich.setNguoiPhuTrach(nguoiPhuTrach);
                }

                taiChinhService.saveGiaoDich(giaoDich);
            }

            String message = giaoDichDTO.getId() == null ? "Thêm giao dịch thành công!"
                    : "Cập nhật giao dịch thành công!";
            redirectAttributes.addFlashAttribute("success", message);

            return "redirect:/admin/tai-chinh/giao-dich";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("title", giaoDichDTO.getId() == null ? "Thêm Giao dịch" : "Sửa Giao dịch");
            model.addAttribute("pageTitle", giaoDichDTO.getId() == null ? "Thêm Giao dịch" : "Sửa Giao dịch");
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "giao-dich");
            model.addAttribute("giaoDichDTO", giaoDichDTO);
            model.addAttribute("loaiGiaoDichValues", TaiChinhGiaoDich.LoaiGiaoDich.values());
            model.addAttribute("danhMucList", taiChinhService.getAllDanhMuc());
            model.addAttribute("nhanSuList", taiChinhService.getActiveNhanSu());
            model.addAttribute("isEdit", giaoDichDTO.getId() != null);
            return "admin/tai-chinh/giao-dich/form";
        }
    }

    @GetMapping("/giao-dich/view/{id}")
    public String giaoDichView(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaiChinhGiaoDich giaoDich = taiChinhService.getGiaoDichById(id);
            TaiChinhGiaoDichDTO giaoDichDTO = new TaiChinhGiaoDichDTO(giaoDich);

            model.addAttribute("title", "Chi tiết Giao dịch");
            model.addAttribute("pageTitle", "Chi tiết Giao dịch #" + id);
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "giao-dich");

            model.addAttribute("giaoDichDTO", giaoDichDTO);

            return "admin/tai-chinh/giao-dich/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giao dịch: " + e.getMessage());
            return "redirect:/admin/tai-chinh/giao-dich";
        }
    }

    @PostMapping("/giao-dich/delete/{id}")
    public String giaoDichDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taiChinhService.deleteGiaoDich(id);
            redirectAttributes.addFlashAttribute("success", "Xóa giao dịch thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa giao dịch: " + e.getMessage());
        }
        return "redirect:/admin/tai-chinh/giao-dich";
    }

    // ==================== THỐNG KÊ VÀ BÁO CÁO ====================

    @GetMapping("/thong-ke")
    public String thongKeDashboard(Model model) {
        model.addAttribute("title", "Thống kê Tài chính");
        model.addAttribute("pageTitle", "Dashboard Tài chính");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "thong-ke");

        // Get available years
        List<Integer> availableYears = taiChinhService.getAvailableYears();
        model.addAttribute("availableYears", availableYears);

        // Get current year statistics
        Integer currentYear = LocalDateTime.now().getYear();
        if (availableYears.contains(currentYear)) {
            TaiChinhNam currentYearData = taiChinhService.getNamById(currentYear);
            model.addAttribute("currentYearData", currentYearData);
        }

        // Get overall statistics
        List<Object[]> thongKeTongQuan = taiChinhService.getThongKeTongQuan();
        model.addAttribute("thongKeTongQuan", thongKeTongQuan);

        // Get recent years data
        List<TaiChinhNam> recentYears = taiChinhService.getNamWithData();
        if (recentYears.size() > 5) {
            recentYears = recentYears.subList(0, 5);
        }
        model.addAttribute("recentYears", recentYears);

        return "admin/tai-chinh/thong-ke/dashboard";
    }

    @GetMapping("/thong-ke/nam/{nam}")
    public String thongKeNam(@PathVariable Integer nam, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Get year data
            TaiChinhNam namData = taiChinhService.getNamById(nam);

            // Get monthly statistics
            List<Object[]> thongKeThang = taiChinhService.getThongKeTheoThang(nam);
            System.out.println("DEBUG - Raw monthly data size: " + thongKeThang.size());
            for (Object[] row : thongKeThang) {
                System.out.println("DEBUG - Monthly row: " + Arrays.toString(row));
            }

            // Normalize monthly data to 12 months array
            List<List<Object>> normalizedMonthlyData = normalizeMonthlyDataAsList(thongKeThang);
            System.out.println("DEBUG - Normalized monthly data size: " + normalizedMonthlyData.size());
            for (int i = 0; i < Math.min(3, normalizedMonthlyData.size()); i++) {
                System.out.println("DEBUG - Normalized month " + (i + 1) + ": " + normalizedMonthlyData.get(i));
            }
            // Check month 9 specifically
            if (normalizedMonthlyData.size() >= 9) {
                System.out.println("DEBUG - Normalized month 9: " + normalizedMonthlyData.get(8));
            }

            // Get category statistics
            List<Object[]> thongKeDanhMuc = taiChinhService.getThongKeTheoDanhMuc(nam);
            System.out.println("DEBUG - Category data size: " + thongKeDanhMuc.size());
            for (Object[] row : thongKeDanhMuc) {
                System.out.println("DEBUG - Category row: " + Arrays.toString(row));
            }

            // Get top transactions
            Page<TaiChinhGiaoDich> topThu = taiChinhService.getTopGiaoDichThu(5);
            Page<TaiChinhGiaoDich> topChi = taiChinhService.getTopGiaoDichChi(5);

            model.addAttribute("title", "Thống kê năm " + nam);
            model.addAttribute("pageTitle", "Thống kê Tài chính năm " + nam);
            model.addAttribute("activeMenu", "tai-chinh");
            model.addAttribute("activeSubMenu", "thong-ke");

            model.addAttribute("nam", nam);
            model.addAttribute("thongKeNam", namData);
            model.addAttribute("thongKeTheoThang", normalizedMonthlyData);
            model.addAttribute("thongKeTheoDanhMuc", thongKeDanhMuc);
            model.addAttribute("topThu", topThu.getContent());
            model.addAttribute("topChi", topChi.getContent());

            // Add transaction counts
            model.addAttribute("soGiaoDichThu", taiChinhService.getSoGiaoDichThuByNam(nam));
            model.addAttribute("soGiaoDichChi", taiChinhService.getSoGiaoDichChiByNam(nam));
            model.addAttribute("totalGiaoDich", taiChinhService.getTotalGiaoDichByNam(nam));

            // Get available years for navigation
            model.addAttribute("availableYears", taiChinhService.getAvailableYears());

            return "admin/tai-chinh/thong-ke/nam";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải thống kê: " + e.getMessage());
            return "redirect:/admin/tai-chinh/thong-ke";
        }
    }

    @GetMapping("/thong-ke/so-sanh")
    public String thongKeSoSanh(@RequestParam(required = false) Integer nam1,
            @RequestParam(required = false) Integer nam2,
            Model model) {

        model.addAttribute("title", "So sánh Tài chính");
        model.addAttribute("pageTitle", "So sánh Tài chính giữa các năm");
        model.addAttribute("activeMenu", "tai-chinh");
        model.addAttribute("activeSubMenu", "thong-ke");

        List<Integer> availableYears = taiChinhService.getAvailableYears();
        model.addAttribute("availableYears", availableYears);

        if (nam1 != null && nam2 != null) {
            try {
                TaiChinhNam data1 = taiChinhService.getNamById(nam1);
                TaiChinhNam data2 = taiChinhService.getNamById(nam2);

                model.addAttribute("nam1", nam1);
                model.addAttribute("nam2", nam2);
                model.addAttribute("data1", data1);
                model.addAttribute("data2", data2);

                // Calculate differences
                BigDecimal diffThu = data1.getTongThu().subtract(data2.getTongThu());
                BigDecimal diffChi = data1.getTongChi().subtract(data2.getTongChi());
                BigDecimal diffSoDu = data1.getSoDu().subtract(data2.getSoDu());

                model.addAttribute("diffThu", diffThu);
                model.addAttribute("diffChi", diffChi);
                model.addAttribute("diffSoDu", diffSoDu);

                // Get detailed statistics for both years
                List<Object[]> thongKeDanhMuc1 = taiChinhService.getThongKeTheoDanhMuc(nam1);
                List<Object[]> thongKeDanhMuc2 = taiChinhService.getThongKeTheoDanhMuc(nam2);

                model.addAttribute("thongKeDanhMuc1", thongKeDanhMuc1);
                model.addAttribute("thongKeDanhMuc2", thongKeDanhMuc2);

            } catch (Exception e) {
                model.addAttribute("error", "Lỗi khi so sánh dữ liệu: " + e.getMessage());
            }
        }

        return "admin/tai-chinh/thong-ke/so-sanh";
    }

    // ==================== API ENDPOINTS FOR CHARTS ====================

    @GetMapping("/api/thong-ke/thang/{nam}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getThongKeThangAPI(@PathVariable Integer nam) {
        try {
            List<Object[]> thongKeThang = taiChinhService.getThongKeTheoThang(nam);

            List<Map<String, Object>> result = thongKeThang.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("thang", row[0]);
                map.put("tongThu", row[1]);
                map.put("tongChi", row[2]);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/thong-ke/danh-muc/{nam}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getThongKeDanhMucAPI(@PathVariable Integer nam) {
        try {
            List<Object[]> thongKeDanhMuc = taiChinhService.getThongKeTheoDanhMuc(nam);

            List<Map<String, Object>> result = thongKeDanhMuc.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("tenDanhMuc", row[0]);
                map.put("loai", row[1]);
                map.put("tongTien", row[2]);
                map.put("soGiaoDich", row[3]);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/debug/thong-ke/{nam}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> debugThongKe(@PathVariable Integer nam) {
        try {
            Map<String, Object> debug = new HashMap<>();

            // Check total transactions first
            List<TaiChinhGiaoDich> allTransactions = taiChinhService.getGiaoDichByNam(nam);
            debug.put("totalTransactions", allTransactions.size());

            // Raw monthly data
            List<Object[]> rawMonthly = taiChinhService.getThongKeTheoThang(nam);
            debug.put("rawMonthlyData", rawMonthly);
            debug.put("rawMonthlySize", rawMonthly.size());

            // Normalized monthly data
            Object[][] normalizedMonthly = normalizeMonthlyData(rawMonthly);
            debug.put("normalizedMonthlyData", normalizedMonthly);

            // Category data
            List<Object[]> categoryData = taiChinhService.getThongKeTheoDanhMuc(nam);
            debug.put("categoryData", categoryData);
            debug.put("categoryDataSize", categoryData.size());

            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/thong-ke/tong-quan")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getThongKeTongQuanAPI() {
        try {
            Map<String, Object> result = new HashMap<>();

            // Overall statistics
            BigDecimal totalThu = taiChinhService.getTotalThuAllTime();
            BigDecimal totalChi = taiChinhService.getTotalChiAllTime();
            BigDecimal totalSoDu = taiChinhService.getTotalSoDuAllTime();
            long totalTransactions = taiChinhService.getTotalTransactionCount();

            result.put("totalThu", totalThu);
            result.put("totalChi", totalChi);
            result.put("totalSoDu", totalSoDu);
            result.put("totalTransactions", totalTransactions);

            // Recent years data
            List<TaiChinhNam> recentYears = taiChinhService.getNamWithData();
            if (recentYears.size() > 5) {
                recentYears = recentYears.subList(0, 5);
            }

            List<Map<String, Object>> yearsData = recentYears.stream().map(year -> {
                Map<String, Object> yearMap = new HashMap<>();
                yearMap.put("nam", year.getNam());
                yearMap.put("tongThu", year.getTongThu());
                yearMap.put("tongChi", year.getTongChi());
                yearMap.put("soDu", year.getSoDu());
                return yearMap;
            }).collect(Collectors.toList());

            result.put("recentYears", yearsData);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Normalize monthly data to 12-month array
     * Input: List of [month, thu, chi] where month can be 1-12
     * Output: Array[12][3] where index 0 = January, index 11 = December
     */
    private Object[][] normalizeMonthlyData(List<Object[]> monthlyData) {
        Object[][] normalized = new Object[12][3];

        // Initialize all months with 0 values
        for (int i = 0; i < 12; i++) {
            normalized[i][0] = i + 1; // Month number (1-12)
            normalized[i][1] = 0; // Thu
            normalized[i][2] = 0; // Chi
        }

        // Fill in actual data
        for (Object[] row : monthlyData) {
            Integer month = (Integer) row[0];
            if (month >= 1 && month <= 12) {
                normalized[month - 1][0] = month;
                normalized[month - 1][1] = row[1]; // Thu
                normalized[month - 1][2] = row[2]; // Chi
            }
        }

        return normalized;
    }

    /**
     * Normalize monthly data to List<List<Object>> for Thymeleaf compatibility
     */
    private List<List<Object>> normalizeMonthlyDataAsList(List<Object[]> monthlyData) {
        List<List<Object>> normalized = new ArrayList<>();

        // Initialize all 12 months with 0 values
        for (int i = 0; i < 12; i++) {
            List<Object> month = new ArrayList<>();
            month.add(i + 1); // Month number (1-12)
            month.add(0); // Thu
            month.add(0); // Chi
            normalized.add(month);
        }

        // Fill in actual data
        for (Object[] row : monthlyData) {
            Integer month = (Integer) row[0];
            if (month >= 1 && month <= 12) {
                normalized.get(month - 1).set(0, month);
                // Convert BigDecimal to Double for JavaScript compatibility
                normalized.get(month - 1).set(1, ((BigDecimal) row[1]).doubleValue()); // Thu
                normalized.get(month - 1).set(2, ((BigDecimal) row[2]).doubleValue()); // Chi
            }
        }

        return normalized;
    }
}