package com.branch.demo.controller;

import com.branch.demo.domain.Account;
import com.branch.demo.domain.LienHe;
import com.branch.demo.repository.AccountRepository;
import com.branch.demo.service.LienHeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/lien-he")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLienHeController {
    
    @Autowired
    private LienHeService lienHeService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * Trang quản lý liên hệ với 2 tab
     */
    @GetMapping
    public String index(@RequestParam(defaultValue = "tat-ca") String tab,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String search,
                       Model model) {
        
        int pageSize = 10;
        Page<LienHe> lienHePage;
        
        try {
            if ("vi-pham".equals(tab)) {
                // Tab vi phạm - chỉ hiển thị CHO_ADMIN_XU_LY
                lienHePage = lienHeService.getLienHeChoAdminXuLy(page, pageSize);
            } else {
                // Tab tất cả - hiển thị CHUA_XU_LY, DANG_XU_LY, DA_XU_LY
                lienHePage = lienHeService.getAllLienHeForAdmin(search, page, pageSize);
            }
            
            model.addAttribute("lienHePage", lienHePage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", lienHePage.getTotalPages());
            model.addAttribute("totalElements", lienHePage.getTotalElements());
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            lienHePage = Page.empty();
            model.addAttribute("lienHePage", lienHePage);
        }
        
        // Get counts for both tabs
        try {
            long totalAll = lienHeService.countAllLienHeForAdmin();
            long totalViPham = lienHeService.countLienHeChoAdminXuLy();
            
            model.addAttribute("totalAll", totalAll);
            model.addAttribute("totalViPham", totalViPham);
        } catch (Exception e) {
            model.addAttribute("totalAll", 0L);
            model.addAttribute("totalViPham", 0L);
        }
        
        model.addAttribute("activeTab", tab);
        model.addAttribute("search", search);
        model.addAttribute("activeMenu", "lien-he");
        model.addAttribute("pageTitle", "Quản lý liên hệ");
        
        return "admin/lien-he/list";
    }
    
    /**
     * Xem chi tiết liên hệ vi phạm
     */
    @GetMapping("/view/{id}")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            LienHe lienHe = lienHeService.getLienHeById(id);
            
            // Chỉ cho phép xem chi tiết liên hệ vi phạm (CHO_ADMIN_XU_LY)
            if (lienHe.getTrangThai() != LienHe.TrangThaiLienHe.CHO_ADMIN_XU_LY) {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể xem chi tiết liên hệ vi phạm");
                return "redirect:/admin/lien-he?tab=vi-pham";
            }
            
            model.addAttribute("lienHe", lienHe);
            model.addAttribute("activeMenu", "lien-he");
            model.addAttribute("pageTitle", "Chi tiết liên hệ vi phạm");
            
            return "admin/lien-he/view";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy liên hệ: " + e.getMessage());
            return "redirect:/admin/lien-he?tab=vi-pham";
        }
    }
    
    /**
     * Xử lý liên hệ vi phạm
     */
    @PostMapping("/xu-ly/{id}")
    public String xuLyViPham(@PathVariable Long id,
                            @RequestParam LienHe.QuyetDinhAdmin quyetDinh,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Optional<Account> adminOpt = accountRepository.findByUsername(username);
            
            if (adminOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin admin");
                return "redirect:/admin/lien-he?tab=vi-pham";
            }
            
            Account admin = adminOpt.get();
            LienHe lienHe = lienHeService.adminXuLyViPham(id, admin.getId(), quyetDinh);
            
            String message = quyetDinh == LienHe.QuyetDinhAdmin.XOA_LIEN_HE ? 
                           "Đã xóa liên hệ vi phạm" : "Đã giữ lại liên hệ";
            redirectAttributes.addFlashAttribute("success", message);
            
            return "redirect:/admin/lien-he?tab=vi-pham";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý: " + e.getMessage());
            return "redirect:/admin/lien-he/view/" + id;
        }
    }
    
    /**
     * Xóa liên hệ vi phạm
     */
    @PostMapping("/delete/{id}")
    public String deleteLienHe(@PathVariable Long id, 
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Optional<Account> adminOpt = accountRepository.findByUsername(username);
            
            if (adminOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin admin");
                return "redirect:/admin/lien-he?tab=vi-pham";
            }
            
            Account admin = adminOpt.get();
            lienHeService.adminXuLyViPham(id, admin.getId(), LienHe.QuyetDinhAdmin.XOA_LIEN_HE);
            
            redirectAttributes.addFlashAttribute("success", "Đã xóa liên hệ vi phạm thành công");
            return "redirect:/admin/lien-he?tab=vi-pham";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa liên hệ: " + e.getMessage());
            return "redirect:/admin/lien-he?tab=vi-pham";
        }
    }
    
    /**
     * Xóa một liên hệ (cho tab tất cả)
     */
    @PostMapping("/delete-single/{id}")
    public String deleteSingleLienHe(@PathVariable Long id, 
                                    RedirectAttributes redirectAttributes) {
        try {
            lienHeService.deleteLienHe(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa liên hệ thành công");
            return "redirect:/admin/lien-he?tab=tat-ca";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa liên hệ: " + e.getMessage());
            return "redirect:/admin/lien-he?tab=tat-ca";
        }
    }
    
    /**
     * Xóa nhiều liên hệ (cho tab tất cả)
     */
    @PostMapping("/delete-bulk")
    public String deleteBulkLienHe(@RequestParam String selectedIds,
                                  RedirectAttributes redirectAttributes) {
        try {
            String[] ids = selectedIds.split(",");
            int deletedCount = 0;
            
            for (String idStr : ids) {
                try {
                    Long id = Long.parseLong(idStr.trim());
                    lienHeService.deleteLienHe(id);
                    deletedCount++;
                } catch (Exception e) {
                    // Log error but continue with other deletions
                    System.err.println("Lỗi xóa liên hệ ID " + idStr + ": " + e.getMessage());
                }
            }
            
            if (deletedCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    "Đã xóa thành công " + deletedCount + " liên hệ");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa liên hệ nào");
            }
            
            return "redirect:/admin/lien-he?tab=tat-ca";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa liên hệ: " + e.getMessage());
            return "redirect:/admin/lien-he?tab=tat-ca";
        }
    }
}