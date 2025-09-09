package com.branch.demo.controller;

import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tin-tuc")
public class NewsController {

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    // Trang chủ tin tức
    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String search,
                       @RequestParam(required = false) Long danhMucId,
                       Model model) {
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "ngayXuatBan"));
        Page<BaiViet> baiVietPage;
        
        if (search != null && !search.trim().isEmpty()) {
            baiVietPage = baiVietRepository.findPublishedWithSearch(search, pageable);
        } else if (danhMucId != null) {
            Optional<DanhMuc> danhMuc = danhMucRepository.findById(danhMucId);
            if (danhMuc.isPresent()) {
                baiVietPage = baiVietRepository.findByDanhMucAndPublished(danhMuc.get(), pageable);
                model.addAttribute("selectedDanhMuc", danhMuc.get());
            } else {
                baiVietPage = baiVietRepository.findAllPublished(pageable);
            }
        } else {
            baiVietPage = baiVietRepository.findAllPublished(pageable);
        }
        
        // Lấy danh sách danh mục cho menu
        List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
            DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
        
        // Lấy bài viết nổi bật
        List<BaiViet> baiVietNoiBat = baiVietRepository.findFeaturedArticles(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan"))).getContent();
        
        // Lấy bài viết mới nhất
        List<BaiViet> baiVietMoiNhat = baiVietRepository.findLatestArticles(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));
        
        // Lấy bài viết phổ biến
        List<BaiViet> baiVietPhoBien = baiVietRepository.findPopularArticles(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "luotXem")));
        
        model.addAttribute("baiVietPage", baiVietPage);
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("baiVietNoiBat", baiVietNoiBat);
        model.addAttribute("baiVietMoiNhat", baiVietMoiNhat);
        model.addAttribute("baiVietPhoBien", baiVietPhoBien);
        model.addAttribute("search", search);
        model.addAttribute("danhMucId", danhMucId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", baiVietPage.getTotalPages());
        
        return "news/index";
    }
    
    // Chi tiết bài viết
    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        Optional<BaiViet> baiVietOpt = baiVietRepository.findBySlugAndPublished(slug);
        
        if (baiVietOpt.isEmpty()) {
            return "redirect:/tin-tuc?error=notfound";
        }
        
        BaiViet baiViet = baiVietOpt.get();
        
        // Tăng lượt xem
        baiViet.tangLuotXem();
        baiVietRepository.save(baiViet);
        
        // Lấy bài viết liên quan
        List<BaiViet> baiVietLienQuan = List.of();
        if (baiViet.getDanhMuc() != null) {
            baiVietLienQuan = baiVietRepository.findRelatedArticles(
                baiViet.getDanhMuc(), baiViet.getId(), 
                PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));
        }
        
        // Lấy danh sách danh mục cho menu
        List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
            DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
        
        model.addAttribute("baiViet", baiViet);
        model.addAttribute("baiVietLienQuan", baiVietLienQuan);
        model.addAttribute("danhMucList", danhMucList);
        
        return "news/detail";
    }
    
    // Danh mục
    @GetMapping("/danh-muc/{slug}")
    public String category(@PathVariable String slug,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        
        Optional<DanhMuc> danhMucOpt = danhMucRepository.findBySlugAndNotDeleted(slug);
        
        if (danhMucOpt.isEmpty()) {
            return "redirect:/tin-tuc?error=category-notfound";
        }
        
        DanhMuc danhMuc = danhMucOpt.get();
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "ngayXuatBan"));
        Page<BaiViet> baiVietPage = baiVietRepository.findByDanhMucAndPublished(danhMuc, pageable);
        
        // Lấy danh sách danh mục cho menu
        List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
            DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
        
        model.addAttribute("danhMuc", danhMuc);
        model.addAttribute("baiVietPage", baiVietPage);
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", baiVietPage.getTotalPages());
        
        return "news/category";
    }
    
    // Tìm kiếm
    @GetMapping("/tim-kiem")
    public String search(@RequestParam String q,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) Long danhMucId,
                        Model model) {
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "ngayXuatBan"));
        Page<BaiViet> baiVietPage;
        
        if (danhMucId != null) {
            baiVietPage = baiVietRepository.findPublishedWithAdvancedFilters(
                q, danhMucId, null, null, pageable);
        } else {
            baiVietPage = baiVietRepository.findPublishedWithSearch(q, pageable);
        }
        
        // Lấy danh sách danh mục cho menu
        List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
            DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
        
        model.addAttribute("baiVietPage", baiVietPage);
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("searchQuery", q);
        model.addAttribute("danhMucId", danhMucId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", baiVietPage.getTotalPages());
        model.addAttribute("totalResults", baiVietPage.getTotalElements());
        
        return "news/search";
    }
}