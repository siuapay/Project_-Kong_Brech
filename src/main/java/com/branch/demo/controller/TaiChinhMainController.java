package com.branch.demo.controller;

import com.branch.demo.domain.TaiChinhNam;
import com.branch.demo.service.TaiChinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/tai-chinh")
public class TaiChinhMainController {

    @Autowired
    private TaiChinhService taiChinhService;

    @GetMapping
    public String taiChinhMain(Model model) {
        // Get current year data
        Integer currentYear = LocalDateTime.now().getYear();
        TaiChinhNam currentYearData = taiChinhService.getNamById(currentYear);

        // Get recent years data
        List<TaiChinhNam> recentYears = taiChinhService.getNamWithData();
        if (recentYears.size() > 5) {
            recentYears = recentYears.subList(0, 5);
        }

        // Get overall statistics
        BigDecimal totalThu = taiChinhService.getTotalThuAllTime();
        BigDecimal totalChi = taiChinhService.getTotalChiAllTime();
        BigDecimal totalSoDu = taiChinhService.getTotalSoDuAllTime();
        long totalTransactions = taiChinhService.getTotalTransactionCount();
        long totalCategories = taiChinhService.getAllDanhMuc().size();

        // Get available years
        List<Integer> availableYears = taiChinhService.getAvailableYears();

        model.addAttribute("title", "Quản Lý Tài Chính");
        model.addAttribute("pageTitle", "Tổng Quan Tài Chính");
        model.addAttribute("activeMenu", "tai-chinh");

        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentYearData", currentYearData);
        model.addAttribute("recentYears", recentYears);
        model.addAttribute("availableYears", availableYears);

        model.addAttribute("totalThu", totalThu);
        model.addAttribute("totalChi", totalChi);
        model.addAttribute("totalSoDu", totalSoDu);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalCategories", totalCategories);

        return "tai-chinh/index";
    }
}