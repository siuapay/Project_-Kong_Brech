package com.branch.demo.controller;

import com.branch.demo.domain.ThongBao;
import com.branch.demo.dto.ThongBaoDTO;
import com.branch.demo.service.ThongBaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-bao")
@PreAuthorize("hasRole('ADMIN')")
public class ThongBaoController {
    
    @Autowired
    private ThongBaoService thongBaoService;
    
    /**
     * Lấy danh sách thông báo mới nhất
     */
    @GetMapping("/moi-nhat")
    public ResponseEntity<Map<String, Object>> getThongBaoMoiNhat(Authentication auth) {
        try {
            String username = auth.getName();
            
            List<ThongBao> thongBaoList = thongBaoService.getThongBaoMoiNhat(username, 10);
            long chuaDocCount = thongBaoService.countThongBaoChuaDoc(username);
            
            // Convert to DTO to avoid serialization issues
            List<ThongBaoDTO> thongBaoDTOList = thongBaoList.stream()
                .map(ThongBaoDTO::new)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("thongBaoList", thongBaoDTOList);
            response.put("chuaDocCount", chuaDocCount);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi tải thông báo: " + e.getMessage());
            response.put("thongBaoList", new java.util.ArrayList<>());
            response.put("chuaDocCount", 0);
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Đếm số thông báo chưa đọc
     */
    @GetMapping("/count-chua-doc")
    public ResponseEntity<Map<String, Object>> countChuaDoc(Authentication auth) {
        try {
            String username = auth.getName();
            long count = thongBaoService.countThongBaoChuaDoc(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("count", 0);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Đánh dấu thông báo là đã đọc
     */
    @PostMapping("/da-doc/{id}")
    public ResponseEntity<Map<String, Object>> daDocThongBao(@PathVariable Long id) {
        try {
            thongBaoService.daDocThongBao(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã đánh dấu thông báo là đã đọc");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Đánh dấu tất cả thông báo là đã đọc
     */
    @PostMapping("/da-doc-tat-ca")
    public ResponseEntity<Map<String, Object>> daDocTatCa(Authentication auth) {
        try {
            String username = auth.getName();
            thongBaoService.daDocTatCaThongBao(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã đánh dấu tất cả thông báo là đã đọc");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lấy tất cả thông báo
     */
    @GetMapping("/tat-ca")
    public ResponseEntity<Map<String, Object>> getTatCaThongBao(Authentication auth) {
        String username = auth.getName();
        
        List<ThongBao> thongBaoList = thongBaoService.getTatCaThongBao(username);
        
        // Convert to DTO
        List<ThongBaoDTO> thongBaoDTOList = thongBaoList.stream()
            .map(ThongBaoDTO::new)
            .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("thongBaoList", thongBaoDTOList);
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }

}