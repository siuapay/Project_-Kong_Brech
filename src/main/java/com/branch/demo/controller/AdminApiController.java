package com.branch.demo.controller;

import com.branch.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private AdminService adminService;

    @GetMapping(value = "/all-loai-su-kien", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllLoaiSuKien() {
        try {
            java.util.List<com.branch.demo.domain.LoaiSuKien> loaiSuKienList = adminService.getAllActiveLoaiSuKien();
            System.out.println("API /admin/api/all-loai-su-kien called successfully, returning " + loaiSuKienList.size() + " items");
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loaiSuKienList);
        } catch (Exception e) {
            System.err.println("Error in /admin/api/all-loai-su-kien: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(java.util.Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}