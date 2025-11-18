package com.branch.demo.controller;

import com.branch.demo.service.TinHuuImportService;
import com.branch.demo.service.TinHuuExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/tin-huu")
public class TinHuuController {

    @Autowired
    private TinHuuImportService tinHuuImportService;

    @Autowired
    private TinHuuExportService tinHuuExportService;

    @GetMapping("/import")
    public String showImportPage(Model model) {
        model.addAttribute("title", "Import Tín Hữu - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Import Danh Sách Tín Hữu");
        return "admin/tin-huu/import";
    }

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file Excel để import!");
                return "redirect:/admin/tin-huu/import";
            }

            // Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file Excel (.xlsx hoặc .xls)!");
                return "redirect:/admin/tin-huu/import";
            }

            // Import data
            TinHuuImportService.ImportResult result = tinHuuImportService.importFromExcel(file);

            // Show result and redirect to tin huu list
            if (result.getErrorCount() == 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Import thành công " + result.getSuccessCount() + " tín hữu!");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Import hoàn tất: " + result.getSuccessCount() + " thành công, " +
                        result.getErrorCount() + " lỗi. " +
                        "Lỗi: " + String.join("; ", result.getErrors()));
            }

            return "redirect:/admin/tin-huu";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi import: " + e.getMessage());
            return "redirect:/admin/tin-huu";
        }
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportExcel() {
        try {
            byte[] excelData = tinHuuExportService.exportToExcel();
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "DanhSachTinDo_" + timestamp + ".xlsx";
            
            ByteArrayResource resource = new ByteArrayResource(excelData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelData.length)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
