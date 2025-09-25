package com.branch.demo.controller.client;

import com.branch.demo.service.AdminService;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.DanhMucRepository;
import com.branch.demo.repository.NhanSuRepository;
import com.branch.demo.repository.BanNganhRepository;
import com.branch.demo.repository.TaiChinhGiaoDichRepository;
import com.branch.demo.repository.DiemNhomRepository;
import com.branch.demo.repository.TinHuuRepository;
import com.branch.demo.repository.SuKienRepository;
import com.branch.demo.repository.NhomRepository;
import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.domain.NhanSu;
import com.branch.demo.domain.BanNganh;
import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.DiemNhom;
import com.branch.demo.domain.TinHuu;
import com.branch.demo.domain.SuKien;
import com.branch.demo.domain.Nhom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.List;
import java.math.BigDecimal;

@Controller
public class ClientController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @Autowired
    private com.branch.demo.repository.ChapSuRepository chapSuRepository;

    @Autowired
    private BanNganhRepository banNganhRepository;

    @Autowired
    private TaiChinhGiaoDichRepository taiChinhGiaoDichRepository;

    @Autowired
    private DiemNhomRepository diemNhomRepository;

    @Autowired
    private TinHuuRepository tinHuuRepository;

    @Autowired
    private SuKienRepository suKienRepository;

    @Autowired
    private NhomRepository nhomRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Trang Chủ - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Chào Mừng Đến Với Chi Hội Kong Brech");

        // Get latest news/articles
        try {
            java.util.List<BaiViet> latestNews = baiVietRepository.findLatestArticles(
                    PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));
            model.addAttribute("latestNews", latestNews);
        } catch (Exception e) {
            // Handle gracefully if news service fails
            model.addAttribute("latestNews", java.util.Collections.emptyList());
        }

        // Get statistics
        try {
            java.util.Map<String, Object> stats = adminService.getDashboardStats();
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            // Handle gracefully if stats fail
            model.addAttribute("stats", java.util.Collections.emptyMap());
        }

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Giới Thiệu - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Giới Thiệu Chi Hội");
        return "client/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Liên Hệ - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Liên Hệ Với Chúng Tôi");
        return "client/contact";
    }

    @GetMapping("/news")
    public String news(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long danhMucId,
            Model model) {
        model.addAttribute("title", "Tin Tức - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Tin Tức & Sự Kiện");

        try {
            Pageable pageable = PageRequest.of(page, 7, Sort.by(Sort.Direction.DESC, "ngayXuatBan"));
            Page<BaiViet> baiVietPage;

            if (search != null && !search.trim().isEmpty()) {
                baiVietPage = baiVietRepository.findPublishedWithSearch(search, pageable);
            } else if (danhMucId != null) {
                java.util.Optional<DanhMuc> danhMuc = danhMucRepository.findById(danhMucId);
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
            java.util.List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
                    DanhMuc.TrangThaiDanhMuc.HOAT_DONG);

            // Lấy bài viết nổi bật
            java.util.List<BaiViet> baiVietNoiBat = baiVietRepository.findFeaturedArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan"))).getContent();

            // Lấy bài viết mới nhất
            java.util.List<BaiViet> baiVietMoiNhat = baiVietRepository.findLatestArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));

            // Lấy bài viết phổ biến
            java.util.List<BaiViet> baiVietPhoBien = baiVietRepository.findPopularArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "luotXem")));

            model.addAttribute("baiVietPage", baiVietPage);
            model.addAttribute("danhMucList", danhMucList);
            model.addAttribute("baiVietNoiBat", baiVietNoiBat);
            model.addAttribute("baiVietMoiNhat", baiVietMoiNhat);
            model.addAttribute("baiVietPhoBien", baiVietPhoBien);
            model.addAttribute("search", search != null && !search.trim().isEmpty() ? search : null);
            model.addAttribute("danhMucId", danhMucId);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", baiVietPage.getTotalPages());

        } catch (Exception e) {
            model.addAttribute("baiVietPage", Page.empty());
            model.addAttribute("danhMucList", java.util.Collections.emptyList());
            model.addAttribute("baiVietNoiBat", java.util.Collections.emptyList());
            model.addAttribute("baiVietMoiNhat", java.util.Collections.emptyList());
            model.addAttribute("baiVietPhoBien", java.util.Collections.emptyList());
        }

        return "client/news";
    }

    @GetMapping("/news/{slug}")
    public String newsDetail(@PathVariable String slug, Model model) {
        try {
            java.util.Optional<BaiViet> baiVietOpt = baiVietRepository.findBySlugAndPublished(slug);

            if (baiVietOpt.isEmpty()) {
                return "redirect:/news?error=notfound";
            }

            BaiViet baiViet = baiVietOpt.get();

            // Tăng lượt xem
            baiViet.tangLuotXem();
            baiVietRepository.save(baiViet);

            // Lấy bài viết liên quan
            java.util.List<BaiViet> baiVietLienQuan = java.util.Collections.emptyList();
            if (baiViet.getDanhMuc() != null) {
                baiVietLienQuan = baiVietRepository.findRelatedArticles(
                        baiViet.getDanhMuc(), baiViet.getId(),
                        PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));
            }

            // Lấy danh sách danh mục cho menu
            java.util.List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
                    DanhMuc.TrangThaiDanhMuc.HOAT_DONG);

            // Lấy bài viết nổi bật
            java.util.List<BaiViet> baiVietNoiBat = baiVietRepository.findFeaturedArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan"))).getContent();

            // Lấy bài viết mới nhất
            java.util.List<BaiViet> baiVietMoiNhat = baiVietRepository.findLatestArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "ngayXuatBan")));

            // Lấy bài viết phổ biến
            java.util.List<BaiViet> baiVietPhoBien = baiVietRepository.findPopularArticles(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "luotXem")));

            model.addAttribute("danhMucList", danhMucList);
            model.addAttribute("baiVietNoiBat", baiVietNoiBat);
            model.addAttribute("baiVietMoiNhat", baiVietMoiNhat);
            model.addAttribute("baiVietPhoBien", baiVietPhoBien);

            model.addAttribute("title", baiViet.getTieuDe() + " - Chi Hội Kong Brech");
            model.addAttribute("pageTitle", baiViet.getTieuDe());
            model.addAttribute("baiViet", baiViet);
            model.addAttribute("baiVietLienQuan", baiVietLienQuan);
            model.addAttribute("danhMucList", danhMucList);

            return "client/news-detail";
        } catch (Exception e) {
            return "redirect:/news";
        }
    }

    // Đặc trách ban ngành
    @GetMapping("/organization/departments")
    public String departments(Model model) {
        model.addAttribute("title", "Đặc Trách Ban Ngành - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Đặc Trách Ban Ngành");

        try {
            java.util.List<BanNganh> banNganhList = banNganhRepository.findAll();

            // Load members for each department
            for (BanNganh banNganh : banNganhList) {
                java.util.List<NhanSu> members = nhanSuRepository.findByBanNganh(banNganh);
                banNganh.setDanhSachNhanSu(members);
            }

            System.out.println("Found " + banNganhList.size() + " ban nganh");
            model.addAttribute("banNganhList", banNganhList);
        } catch (Exception e) {
            System.out.println("Error loading ban nganh: " + e.getMessage());
            model.addAttribute("banNganhList", java.util.Collections.emptyList());
        }

        return "client/departments";
    }

    // Chấp sự
    @GetMapping("/organization/personnel")
    public String personnel(Model model) {
        model.addAttribute("title", "Chấp Sự - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Ban Chấp Sự");

        try {
            // Get all Chấp sự with active status
            java.util.List<com.branch.demo.domain.ChapSu> allChapSu = chapSuRepository.findByTrangThaiOrderByHoTenAsc(
                    com.branch.demo.domain.ChapSu.TrangThaiChapSu.DANG_NHIEM_VU);

            com.branch.demo.domain.ChapSu chapSuChinh = null;
            java.util.List<com.branch.demo.domain.ChapSu> banChapSu = new java.util.ArrayList<>();

            for (com.branch.demo.domain.ChapSu cs : allChapSu) {
                if (cs.getChucVu() != null) {
                    // Find main leader (Mục sư or Truyền đạo)
                    if (cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.MUC_SU ||
                            cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.TRUYEN_DAO) {
                        if (chapSuChinh == null) {
                            chapSuChinh = cs;
                        }
                    }
                    // Find all other Chấp sự members
                    if (cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.CHAP_SU_TRUONG ||
                            cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.CHAP_SU ||
                            cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.THU_KY ||
                            cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.THU_QUY ||
                            cs.getChucVu() == com.branch.demo.domain.ChapSu.ChucVu.THANH_VIEN) {
                        banChapSu.add(cs);
                    }
                }
            }

            // Sort ban chấp sự by chức vụ priority
            banChapSu.sort((a, b) -> {
                int priorityA = getChucVuPriority(a.getChucVu());
                int priorityB = getChucVuPriority(b.getChucVu());
                return Integer.compare(priorityA, priorityB);
            });

            model.addAttribute("chapSuChinh", chapSuChinh);
            model.addAttribute("banChapSu", banChapSu);
            model.addAttribute("totalChapSu", allChapSu.size());

            return "client/personnel";
        } catch (Exception e) {
            System.out.println("Error loading chấp sự: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("chapSuChinh", null);
            model.addAttribute("banChapSu", java.util.Collections.emptyList());
            model.addAttribute("totalChapSu", 0);
            return "client/personnel";
        }
    }

    // Helper method to determine chức vụ priority for sorting
    private int getChucVuPriority(com.branch.demo.domain.ChapSu.ChucVu chucVu) {
        if (chucVu == null)
            return 999;
        switch (chucVu) {
            case CHAP_SU_TRUONG:
                return 1;
            case THU_KY:
                return 2;
            case THU_QUY:
                return 3;
            case CHAP_SU:
                return 4;
            case THANH_VIEN:
                return 5;
            default:
                return 999;
        }
    }

    // Nhân sự (từ bảng NhanSu)
    @GetMapping("/organization/staff")
    public String staff(Model model) {
        model.addAttribute("title", "Nhân Sự - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Nhân Sự Chi Hội");

        try {
            // Get all active điểm nhóm
            java.util.List<DiemNhom> diemNhomList = diemNhomRepository.findByTrangThai(
                    DiemNhom.TrangThaiDiemNhom.HOAT_DONG);

            // Load staff for each điểm nhóm
            for (DiemNhom diemNhom : diemNhomList) {
                java.util.List<NhanSu> staffList = nhanSuRepository.findByDiemNhomId(diemNhom.getId());

                // Filter only active staff
                staffList = staffList.stream()
                        .filter(ns -> ns.getTrangThai() == NhanSu.TrangThaiNhanSu.HOAT_DONG)
                        .collect(java.util.stream.Collectors.toList());

                // Sort by chức vụ priority
                staffList.sort((a, b) -> {
                    int priorityA = getNhanSuChucVuPriority(a.getChucVu());
                    int priorityB = getNhanSuChucVuPriority(b.getChucVu());
                    return Integer.compare(priorityA, priorityB);
                });

                diemNhom.setDanhSachNhanSuTrucTiep(staffList);
            }

            // Get total staff count
            long totalStaff = nhanSuRepository.countByTrangThai(NhanSu.TrangThaiNhanSu.HOAT_DONG);

            model.addAttribute("diemNhomList", diemNhomList);
            model.addAttribute("totalStaff", totalStaff);

            return "client/staff";
        } catch (Exception e) {
            System.out.println("Error loading staff: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("diemNhomList", java.util.Collections.emptyList());
            model.addAttribute("totalStaff", 0);
            return "client/staff";
        }
    }

    // Helper method for NhanSu chức vụ priority
    private int getNhanSuChucVuPriority(NhanSu.ChucVu chucVu) {
        if (chucVu == null)
            return 999;
        switch (chucVu) {
            case MUC_SU:
                return 1;
            case TRUYEN_DAO:
                return 2;
            case CHAP_SU:
                return 3;
            case THU_KY:
                return 4;
            case THU_QUY:
                return 5;
            case THANH_VIEN:
                return 6;
            default:
                return 999;
        }
    }

    // Quản nhiệm hội thánh
    @GetMapping("/organization/management")
    public String management(Model model) {
        model.addAttribute("title", "Quản Nhiệm Hội Thánh - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Quản Nhiệm Hội Thánh");

        try {
            // Tổng quan hoạt động
            YearMonth currentMonth = YearMonth.now();
            LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

            System.out.println("=== MANAGEMENT STATISTICS DEBUG ===");
            System.out.println("Current month: " + currentMonth);
            System.out.println("Start of month: " + startOfMonth);
            System.out.println("End of month: " + endOfMonth);

            // Debug: Check total records in each table
            try {
                long totalBaiViet = baiVietRepository.count();
                long totalTaiChinhGiaoDich = taiChinhGiaoDichRepository.count();
                long totalDiemNhomAll = diemNhomRepository.count();
                long totalTinHuuAll = tinHuuRepository.count();

                System.out.println("Total records - BaiViet: " + totalBaiViet +
                        ", TaiChinhGiaoDich: " + totalTaiChinhGiaoDich +
                        ", DiemNhom: " + totalDiemNhomAll +
                        ", TinHuu: " + totalTinHuuAll);
            } catch (Exception e) {
                System.out.println("Error checking total records: " + e.getMessage());
            }

            // Tổng tín hữu
            long totalTinHuu = 0;
            try {
                totalTinHuu = tinHuuRepository.count();
                System.out.println("Total tin huu: " + totalTinHuu);
            } catch (Exception e) {
                System.out.println("Error counting tin huu: " + e.getMessage());
            }
            model.addAttribute("totalTinHuu", totalTinHuu);

            // Tổng sự kiện tháng này (từ bảng SuKien)
            long totalSuKienThangNay = 0;
            try {
                // Đếm sự kiện trong tháng hiện tại
                java.util.List<SuKien> suKienThangNay = suKienRepository.findSuKienTheoThang(
                        currentMonth.getYear(), currentMonth.getMonthValue());
                totalSuKienThangNay = suKienThangNay.size();
                System.out.println("Total su kien thang nay: " + totalSuKienThangNay);
            } catch (Exception e) {
                System.out.println("Error counting su kien: " + e.getMessage());
                // Fallback: đếm tất cả sự kiện active
                try {
                    totalSuKienThangNay = suKienRepository.countActive();
                    System.out.println("Fallback total su kien: " + totalSuKienThangNay);
                } catch (Exception e2) {
                    System.out.println("Error in fallback count: " + e2.getMessage());
                }
            }
            model.addAttribute("totalSuKienThangNay", totalSuKienThangNay);

            // Dâng hiến tháng này (chỉ tính loại thu)
            BigDecimal dangHienThangNay = BigDecimal.ZERO;
            try {
                dangHienThangNay = taiChinhGiaoDichRepository.sumByLoaiGiaoDichAndNgayGiaoDichBetween(
                        TaiChinhGiaoDich.LoaiGiaoDich.THU, startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
                if (dangHienThangNay == null) {
                    dangHienThangNay = BigDecimal.ZERO;
                }
                System.out.println("Dang hien thang nay: " + dangHienThangNay);
            } catch (Exception e) {
                System.out.println("Error calculating dang hien: " + e.getMessage());
                // Fallback: tính tổng tất cả giao dịch thu
                try {
                    dangHienThangNay = taiChinhGiaoDichRepository.getTongTienByLoai(TaiChinhGiaoDich.LoaiGiaoDich.THU);
                    if (dangHienThangNay == null) {
                        dangHienThangNay = BigDecimal.ZERO;
                    }
                    System.out.println("Fallback total thu: " + dangHienThangNay);
                } catch (Exception e2) {
                    System.out.println("Error in fallback dang hien: " + e2.getMessage());
                    dangHienThangNay = BigDecimal.ZERO;
                }
            }
            model.addAttribute("dangHienThangNay", dangHienThangNay);

            // Tổng điểm nhóm
            long totalDiemNhom = 0;
            try {
                totalDiemNhom = diemNhomRepository.count();
                System.out.println("Total diem nhom: " + totalDiemNhom);
            } catch (Exception e) {
                System.out.println("Error counting diem nhom: " + e.getMessage());
            }
            model.addAttribute("totalDiemNhom", totalDiemNhom);

            // 10 sự kiện gần nhất theo ngày diễn ra
            java.util.List<SuKien> recentEvents = java.util.Collections.emptyList();
            try {
                recentEvents = suKienRepository.findRecentEventsByDate(
                        PageRequest.of(0, 10));
                System.out.println("Recent events: " + recentEvents.size());
            } catch (Exception e) {
                System.out.println("Error loading recent events: " + e.getMessage());
            }
            model.addAttribute("recentEvents", recentEvents);

            return "client/management";
        } catch (Exception e) {
            System.out.println("General error in management: " + e.getMessage());
            e.printStackTrace();
            // Set default values on error
            model.addAttribute("totalTinHuu", 0L);
            model.addAttribute("totalSuKienThangNay", 0L);
            model.addAttribute("dangHienThangNay", BigDecimal.ZERO);
            model.addAttribute("totalDiemNhom", 0L);
            model.addAttribute("recentEvents", java.util.Collections.emptyList());
            return "client/management";
        }
    }

    // Danh sách tín hữu
    @GetMapping("/list/tin-huu")
    public String tinHuuList(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        model.addAttribute("title", "Danh Sách Tín Hữu - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Danh Sách Tín Hữu");

        try {
            // Thống kê tín hữu
            long totalTinHuu = tinHuuRepository.count();
            long totalNam = tinHuuRepository.countByGioiTinh("Nam");
            long totalNu = tinHuuRepository.countByGioiTinh("Nữ");

            model.addAttribute("totalTinHuu", totalTinHuu);
            model.addAttribute("totalNam", totalNam);
            model.addAttribute("totalNu", totalNu);

            // Danh sách tín hữu với phân trang
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "hoTen"));
            Page<TinHuu> tinHuuPage;

            if (search != null && !search.trim().isEmpty()) {
                tinHuuPage = tinHuuRepository.findByHoTenContainingIgnoreCase(search.trim(), pageable);
                model.addAttribute("search", search);
            } else {
                tinHuuPage = tinHuuRepository.findAll(pageable);
            }

            model.addAttribute("tinHuuPage", tinHuuPage);
            model.addAttribute("currentPage", page);

            return "client/tin-huu-list";
        } catch (Exception e) {
            model.addAttribute("totalTinHuu", 0L);
            model.addAttribute("totalNam", 0L);
            model.addAttribute("totalNu", 0L);
            model.addAttribute("tinHuuPage", Page.empty());
            return "client/tin-huu-list";
        }
    }

    // Danh sách điểm nhóm
    @GetMapping("/list/diem-nhom")
    public String diemNhomList(Model model) {
        model.addAttribute("title", "Danh Sách Điểm Nhóm - Chi Hội Kong Brech");
        model.addAttribute("pageTitle", "Danh Sách Điểm Nhóm");

        try {
            // Thống kê điểm nhóm
            long totalDiemNhom = diemNhomRepository.count();
            long diemDangHoatDong = diemNhomRepository.countByTrangThai(DiemNhom.TrangThaiDiemNhom.HOAT_DONG);

            System.out.println("Total diem nhom: " + totalDiemNhom);
            System.out.println("Diem dang hoat dong: " + diemDangHoatDong);

            model.addAttribute("totalDiemNhom", totalDiemNhom);
            model.addAttribute("diemDangHoatDong", diemDangHoatDong);

            // Danh sách điểm nhóm
            List<DiemNhom> diemNhomList = diemNhomRepository.findAll(Sort.by(Sort.Direction.ASC, "tenDiemNhom"));
            System.out.println("Diem nhom list size: " + diemNhomList.size());
            model.addAttribute("diemNhomList", diemNhomList);

            // Danh sách nhóm
            List<Nhom> nhomList = nhomRepository.findAll(Sort.by(Sort.Direction.ASC, "tenNhom"));
            System.out.println("Nhom list size: " + nhomList.size());
            model.addAttribute("nhomList", nhomList);

            return "client/diem-nhom-list";
        } catch (Exception e) {
            System.out.println("Error in diemNhomList: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("totalDiemNhom", 0L);
            model.addAttribute("diemDangHoatDong", 0L);
            model.addAttribute("diemNhomList", java.util.Collections.emptyList());
            model.addAttribute("nhomList", java.util.Collections.emptyList());
            return "client/diem-nhom-list";
        }
    }
}