package com.branch.demo.config;

import com.branch.demo.domain.Account;
import com.branch.demo.domain.NhanSu;
import com.branch.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private AccountRepository accountRepository;

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getName());
    }

    @ModelAttribute("currentUser")
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return null;
    }

    @ModelAttribute("currentAccount")
    public Account currentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            Optional<Account> accountOpt = accountRepository.findByUsername(authentication.getName());
            return accountOpt.orElse(null);
        }
        return null;
    }

    @ModelAttribute("currentNhanSu")
    public NhanSu currentNhanSu() {
        Account account = currentAccount();
        return account != null ? account.getNhanSu() : null;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("title", "Không tìm thấy trang");
        model.addAttribute("message", "Trang bạn đang tìm kiếm không tồn tại");
        model.addAttribute("description", "URL có thể đã thay đổi hoặc trang đã bị xóa");
        model.addAttribute("icon", "fas fa-search");
        model.addAttribute("buttonText", "Về trang chủ");
        model.addAttribute("buttonLink", "/");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "403");
        model.addAttribute("title", "Không có quyền truy cập");
        model.addAttribute("message", "Bạn không có quyền truy cập vào trang này");
        model.addAttribute("description", "Vui lòng liên hệ quản trị viên nếu bạn cho rằng đây là lỗi");
        model.addAttribute("icon", "fas fa-ban");
        model.addAttribute("buttonText", "Về trang chủ");
        model.addAttribute("buttonLink", "/");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "413");
        model.addAttribute("title", "File quá lớn");
        model.addAttribute("message", "File bạn tải lên vượt quá giới hạn cho phép");
        model.addAttribute("description", "Vui lòng chọn file nhỏ hơn và thử lại");
        model.addAttribute("icon", "fas fa-file-upload");
        model.addAttribute("buttonText", "Thử lại");
        model.addAttribute("buttonLink", "javascript:history.back()");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFileNotFound(FileNotFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("title", "Không tìm thấy file");
        model.addAttribute("message", "File bạn đang tìm kiếm không tồn tại");
        model.addAttribute("description", "File có thể đã bị xóa hoặc di chuyển");
        model.addAttribute("icon", "fas fa-file-times");
        model.addAttribute("buttonText", "Về trang chủ");
        model.addAttribute("buttonLink", "/");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseError(DataAccessException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("title", "Lỗi cơ sở dữ liệu");
        model.addAttribute("message", "Không thể kết nối đến cơ sở dữ liệu");
        model.addAttribute("description", "Vui lòng thử lại sau hoặc liên hệ quản trị viên");
        model.addAttribute("icon", "fas fa-database");
        model.addAttribute("buttonText", "Thử lại");
        model.addAttribute("buttonLink", "javascript:location.reload()");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("title", "Lỗi hệ thống");
        model.addAttribute("message", "Đã xảy ra lỗi không mong muốn");
        model.addAttribute("description", "Chúng tôi đang khắc phục sự cố. Vui lòng thử lại sau");
        model.addAttribute("icon", "fas fa-exclamation-triangle");
        model.addAttribute("buttonText", "Về trang chủ");
        model.addAttribute("buttonLink", "/");
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("title", "Lỗi không xác định");
        model.addAttribute("message", "Đã xảy ra lỗi không mong muốn");
        model.addAttribute("description", "Vui lòng thử lại sau hoặc liên hệ quản trị viên");
        model.addAttribute("icon", "fas fa-bug");
        model.addAttribute("buttonText", "Về trang chủ");
        model.addAttribute("buttonLink", "/");
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }
}