package com.branch.demo.controller;

import com.branch.demo.domain.Account;
import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.repository.AccountRepository;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.DanhMucRepository;
import com.branch.demo.repository.NhanSuRepository;
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
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @Autowired
    private com.branch.demo.service.FileUploadService fileUploadService;

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
        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu()); // Thêm thông tin nhân sự
        model.addAttribute("activeTab", "profile");

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
        model.addAttribute("nhanSu",
                account.getNhanSu() != null ? account.getNhanSu() : new com.branch.demo.domain.NhanSu());
        model.addAttribute("activeTab", "profile");

        return "account/profile-edit";
    }

    // Xử lý cập nhật profile
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute com.branch.demo.domain.NhanSu nhanSu,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);

            if (accountOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/profile";
            }

            Account account = accountOpt.get();

            // Nếu chưa có NhanSu, tạo mới
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

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/account/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
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
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài viết");
            return "redirect:/account/my-posts";
        }

        Account account = accountOpt.get();
        BaiViet baiViet = baiVietOpt.get();

        // Kiểm tra quyền sở hữu
        if (!baiViet.getTacGia().equals(account.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền chỉnh sửa bài viết này");
            return "redirect:/account/my-posts";
        }

        // Chỉ cho phép chỉnh sửa bài viết ở trạng thái NHAP hoặc TU_CHOI
        if (baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.NHAP &&
                baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.TU_CHOI) {
            redirectAttributes.addFlashAttribute("error",
                    "Chỉ có thể chỉnh sửa bài viết ở trạng thái nháp hoặc bị từ chối");
            return "redirect:/account/my-posts";
        }

        model.addAttribute("account", account);
        model.addAttribute("nhanSu", account.getNhanSu());
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
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật bài viết");
                return "redirect:/account/my-posts";
            }

            Account account = accountOpt.get();
            DanhMuc danhMuc = danhMucOpt.get();
            BaiViet existingBaiViet = existingBaiVietOpt.get();

            // Kiểm tra quyền sở hữu
            if (!existingBaiViet.getTacGia().equals(account.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền chỉnh sửa bài viết này");
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

            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật bài viết thành công! Bài viết sẽ được gửi lại để admin duyệt.");

            return "redirect:/account/my-posts";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài viết");
                return "redirect:/account/my-posts";
            }

            Account account = accountOpt.get();
            BaiViet baiViet = baiVietOpt.get();

            // Kiểm tra quyền sở hữu
            if (!baiViet.getTacGia().equals(account.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa bài viết này");
                return "redirect:/account/my-posts";
            }

            // Chỉ cho phép xóa bài viết ở trạng thái NHAP hoặc TU_CHOI
            if (baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.NHAP &&
                    baiViet.getTrangThai() != BaiViet.TrangThaiBaiViet.TU_CHOI) {
                redirectAttributes.addFlashAttribute("error",
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

            redirectAttributes.addFlashAttribute("success", "Đã xóa bài viết thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa bài viết: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng bài");
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

            redirectAttributes.addFlashAttribute("success",
                    "Đăng bài thành công! ID: " + savedBaiViet.getId() + ". Bài viết đang chờ admin duyệt.");

            return "redirect:/account/my-posts";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
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
        model.addAttribute("activeTab", "security");

        return "account/security";
    }

    // Đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            String username = auth.getName();
            Optional<Account> accountOpt = accountRepository.findByUsername(username);

            if (accountOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản");
                return "redirect:/account/security";
            }

            Account account = accountOpt.get();

            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
                return "redirect:/account/security";
            }

            // Kiểm tra mật khẩu mới
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
                return "redirect:/account/security";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
                return "redirect:/account/security";
            }

            // Cập nhật mật khẩu
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
            return "redirect:/account/security";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
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
}