package com.branch.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        String errorCode = "500";
        String title = "Lỗi hệ thống";
        String message = "Đã xảy ra lỗi không mong muốn";
        String description = "Vui lòng thử lại sau hoặc liên hệ với quản trị viên";
        String icon = "fas fa-exclamation-triangle";
        String buttonText = "Về trang chủ";
        String buttonLink = "/";
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            errorCode = String.valueOf(statusCode);
            
            switch (statusCode) {
                case 400:
                    title = "Yêu cầu không hợp lệ";
                    message = "Dữ liệu gửi lên không đúng định dạng";
                    description = "Vui lòng kiểm tra lại thông tin và thử lại";
                    icon = "fas fa-exclamation-circle";
                    break;
                    
                case 401:
                    title = "Chưa đăng nhập";
                    message = "Bạn cần đăng nhập để truy cập trang này";
                    description = "Vui lòng đăng nhập để tiếp tục";
                    icon = "fas fa-lock";
                    buttonText = "Đăng nhập";
                    buttonLink = "/login";
                    break;
                    
                case 403:
                    title = "Không có quyền truy cập";
                    message = "Bạn không có quyền truy cập vào trang này";
                    description = "Vui lòng liên hệ quản trị viên nếu bạn cho rằng đây là lỗi";
                    icon = "fas fa-ban";
                    break;
                    
                case 404:
                    title = "Không tìm thấy trang";
                    message = "Trang bạn đang tìm kiếm không tồn tại";
                    description = "URL có thể đã thay đổi hoặc trang đã bị xóa";
                    icon = "fas fa-search";
                    buttonText = "Về trang chủ";
                    buttonLink = "/";
                    break;
                    
                case 405:
                    title = "Phương thức không được hỗ trợ";
                    message = "Phương thức HTTP không được phép cho trang này";
                    description = "Vui lòng kiểm tra lại cách truy cập";
                    icon = "fas fa-times-circle";
                    break;
                    
                case 413:
                    title = "Dữ liệu quá lớn";
                    message = "File hoặc dữ liệu bạn tải lên quá lớn";
                    description = "Vui lòng giảm kích thước file và thử lại";
                    icon = "fas fa-file-upload";
                    buttonText = "Thử lại";
                    buttonLink = "javascript:history.back()";
                    break;
                    
                case 500:
                    title = "Lỗi máy chủ";
                    message = "Đã xảy ra lỗi trong quá trình xử lý";
                    description = "Chúng tôi đang khắc phục sự cố. Vui lòng thử lại sau";
                    icon = "fas fa-server";
                    break;
                    
                case 502:
                    title = "Lỗi kết nối";
                    message = "Không thể kết nối đến máy chủ";
                    description = "Vui lòng thử lại sau ít phút";
                    icon = "fas fa-plug";
                    break;
                    
                case 503:
                    title = "Dịch vụ không khả dụng";
                    message = "Hệ thống đang bảo trì";
                    description = "Vui lòng quay lại sau";
                    icon = "fas fa-tools";
                    break;
                    
                default:
                    title = "Lỗi " + statusCode;
                    message = "Đã xảy ra lỗi không xác định";
                    break;
            }
        }
        
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("title", title);
        model.addAttribute("message", message);
        model.addAttribute("description", description);
        model.addAttribute("icon", icon);
        model.addAttribute("buttonText", buttonText);
        model.addAttribute("buttonLink", buttonLink);
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("exception", exception);
        
        return "error/error";
    }
}