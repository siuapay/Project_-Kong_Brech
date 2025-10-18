package com.branch.demo.controller;

import com.branch.demo.domain.Account;
import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.repository.AccountRepository;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.ChapSuRepository;
import com.branch.demo.repository.DanhMucRepository;
import com.branch.demo.repository.NhanSuRepository;
import com.branch.demo.util.ToastUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private com.branch.demo.service.LienHeService lienHeService;

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @Autowired
    private ChapSuRepository chapSuRepository;

    @Autowired
    private com.branch.demo.service.FileUploadService fileUploadService;

    @Autowired
    private com.branch.demo.service.ThongBaoService thongBaoService;

    // Trang profile chính
    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        Account account = accountOpt.get();
        
        // Tính toán thống kê bài viết
        long totalPosts = baiVietRepository.countByTacGia(username);
        long publishedPosts = baiVietRepository.countByTacGiaAndTrangThai(username, BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        long pendingPosts = baiVietRepository.countByTacGiaAndTrangThai(username, BaiViet.TrangThaiBaiViet.CHO_DUYET);
        long draftPosts = baiVietRepository.countByTacGiaAndTrangThai(username, BaiViet.TrangThaiBaiViet.NHAP);
        long rejectedPosts = baiVietRepository.countByTacGiaAndTrangThai(username, BaiViet.TrangThaiBaiViet.TU_CHOI);
        
        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu()); // Thêm thông tin nhân sự
        model.addAttribute("chapSu", account.getChapSu()); // Thêm thông tin chấp sự
        model.addAttribute("activeTab", "profile");
        
        // Thêm thống kê bài viết
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("publishedPosts", publishedPosts);
        model.addAttribute("pendingPosts", pendingPosts);
        model.addAttribute("draftPosts", draftPosts);
        model.addAttribute("rejectedPosts", rejectedPosts);

        return "account/profile";
    }

    // Trang chỉnh sửa profile
    @GetMapping("/profile/edit")
    public String editProfile(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        Account account = accountOpt.get();
        model.addAttribute("account", account);
        // Chỉ truyền object thực sự tồn tại để sidebar hiển thị đúng
        model.addAttribute("nhanSu", account.getNhanSu());
        model.addAttribute("chapSu", account.getChapSu());
        
        // Tạo object mới cho form binding
        if (account.getNhanSu() != null) {
            model.addAttribute("nhanSuForm", account.getNhanSu());
        } else {
            model.addAttribute("nhanSuForm", new com.branch.demo.domain.NhanSu());
        }
        
        if (account.getChapSu() != null) {
            model.addAttribute("chapSuForm", account.getChapSu());
        } else {
            model.addAttribute("chapSuForm", new com.branch.demo.domain.ChapSu());
        }
        model.addAttribute("activeTab", "profile");

        return "account/profile-edit";
    }

    // Xử lý cập nhật profile
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("profileType") String profileType,
            @ModelAttribute com.branch.demo.domain.NhanSu nhanSu,
            @ModelAttribute com.branch.demo.domain.ChapSu chapSu,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);

            if (accountOpt.isEmpty()) {
                ToastUtil.addNotFoundError(redirectAttributes, "tài khoản");
                return "redirect:/account/profile";
            }

            Account account = accountOpt.get();

            if ("nhansu".equals(profileType)) {
                // Xử lý cập nhật nhân sự
                if (account.getNhanSu() == null) {
                    nhanSu.setTrangThai(com.branch.demo.domain.NhanSu.TrangThaiNhanSu.HOAT_DONG);
                    com.branch.demo.domain.NhanSu savedNhanSu = nhanSuRepository.save(nhanSu);
                    account.setNhanSu(savedNhanSu);
                    accountRepository.save(account);
                } else {
                    // Cập nhật thông tin NhanSu hiện có
                    com.branch.demo.domain.NhanSu existingNhanSu = account.getNhanSu();
                    existingNhanSu.setHoTen(nhanSu.getHoTen());
                    existingNhanSu.setEmail(nhanSu.getEmail());
                    existingNhanSu.setDienThoai(nhanSu.getDienThoai());
                    existingNhanSu.setNgaySinh(nhanSu.getNgaySinh());
                    existingNhanSu.setDiaChi(nhanSu.getDiaChi());
                    existingNhanSu.setTieuSu(nhanSu.getTieuSu());
                    nhanSuRepository.save(existingNhanSu);
                }
            } else if ("chapsu".equals(profileType)) {
                // Xử lý cập nhật chấp sự
                if (account.getChapSu() == null) {
                    chapSu.setTrangThai(com.branch.demo.domain.ChapSu.TrangThaiChapSu.DANG_NHIEM_VU);
                    com.branch.demo.domain.ChapSu savedChapSu = chapSuRepository.save(chapSu);
                    account.setChapSu(savedChapSu);
                    accountRepository.save(account);
                } else {
                    // Cập nhật thông tin ChapSu hiện có
                    com.branch.demo.domain.ChapSu existingChapSu = account.getChapSu();
                    existingChapSu.setHoTen(chapSu.getHoTen());
                    existingChapSu.setEmail(chapSu.getEmail());
                    existingChapSu.setDienThoai(chapSu.getDienThoai());
                    existingChapSu.setNgaySinh(chapSu.getNgaySinh());
                    existingChapSu.setDiaChi(chapSu.getDiaChi());
                    existingChapSu.setTieuSu(chapSu.getTieuSu());
                    chapSuRepository.save(existingChapSu);
                }
            }

            ToastUtil.addProfileUpdateSuccess(redirectAttributes);
            return "redirect:/account/profile";

        } catch (Exception e) {
            ToastUtil.addSystemError(redirectAttributes, e.getMessage());
            return "redirect:/account/profile/edit";
        }
    }

    // Danh sách bài viết của user
    @GetMapping("/my-posts")
    public String myPosts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        Account account = accountOpt.get();
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<BaiViet> baiVietPage;
        // Luôn tìm kiếm trong bài viết của user hiện tại
        baiVietPage = baiVietRepository.findByTacGiaWithSearch(account.getUsername(), search, pageable);

        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu());
        model.addAttribute("chapSu", account.getChapSu());
        model.addAttribute("baiVietPage", baiVietPage);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", baiVietPage.getTotalPages());
        model.addAttribute("activeTab", "posts");

        return "account/my-posts";
    }

    // Form chỉnh sửa bài viết
    @GetMapping("/posts/edit/{id}")
    public String editPost(@PathVariable Long id, Authentication auth, Model model,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        Optional<BaiViet> baiVietOpt = baiVietRepository.findByIdNotDeleted(id);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        if (baiVietOpt.isEmpty()) {
            ToastUtil.addNotFoundError(redirectAttributes, "bài viết");
            return "redirect:/account/my-posts";
        }

        Account account = accountOpt.get();
        BaiViet baiViet = baiVietOpt.get();

        // Kiểm tra quyền sở hữu
        if (!baiViet.getTacGia().equals(account.getUsername())) {
            ToastUtil.addPermissionError(redirectAttributes);
            return "redirect:/account/my-posts";
        }

        // Chỉ cho phép chỉnh sửa bài viết ở trạng thái NHAP hoặc TU_CHOI
        if (baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.NHAP &&
                baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.TU_CHOI) {
            ToastUtil.addWarning(redirectAttributes, "Không thể chỉnh sửa", 
                "Chỉ có thể chỉnh sửa bài viết ở trạng thái nháp hoặc bị từ chối");
            return "redirect:/account/my-posts";
        }

        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu());
        model.addAttribute("chapSu", account.getChapSu());
        model.addAttribute("baiViet", baiViet);
        model.addAttribute("danhMucList", danhMucRepository.findAll());
        model.addAttribute("activeTab", "posts");
        model.addAttribute("isEdit", true);

        return "account/create-post";
    }

    // Cập nhật bài viết
    @PostMapping("/posts/update/{id}")
    public String updatePost(@PathVariable Long id,
            @ModelAttribute BaiViet baiViet,
            @RequestParam Long danhMucId,
            @RequestParam(required = false) String metaTitle,
            @RequestParam(required = false) String metaDescription,
            @RequestParam(required = false) String metaKeywords,
            @RequestParam(value = "anhDaiDienFile", required = false) MultipartFile anhDaiDienFile,
            @RequestParam(value = "hinhAnhFiles", required = false) MultipartFile[] hinhAnhFiles,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            Optional<DanhMuc> danhMucOpt = danhMucRepository.findById(danhMucId);
            Optional<BaiViet> existingBaiVietOpt = baiVietRepository.findByIdNotDeleted(id);

            if (accountOpt.isEmpty() || danhMucOpt.isEmpty() || existingBaiVietOpt.isEmpty()) {
                ToastUtil.addSystemError(redirectAttributes, "Không thể cập nhật bài viết");
                return "redirect:/account/my-posts";
            }

            Account account = accountOpt.get();
            DanhMuc danhMuc = danhMucOpt.get();
            BaiViet existingBaiViet = existingBaiVietOpt.get();

            // Kiểm tra quyền sở hữu
            if (!existingBaiViet.getTacGia().equals(account.getUsername())) {
                ToastUtil.addPermissionError(redirectAttributes);
                return "redirect:/account/my-posts";
            }

            // Tạo slug từ tiêu đề
            String slug = createSlug(baiViet.getTieuDe());

            // Xử lý upload ảnh đại diện
            if (anhDaiDienFile != null && !anhDaiDienFile.isEmpty()) {
                // Xóa ảnh cũ nếu có
                if (existingBaiViet.getAnhDaiDien() != null) {
                    try {
                        fileUploadService.deleteImage(existingBaiViet.getAnhDaiDien());
                    } catch (Exception e) {
                        System.err.println("Could not delete old image: " + e.getMessage());
                    }
                }
                String anhDaiDienUrl = fileUploadService.uploadImage(anhDaiDienFile);
                baiViet.setAnhDaiDien(anhDaiDienUrl);
            } else {
                // Giữ ảnh cũ
                baiViet.setAnhDaiDien(existingBaiViet.getAnhDaiDien());
            }

            // Xử lý upload hình ảnh bổ sung
            if (hinhAnhFiles != null && hinhAnhFiles.length > 0 &&
                    !(hinhAnhFiles.length == 1 && hinhAnhFiles[0].isEmpty())) {
                java.util.List<String> hinhAnhUrls = new java.util.ArrayList<>();
                for (MultipartFile file : hinhAnhFiles) {
                    if (!file.isEmpty()) {
                        String imageUrl = fileUploadService.uploadImage(file);
                        hinhAnhUrls.add(imageUrl);
                    }
                }
                if (!hinhAnhUrls.isEmpty()) {
                    baiViet.setDanhSachHinhAnh(hinhAnhUrls);
                } else {
                    baiViet.setDanhSachHinhAnh(existingBaiViet.getDanhSachHinhAnh());
                }
            } else {
                // Giữ hình ảnh cũ
                baiViet.setDanhSachHinhAnh(existingBaiViet.getDanhSachHinhAnh());
            }

            // Preserve thông tin quan trọng
            baiViet.setId(existingBaiViet.getId());
            baiViet.setSlug(slug);
            baiViet.setDanhMuc(danhMuc);
            baiViet.setTacGia(existingBaiViet.getTacGia());
            baiViet.setLoaiTacGia(existingBaiViet.getLoaiTacGia());
            baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.CHO_DUYET); // Reset về nháp để admin duyệt lại
            baiViet.setNoiBat(existingBaiViet.getNoiBat());
            baiViet.setLuotXem(existingBaiViet.getLuotXem());
            baiViet.setCreatedAt(existingBaiViet.getCreatedAt());
            baiViet.setDanhSachVideo(existingBaiViet.getDanhSachVideo());

            // Set SEO fields
            baiViet.setMetaTitle(metaTitle != null && !metaTitle.trim().isEmpty() ? metaTitle : baiViet.getTieuDe());
            baiViet.setMetaDescription(metaDescription != null && !metaDescription.trim().isEmpty() ? metaDescription
                    : baiViet.getTomTat());
            baiViet.setMetaKeywords(metaKeywords);

            BaiViet savedBaiViet = baiVietRepository.save(baiViet);

            // Tạo thông báo cho admin về bài viết cập nhật
            try {
                thongBaoService.taoThongBaiBaiVietCapNhat(savedBaiViet, username);
            } catch (Exception e) {
                System.err.println("Lỗi tạo thông báo: " + e.getMessage());
            }

            ToastUtil.addPostUpdateSuccess(redirectAttributes);
            return "redirect:/account/my-posts";

        } catch (Exception e) {
            ToastUtil.addSystemError(redirectAttributes, e.getMessage());
            return "redirect:/account/my-posts";
        }
    }

    // Xóa bài viết (hard delete)
    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            Optional<BaiViet> baiVietOpt = baiVietRepository.findByIdNotDeleted(id);

            if (accountOpt.isEmpty() || baiVietOpt.isEmpty()) {
                ToastUtil.addNotFoundError(redirectAttributes, "bài viết");
                return "redirect:/account/my-posts";
            }

            Account account = accountOpt.get();
            BaiViet baiViet = baiVietOpt.get();

            // Kiểm tra quyền sở hữu
            if (!baiViet.getTacGia().equals(account.getUsername())) {
                ToastUtil.addPermissionError(redirectAttributes);
                return "redirect:/account/my-posts";
            }

            // Chỉ cho phép xóa bài viết ở trạng thái NHAP hoặc TU_CHOI
            if (baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.NHAP &&
                    baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.TU_CHOI) {
                ToastUtil.addWarning(redirectAttributes, "Không thể xóa", 
                    "Chỉ có thể xóa bài viết ở trạng thái nháp hoặc bị từ chối");
                return "redirect:/account/my-posts";
            }

            // Xóa các file ảnh liên quan trước khi xóa bài viết
            try {
                // Xóa ảnh đại diện
                if (baiViet.getAnhDaiDien() != null && !baiViet.getAnhDaiDien().isEmpty()) {
                    fileUploadService.deleteImage(baiViet.getAnhDaiDien());
                }

                // Xóa các hình ảnh bổ sung
                if (baiViet.getDanhSachHinhAnh() != null && !baiViet.getDanhSachHinhAnh().isEmpty()) {
                    for (String imageUrl : baiViet.getDanhSachHinhAnh()) {
                        fileUploadService.deleteImage(imageUrl);
                    }
                }
            } catch (Exception e) {
                // Log error but continue with deletion
                System.err.println("Could not delete images: " + e.getMessage());
            }

            // Hard delete - xóa hoàn toàn khỏi database
            baiVietRepository.delete(baiViet);

            ToastUtil.addPostDeleteSuccess(redirectAttributes);

        } catch (Exception e) {
            ToastUtil.addSystemError(redirectAttributes, "Không thể xóa bài viết: " + e.getMessage());
        }

        return "redirect:/account/my-posts";
    }

    // Form đăng bài viết
    @GetMapping("/posts")
    public String createPost(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        Account account = accountOpt.get();
        List<DanhMuc> danhMucList = danhMucRepository.findByTrangThaiAndNotDeleted(
                DanhMuc.TrangThaiDanhMuc.HOAT_DONG);

        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu());
        model.addAttribute("chapSu", account.getChapSu());
        model.addAttribute("baiViet", new BaiViet());
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("activeTab", "create");

        return "account/create-post";
    }

    // Xử lý đăng bài viết
    @PostMapping("/posts")
    public String savePost(@ModelAttribute BaiViet baiViet,
            @RequestParam Long danhMucId,
            @RequestParam(required = false) String metaTitle,
            @RequestParam(required = false) String metaDescription,
            @RequestParam(required = false) String metaKeywords,
            @RequestParam(value = "anhDaiDienFile", required = false) MultipartFile anhDaiDienFile,
            @RequestParam(value = "hinhAnhFiles", required = false) MultipartFile[] hinhAnhFiles,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            Optional<DanhMuc> danhMucOpt = danhMucRepository.findById(danhMucId);

            if (accountOpt.isEmpty() || danhMucOpt.isEmpty()) {
                ToastUtil.addSystemError(redirectAttributes, "Không thể đăng bài");
                return "redirect:/account/posts";
            }

            Account account = accountOpt.get();
            DanhMuc danhMuc = danhMucOpt.get();

            // Tạo slug từ tiêu đề
            String slug = createSlug(baiViet.getTieuDe());

            // Xử lý upload ảnh đại diện
            if (anhDaiDienFile != null && !anhDaiDienFile.isEmpty()) {
                String anhDaiDienUrl = fileUploadService.uploadImage(anhDaiDienFile);
                baiViet.setAnhDaiDien(anhDaiDienUrl);
            }

            // Xử lý upload hình ảnh bổ sung
            if (hinhAnhFiles != null && hinhAnhFiles.length > 0) {
                java.util.List<String> hinhAnhUrls = new java.util.ArrayList<>();
                for (MultipartFile file : hinhAnhFiles) {
                    if (!file.isEmpty()) {
                        String imageUrl = fileUploadService.uploadImage(file);
                        hinhAnhUrls.add(imageUrl);
                    }
                }
                if (!hinhAnhUrls.isEmpty()) {
                    baiViet.setDanhSachHinhAnh(hinhAnhUrls);
                }
            }

            // Set thông tin bài viết
            baiViet.setSlug(slug);
            baiViet.setDanhMuc(danhMuc);
            baiViet.setTacGia(account.getUsername());
            baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.CONTRIBUTOR);
            baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.CHO_DUYET);
            baiViet.setNoiBat(false);
            baiViet.setLuotXem(0L);

            // Set SEO fields
            baiViet.setMetaTitle(metaTitle != null && !metaTitle.trim().isEmpty() ? metaTitle : baiViet.getTieuDe());
            baiViet.setMetaDescription(metaDescription != null && !metaDescription.trim().isEmpty() ? metaDescription
                    : baiViet.getTomTat());
            baiViet.setMetaKeywords(metaKeywords);

            BaiViet savedBaiViet = baiVietRepository.save(baiViet);

            // Tạo thông báo cho admin
            try {
                thongBaoService.taoThongBaoBaiVietMoi(savedBaiViet, username);
            } catch (Exception e) {
                System.err.println("Lỗi tạo thông báo: " + e.getMessage());
            }

            ToastUtil.addPostCreateSuccess(redirectAttributes, savedBaiViet.getId());
            return "redirect:/account/my-posts";

        } catch (Exception e) {
            ToastUtil.addSystemError(redirectAttributes, e.getMessage());
            return "redirect:/account/posts";
        }
    }

    // Trang bảo mật
    @GetMapping("/security")
    public String security(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return "redirect:/login";
        }

        Account account = accountOpt.get();
        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu());
        model.addAttribute("chapSu", account.getChapSu());
        model.addAttribute("activeTab", "security");

        return "account/security";
    }

    // Đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication auth,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);

            if (accountOpt.isEmpty()) {
                ToastUtil.addNotFoundError(redirectAttributes, "tài khoản");
                return "redirect:/account/security";
            }

            Account account = accountOpt.get();

            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
                ToastUtil.addValidationError(redirectAttributes, "Mật khẩu hiện tại không đúng");
                return "redirect:/account/security";
            }

            // Kiểm tra mật khẩu mới
            if (!newPassword.equals(confirmPassword)) {
                ToastUtil.addValidationError(redirectAttributes, "Mật khẩu xác nhận không khớp");
                return "redirect:/account/security";
            }

            if (newPassword.length() < 6) {
                ToastUtil.addValidationError(redirectAttributes, "Mật khẩu mới phải có ít nhất 6 ký tự");
                return "redirect:/account/security";
            }

            // Cập nhật mật khẩu
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);

            // Invalidate session để đăng xuất người dùng
            request.getSession().invalidate();
            
            // Redirect đến trang login với thông báo thành công
            ToastUtil.addPasswordChangeSuccess(redirectAttributes);
            return "redirect:/login";

        } catch (Exception e) {
            ToastUtil.addSystemError(redirectAttributes, e.getMessage());
            return "redirect:/account/security";
        }
    }



    // Helper method để tạo slug
    private String createSlug(String title) {
        if (title == null)
            return "";

        String slug = title.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Đảm bảo slug unique
        String originalSlug = slug;
        int counter = 1;
        while (baiVietRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    // ==================== LIÊN HỆ MANAGEMENT (CHỈ CHO MODERATOR) ====================
    


    @GetMapping("/lien-he")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public String lienHeManagement(Model model, Authentication authentication,
                                   @RequestParam(defaultValue = "0") int chuaXuLyPage,
                                   @RequestParam(defaultValue = "0") int dangXuLyPage,
                                   @RequestParam(defaultValue = "0") int daXuLyPage,
                                   @RequestParam(defaultValue = "0") int adminPage,
                                   jakarta.servlet.http.HttpServletRequest request) {
        Account account = accountRepository.findByUsername(authentication.getName()).orElse(null);
        if (account == null) {
            return "redirect:/login";
        }
        

        
        // Thêm thông tin nhân sự/chấp sự để hiển thị sidebar
        if (account.getNhanSu() != null) {
            model.addAttribute("nhanSu", account.getNhanSu());
        }
        if (account.getChapSu() != null) {
            model.addAttribute("chapSu", account.getChapSu());
        }
        
        int pageSize = 5; // 5 items per page
        
        // Debug và load data
        try {
            if (account.getRole() == Account.Role.MODERATOR) {
                
                // Tab "Chưa xử lý" - tạm thời không phân trang
                try {
                    java.util.List<com.branch.demo.domain.LienHe> chuaXuLyList = lienHeService.getLienHeChuaXuLy();
                    
                    // Manual pagination - lấy 5 items đầu tiên
                    int startIndex = chuaXuLyPage * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, chuaXuLyList.size());
                    java.util.List<com.branch.demo.domain.LienHe> pageContent = 
                        startIndex < chuaXuLyList.size() ? chuaXuLyList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
                    
                    model.addAttribute("lienHeChuaXuLy", pageContent);
                    
                    // Tạo manual page info
                    int totalPages = (int) Math.ceil((double) chuaXuLyList.size() / pageSize);
                    model.addAttribute("chuaXuLyCurrentPage", chuaXuLyPage);
                    model.addAttribute("chuaXuLyTotalPages", totalPages);
                    model.addAttribute("chuaXuLyTotalElements", chuaXuLyList.size());
                    

                } catch (Exception e) {
                    model.addAttribute("lienHeChuaXuLy", new java.util.ArrayList<>());
                    model.addAttribute("error", "Lỗi tải dữ liệu Chưa xử lý: " + e.getMessage());
                }
                
                // Tab "Đang xử lý" - tạm thời không phân trang
                try {
                    java.util.List<com.branch.demo.domain.LienHe> dangXuLyList = lienHeService.getLienHeDangXuLyByModerator(account.getId());
                    
                    // Manual pagination
                    int startIndex = dangXuLyPage * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, dangXuLyList.size());
                    java.util.List<com.branch.demo.domain.LienHe> pageContent = 
                        startIndex < dangXuLyList.size() ? dangXuLyList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
                    
                    model.addAttribute("lienHeDangXuLy", pageContent);
                    
                    // Manual page info
                    int totalPages = (int) Math.ceil((double) dangXuLyList.size() / pageSize);
                    model.addAttribute("dangXuLyCurrentPage", dangXuLyPage);
                    model.addAttribute("dangXuLyTotalPages", totalPages);
                    model.addAttribute("dangXuLyTotalElements", dangXuLyList.size());
                    

                } catch (Exception e) {
                    model.addAttribute("lienHeDangXuLy", new java.util.ArrayList<>());
                }
                
                // Tab "Đã xử lý" - tạm thời không phân trang
                try {
                    java.util.List<com.branch.demo.domain.LienHe> daXuLyList = lienHeService.getLienHeDaXuLyByModerator(account.getId());
                    
                    // Manual pagination
                    int startIndex = daXuLyPage * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, daXuLyList.size());
                    java.util.List<com.branch.demo.domain.LienHe> pageContent = 
                        startIndex < daXuLyList.size() ? daXuLyList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
                    
                    model.addAttribute("lienHeByModerator", pageContent);
                    
                    // Manual page info
                    int totalPages = (int) Math.ceil((double) daXuLyList.size() / pageSize);
                    model.addAttribute("daXuLyCurrentPage", daXuLyPage);
                    model.addAttribute("daXuLyTotalPages", totalPages);
                    model.addAttribute("daXuLyTotalElements", daXuLyList.size());
                    

                } catch (Exception e) {
                    model.addAttribute("lienHeByModerator", new java.util.ArrayList<>());
                }
                
            } else if (account.getRole() == Account.Role.ADMIN) {
                // Tab ADMIN - tạm thời không phân trang
                try {
                    java.util.List<com.branch.demo.domain.LienHe> adminList = lienHeService.getLienHeChoAdminXuLy();
                    
                    // Manual pagination
                    int startIndex = adminPage * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, adminList.size());
                    java.util.List<com.branch.demo.domain.LienHe> pageContent = 
                        startIndex < adminList.size() ? adminList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
                    
                    model.addAttribute("lienHeChoAdminXuLy", pageContent);
                    
                    // Manual page info
                    int totalPages = (int) Math.ceil((double) adminList.size() / pageSize);
                    model.addAttribute("adminCurrentPage", adminPage);
                    model.addAttribute("adminTotalPages", totalPages);
                    model.addAttribute("adminTotalElements", adminList.size());
                    

                } catch (Exception e) {
                    model.addAttribute("lienHeChoAdminXuLy", new java.util.ArrayList<>());
                    model.addAttribute("error", "Lỗi tải dữ liệu Admin: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Fallback toàn bộ nếu có lỗi
            model.addAttribute("lienHeChuaXuLy", new java.util.ArrayList<>());
            model.addAttribute("lienHeDangXuLy", new java.util.ArrayList<>());
            model.addAttribute("lienHeByModerator", new java.util.ArrayList<>());
            model.addAttribute("lienHeChoAdminXuLy", new java.util.ArrayList<>());
            model.addAttribute("error", "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        
        model.addAttribute("account", account);
        model.addAttribute("activeTab", "lien-he");
        model.addAttribute("title", "Quản Lý Liên Hệ");
        
        // Thêm thông tin để JavaScript biết tab nào đang active
        String activeTabParam = request.getParameter("tab");
        if (activeTabParam != null) {
            model.addAttribute("currentTab", activeTabParam);
        }
        
        return "account/lien-he";
    }
    
    @PostMapping("/lien-he/{id}/bat-dau-xu-ly")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MODERATOR')")
    public String batDauXuLyLienHe(@PathVariable Long id, Authentication authentication, 
                                   RedirectAttributes redirectAttributes) {
        try {
            Account account = accountRepository.findByUsername(authentication.getName()).orElse(null);
            if (account == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/lien-he";
            }
            
            lienHeService.batDauXuLy(id, account.getId());
            redirectAttributes.addFlashAttribute("success", "Đã bắt đầu xử lý liên hệ");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/account/lien-he";
    }
    
    @PostMapping("/lien-he/{id}/xac-nhan-xu-ly")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MODERATOR')")
    public String xacNhanXuLyLienHe(@PathVariable Long id, @RequestParam String ghiChu,
                                    Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Account account = accountRepository.findByUsername(authentication.getName()).orElse(null);
            if (account == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/lien-he";
            }
            
            lienHeService.xacNhanXuLy(id, account.getId(), ghiChu);
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận xử lý liên hệ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/account/lien-he";
    }
    
    @PostMapping("/lien-he/{id}/bao-cao-vi-pham")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MODERATOR')")
    public String baoCaoViPhamLienHe(@PathVariable Long id, @RequestParam String lyDoViPham,
                                     Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Account account = accountRepository.findByUsername(authentication.getName()).orElse(null);
            if (account == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/lien-he";
            }
            
            lienHeService.baoCaoViPham(id, account.getId(), lyDoViPham);
            redirectAttributes.addFlashAttribute("success", "Đã báo cáo vi phạm cho Admin");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/account/lien-he";
    }
    
    @PostMapping("/lien-he/{id}/admin-xu-ly")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String adminXuLyViPham(@PathVariable Long id, @RequestParam String quyetDinh,
                                  Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Account account = accountRepository.findByUsername(authentication.getName()).orElse(null);
            if (account == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/lien-he";
            }
            
            com.branch.demo.domain.LienHe.QuyetDinhAdmin quyetDinhEnum = 
                com.branch.demo.domain.LienHe.QuyetDinhAdmin.valueOf(quyetDinh);
            
            lienHeService.adminXuLyViPham(id, account.getId(), quyetDinhEnum);
            
            if (quyetDinhEnum == com.branch.demo.domain.LienHe.QuyetDinhAdmin.XOA_LIEN_HE) {
                redirectAttributes.addFlashAttribute("success", "Đã xóa liên hệ vi phạm");
            } else {
                redirectAttributes.addFlashAttribute("success", "Đã giữ lại liên hệ");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/account/lien-he";
    }
}