package com.branch.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Value("${file.storage.type:local}")
    private String storageType;
    
    @Autowired(required = false)
    private CloudinaryService cloudinaryService;
    
    public String uploadFile(MultipartFile file, String subDirectory) throws IOException {
        // Nếu cấu hình cloud storage, sử dụng Cloudinary
        if ("cloudinary".equalsIgnoreCase(storageType) && cloudinaryService != null) {
            try {
                return cloudinaryService.uploadFile(file, subDirectory);
            } catch (Exception e) {
                System.err.println("Cloudinary upload failed, fallback to local: " + e.getMessage());
                // Fallback to local storage if cloud fails
            }
        }
        
        // Sử dụng local storage (mặc định)
        return uploadFileLocal(file, subDirectory);
    }
    
    private String uploadFileLocal(MultipartFile file, String subDirectory) throws IOException {
        // Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            throw new IOException("File không được để trống");
        }
        
        // Kiểm tra kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File quá lớn. Kích thước tối đa là 5MB");
        }
        
        // Kiểm tra định dạng file
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Tên file không hợp lệ");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedExtension(fileExtension)) {
            throw new IOException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: jpg, jpeg, png, gif");
        }
        
        // Tạo thư mục nếu chưa tồn tại
        createUploadDirectoryIfNotExists(subDirectory);
        
        // Tạo tên file unique
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Đường dẫn lưu file
        Path uploadPath = Paths.get(UPLOAD_DIR + subDirectory + "/" + uniqueFilename);
        
        // Lưu file
        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Trả về đường dẫn relative để lưu vào database
        return "/uploads/" + subDirectory + "/" + uniqueFilename;
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        return uploadFile(file, "avatars");
    }
    public String uploadImage(MultipartFile file) throws IOException {
    return uploadFile(file, "images");
}
    
    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                // Chuyển từ URL thành đường dẫn file
                String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR + "avatars/" + filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Không thể xóa file avatar: " + e.getMessage());
            }
        }
    }
    
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Chuyển từ URL thành đường dẫn file
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                // Determine subdirectory from URL path
                String subDirectory = "images"; // default
                if (imageUrl.contains("/uploads/")) {
                    String pathPart = imageUrl.substring(imageUrl.indexOf("/uploads/") + 9);
                    if (pathPart.contains("/")) {
                        subDirectory = pathPart.substring(0, pathPart.lastIndexOf("/"));
                    }
                }
                Path filePath = Paths.get(UPLOAD_DIR + subDirectory + "/" + filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Không thể xóa file image: " + e.getMessage());
            }
        }
    }
    
    private void createUploadDirectoryIfNotExists(String subDirectory) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR + subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Đã tạo thư mục upload: " + uploadPath.toAbsolutePath());
        }
    }
    
    private void createUploadDirectoryIfNotExists() throws IOException {
        createUploadDirectoryIfNotExists("avatars");
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}