package com.branch.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    private Cloudinary cloudinary;
    private final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @PostConstruct
    public void init() {
        if (!cloudName.isEmpty() && !apiKey.isEmpty() && !apiSecret.isEmpty()) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            ));
            System.out.println("🌤️ Cloudinary initialized successfully!");
        } else {
            System.out.println("⚠️ Cloudinary not configured - missing credentials");
        }
    }
    
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Kiểm tra cấu hình Cloudinary
        if (cloudinary == null) {
            throw new IOException("Cloudinary chưa được cấu hình. Fallback to local storage.");
        }
        
        // Validate file
        validateFile(file);
        
        try {
            // Tạo tên file unique
            String uniqueFilename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            
            // Upload lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", uniqueFilename.substring(0, uniqueFilename.lastIndexOf('.')), // Remove extension
                    "resource_type", "image",
                    "format", getFileExtension(file.getOriginalFilename()).substring(1) // Remove dot
                )
            );
            
            // Lấy URL từ kết quả upload
            String imageUrl = (String) uploadResult.get("secure_url");
            System.out.println("🌤️ Cloudinary upload success: " + imageUrl);
            
            return imageUrl;
            
        } catch (Exception e) {
            System.err.println("❌ Cloudinary upload failed: " + e.getMessage());
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
    
    /**
     * Xóa file trên Cloudinary
     * @param imageUrl URL của ảnh trên Cloudinary
     */
    public void deleteFile(String imageUrl) {
        if (cloudinary == null || imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return; // Không phải file Cloudinary hoặc chưa config
        }
        
        try {
            // Extract public_id from Cloudinary URL
            // URL format: https://res.cloudinary.com/cloud-name/image/upload/v123456/folder/filename.jpg
            String[] parts = imageUrl.split("/");
            if (parts.length >= 2) {
                String filename = parts[parts.length - 1];
                String folder = parts[parts.length - 2];
                String publicId = folder + "/" + filename.substring(0, filename.lastIndexOf('.'));
                
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                System.out.println("🗑️ Cloudinary file deleted: " + publicId);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to delete Cloudinary file: " + e.getMessage());
        }
    }
}