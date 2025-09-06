// package com.branch.demo.controller;

// import com.branch.demo.domain.SuKien;
// import com.branch.demo.domain.LoaiSuKien;
// import com.branch.demo.dto.SuKienDTO;
// import com.branch.demo.repository.SuKienRepository;
// import com.branch.demo.repository.LoaiSuKienRepository;
// import com.branch.demo.service.FileUploadService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import jakarta.validation.Valid;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// @Controller
// @RequestMapping("/admin/su-kien")
// public class SuKienController {

//     @Autowired
//     private SuKienRepository suKienRepository;

//     @Autowired
//     private LoaiSuKienRepository loaiSuKienRepository;

//     @Autowired
//     private FileUploadService fileUploadService;

//     @GetMapping
//     public String list(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(required = false) String search,
//             @RequestParam(required = false) Long loaiSuKienId,
//             @RequestParam(required = false) SuKien.TrangThaiSuKien trangThai,
//             @RequestParam(required = false) String fromDate,
//             @RequestParam(required = false) String toDate,
//             Model model) {

//         Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

//         LocalDate fromLocalDate = null;
//         LocalDate toLocalDate = null;

//         try {
//             if (fromDate != null && !fromDate.isEmpty()) {
//                 fromLocalDate = LocalDate.parse(fromDate);
//             }
//             if (toDate != null && !toDate.isEmpty()) {
//                 toLocalDate = LocalDate.parse(toDate);
//             }
//         } catch (Exception e) {
//             // Invalid date format, ignore
//         }

//         Page<SuKien> suKienPage = suKienRepository.searchSuKien(
//                 search, loaiSuKienId, trangThai, fromLocalDate, toLocalDate, pageable);

//         // Load danh sách loại sự kiện cho filter
//         List<LoaiSuKien> danhSachLoaiSuKien = loaiSuKienRepository
//                 .findByDeletedFalseAndKichHoatTrueOrderByThuTuAscTenLoaiAsc();

//         model.addAttribute("suKienPage", suKienPage);
//         model.addAttribute("danhSachLoaiSuKien", danhSachLoaiSuKien);
//         model.addAttribute("danhSachTrangThai", SuKien.TrangThaiSuKien.values());
//         model.addAttribute("search", search);
//         model.addAttribute("loaiSuKienId", loaiSuKienId);
//         model.addAttribute("trangThai", trangThai);
//         model.addAttribute("fromDate", fromDate);
//         model.addAttribute("toDate", toDate);

//         return "admin/su-kien/list";
//     }

//     @GetMapping("/form")
//     public String showForm(@RequestParam(required = false) Long id, Model model) {
//         SuKienDTO suKienDTO;

//         if (id != null) {
//             Optional<SuKien> suKienOpt = suKienRepository.findByIdAndDeletedFalse(id);
//             if (suKienOpt.isPresent()) {
//                 suKienDTO = new SuKienDTO(suKienOpt.get());
//             } else {
//                 return "redirect:/admin/su-kien?error=notfound";
//             }
//         } else {
//             suKienDTO = new SuKienDTO();
//         }

//         // Load danh sách loại sự kiện đang kích hoạt
//         List<LoaiSuKien> danhSachLoaiSuKien = loaiSuKienRepository
//                 .findByDeletedFalseAndKichHoatTrueOrderByThuTuAscTenLoaiAsc();

//         model.addAttribute("suKienDTO", suKienDTO);
//         model.addAttribute("danhSachLoaiSuKien", danhSachLoaiSuKien);
//         model.addAttribute("danhSachTrangThai", SuKien.TrangThaiSuKien.values());
//         model.addAttribute("isEdit", id != null);

//         return "admin/su-kien/form";
//     }

//     @PostMapping("/save")
//     public String save(@Valid @ModelAttribute SuKienDTO suKienDTO,
//             BindingResult bindingResult,
//             RedirectAttributes redirectAttributes,
//             Model model) {

//         // Validate loại sự kiện
//         if (suKienDTO.getLoaiSuKienId() == null) {
//             bindingResult.rejectValue("loaiSuKienId", "error.required", "Vui lòng chọn loại sự kiện");
//         } else {
//             Optional<LoaiSuKien> loaiSuKienOpt = loaiSuKienRepository
//                     .findByIdAndDeletedFalse(suKienDTO.getLoaiSuKienId());
//             if (!loaiSuKienOpt.isPresent()) {
//                 bindingResult.rejectValue("loaiSuKienId", "error.invalid", "Loại sự kiện không hợp lệ");
//             } else if (!loaiSuKienOpt.get().isKichHoat()) {
//                 bindingResult.rejectValue("loaiSuKienId", "error.inactive", "Loại sự kiện đã bị vô hiệu hóa");
//             } else {
//                 suKienDTO.setLoaiSuKien(loaiSuKienOpt.get());
//             }
//         }

