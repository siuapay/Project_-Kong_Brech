package com.branch.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CloudinaryService {
    
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    private final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Kiểm tra cấu hình Cloudinary
        if (cloudName.isEmpty() || apiKey.isEmpty() || apiSecret.isEmpty()) {
            throw new IOException("Cloudinary chưa được cấu hình. Vui lòng thêm environment variables.");
        }
        
        // Validate file
        validateFile(file);
        
        try {
            // TODO: Implement actual Cloudinary upload
            // Hiện tại return mock URL để test
            String uniqueFilename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            return "https://res.cloudinary.com/" + cloudName + "/image/upload/" + folder + "/" + uniqueFilename;
        } catch (Exception e) {
            throw new IOException("Lỗi upload file lên Cloudinary: " + e.getMessage());
        }
    }
    
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File không được để trống");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File quá lớn. Kích thước tối đa là 5MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Tên file không hợp lệ");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedExtension(fileExtension)) {
            throw new IOException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: jpg, jpeg, png, gif");
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex).toLowerCase() : "";
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}