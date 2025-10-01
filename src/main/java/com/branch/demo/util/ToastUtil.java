package com.branch.demo.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Utility class để hỗ trợ Toast Notifications
 */
public class ToastUtil {
    
    /**
     * Thêm thông báo thành công
     */
    public static void addSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("success", message);
    }
    
    /**
     * Thêm thông báo thành công với title tùy chỉnh
     */
    public static void addSuccess(RedirectAttributes redirectAttributes, String title, String message) {
        redirectAttributes.addFlashAttribute("successTitle", title);
        redirectAttributes.addFlashAttribute("successMessage", message);
    }
    
    /**
     * Thêm thông báo lỗi
     */
    public static void addError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("error", message);
    }
    
    /**
     * Thêm thông báo lỗi với title tùy chỉnh
     */
    public static void addError(RedirectAttributes redirectAttributes, String title, String message) {
        redirectAttributes.addFlashAttribute("errorTitle", title);
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }
    
    /**
     * Thêm thông báo cảnh báo
     */
    public static void addWarning(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("warning", message);
    }
    
    /**
     * Thêm thông báo cảnh báo với title tùy chỉnh
     */
    public static void addWarning(RedirectAttributes redirectAttributes, String title, String message) {
        redirectAttributes.addFlashAttribute("warningTitle", title);
        redirectAttributes.addFlashAttribute("warningMessage", message);
    }
    
    /**
     * Thêm thông báo thông tin
     */
    public static void addInfo(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("info", message);
    }
    
    /**
     * Thêm thông báo thông tin với title tùy chỉnh
     */
    public static void addInfo(RedirectAttributes redirectAttributes, String title, String message) {
        redirectAttributes.addFlashAttribute("infoTitle", title);
        redirectAttributes.addFlashAttribute("infoMessage", message);
    }
    
    /**
     * Thêm thông báo cập nhật profile thành công
     */
    public static void addProfileUpdateSuccess(RedirectAttributes redirectAttributes) {
        addSuccess(redirectAttributes, "Cập nhật thành công", "Thông tin cá nhân đã được cập nhật thành công!");
    }
    
    /**
     * Thêm thông báo đổi mật khẩu thành công
     */
    public static void addPasswordChangeSuccess(RedirectAttributes redirectAttributes) {
        addSuccess(redirectAttributes, "Đổi mật khẩu thành công", "Mật khẩu đã được thay đổi. Vui lòng đăng nhập lại!");
    }
    
    /**
     * Thêm thông báo đăng bài thành công
     */
    public static void addPostCreateSuccess(RedirectAttributes redirectAttributes, Long postId) {
        addSuccess(redirectAttributes, "Đăng bài thành công", 
            "Bài viết ID: " + postId + " đã được tạo và đang chờ admin duyệt.");
    }
    
    /**
     * Thêm thông báo cập nhật bài viết thành công
     */
    public static void addPostUpdateSuccess(RedirectAttributes redirectAttributes) {
        addSuccess(redirectAttributes, "Cập nhật bài viết thành công", 
            "Bài viết đã được cập nhật và sẽ được gửi lại để admin duyệt.");
    }
    
    /**
     * Thêm thông báo xóa bài viết thành công
     */
    public static void addPostDeleteSuccess(RedirectAttributes redirectAttributes) {
        addSuccess(redirectAttributes, "Xóa bài viết thành công", "Bài viết đã được xóa khỏi hệ thống.");
    }
    
    /**
     * Thêm thông báo lỗi không có quyền
     */
    public static void addPermissionError(RedirectAttributes redirectAttributes) {
        addError(redirectAttributes, "Không có quyền", "Bạn không có quyền thực hiện thao tác này.");
    }
    
    /**
     * Thêm thông báo lỗi không tìm thấy
     */
    public static void addNotFoundError(RedirectAttributes redirectAttributes, String item) {
        addError(redirectAttributes, "Không tìm thấy", "Không tìm thấy " + item + " được yêu cầu.");
    }
    
    /**
     * Thêm thông báo lỗi validation
     */
    public static void addValidationError(RedirectAttributes redirectAttributes, String message) {
        addError(redirectAttributes, "Dữ liệu không hợp lệ", message);
    }
    
    /**
     * Thêm thông báo lỗi hệ thống
     */
    public static void addSystemError(RedirectAttributes redirectAttributes) {
        addError(redirectAttributes, "Lỗi hệ thống", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau.");
    }
    
    /**
     * Thêm thông báo lỗi hệ thống với chi tiết
     */
    public static void addSystemError(RedirectAttributes redirectAttributes, String details) {
        addError(redirectAttributes, "Lỗi hệ thống", "Có lỗi xảy ra: " + details);
    }
}