//         if (bindingResult.hasErrors()) {
//             List<LoaiSuKien> danhSachLoaiSuKien = loaiSuKienRepository
//                     .findByDeletedFalseAndKichHoatTrueOrderByThuTuAscTenLoaiAsc();
//             model.addAttribute("danhSachLoaiSuKien", danhSachLoaiSuKien);
//             model.addAttribute("danhSachTrangThai", SuKien.TrangThaiSuKien.values());
//             model.addAttribute("isEdit", suKienDTO.getId() != null);
//             return "admin/su-kien/form";
//         }

//         try {
//             SuKien suKien;

//             if (suKienDTO.getId() != null) {
//                 // Update existing
//                 Optional<SuKien> existingOpt = suKienRepository.findByIdAndDeletedFalse(suKienDTO.getId());
//                 if (!existingOpt.isPresent()) {
//                     redirectAttributes.addFlashAttribute("error", "Sự kiện không tồn tại");
//                     return "redirect:/admin/su-kien";
//                 }
//                 suKien = existingOpt.get();
//             } else {
//                 // Create new
//                 suKien = new SuKien();
//             }

//             // Map DTO to entity
//             suKien.setTenSuKien(suKienDTO.getTenSuKien());
//             suKien.setMoTa(suKienDTO.getMoTa());
//             suKien.setNoiDung(suKienDTO.getNoiDung());
//             suKien.setNgayDienRa(suKienDTO.getNgayDienRa());
//             suKien.setDiaDiem(suKienDTO.getDiaDiem());
//             suKien.setLoaiSuKien(suKienDTO.getLoaiSuKien());
//             suKien.setTrangThai(suKienDTO.getTrangThai());
//             suKien.setSoLuongThamGiaDuKien(suKienDTO.getSoLuongThamGiaDuKien());
//             suKien.setSoLuongThamGiaThucTe(suKienDTO.getSoLuongThamGiaThucTe());
//             suKien.setGhiChu(suKienDTO.getGhiChu());

//             // Handle file upload
//             if (suKienDTO.getHinhAnhFile() != null && !suKienDTO.getHinhAnhFile().isEmpty()) {
//                 String fileName = fileUploadService.saveFile(suKienDTO.getHinhAnhFile(), "su-kien");
//                 suKien.setHinhAnhUrl(fileName);
//             }

//             suKienRepository.save(suKien);

//             String message = suKienDTO.getId() != null ? "Cập nhật sự kiện thành công" : "Thêm sự kiện thành công";
//             redirectAttributes.addFlashAttribute("success", message);

//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
//         }

//         return "redirect:/admin/su-kien";
//     }

//     @GetMapping("/view/{id}")
//     public String view(@PathVariable Long id, Model model) {
//         Optional<SuKien> suKienOpt = suKienRepository.findByIdAndDeletedFalse(id);

//         if (!suKienOpt.isPresent()) {
//             return "redirect:/admin/su-kien?error=notfound";
//         }

//         model.addAttribute("suKien", suKienOpt.get());
//         return "admin/su-kien/view";
//     }

//     @PostMapping("/delete/{id}")
//     public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//         try {
//             Optional<SuKien> suKienOpt = suKienRepository.findByIdAndDeletedFalse(id);

//             if (suKienOpt.isPresent()) {
//                 SuKien suKien = suKienOpt.get();
//                 suKien.setDeleted(true);
//                 suKienRepository.save(suKien);
//                 redirectAttributes.addFlashAttribute("success", "Xóa sự kiện thành công");
//             } else {
//                 redirectAttributes.addFlashAttribute("error", "Sự kiện không tồn tại");
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa sự kiện");
//         }

//         return "redirect:/admin/su-kien";
//     }

//     @GetMapping("/deleted")
//     public String deletedList(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(required = false) String search,
//             Model model) {

//         Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
//         Page<SuKien> deletedPage = suKienRepository.findDeletedWithSearch(search, pageable);

//         model.addAttribute("deletedPage", deletedPage);
//         model.addAttribute("search", search);

//         return "admin/su-kien/deleted";
//     }

//     @PostMapping("/restore/{id}")
//     public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//         try {
//             Optional<SuKien> suKienOpt = suKienRepository.findById(id);

//             if (suKienOpt.isPresent() && suKienOpt.get().isDeleted()) {
//                 SuKien suKien = suKienOpt.get();
//                 suKien.setDeleted(false);
//                 suKienRepository.save(suKien);
//                 redirectAttributes.addFlashAttribute("success", "Khôi phục sự kiện thành công");
//             } else {
//                 redirectAttributes.addFlashAttribute("error", "Sự kiện không tồn tại hoặc chưa bị xóa");
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi khôi phục sự kiện");
//         }

//         return "redirect:/admin/su-kien/deleted";
//     }
// }