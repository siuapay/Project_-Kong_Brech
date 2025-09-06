# 🖼️ Hướng Dẫn Sử Dụng Tính Năng Avatar

## ✨ Tính Năng Hoàn Thành

### 🎯 **Avatar Upload System**

- ✅ Upload ảnh đại diện cho tin hữu
- ✅ Preview ảnh trước khi lưu
- ✅ Validation file size và format
- ✅ Tự động tạo thư mục uploads
- ✅ Xóa ảnh cũ khi cập nhật
- ✅ Default avatar cho tin hữu chưa có ảnh

### 📋 **Thông Số Kỹ Thuật**

- **Định dạng hỗ trợ**: JPG, JPEG, PNG, GIF
- **Kích thước tối đa**: 5MB
- **Thư mục lưu trữ**: `src/main/resources/static/uploads/avatars/`
- **URL truy cập**: `/uploads/avatars/filename.ext`

### 🚀 **Cách Sử Dụng**

#### 1. **Thêm Avatar Khi Tạo Tin Hữu Mới**

1. Truy cập: `http://localhost:8080/admin/tin-huu/new`
2. Trong phần "Ảnh Đại Diện", click "Chọn ảnh mới"
3. Chọn file ảnh từ máy tính
4. Xem preview ảnh ngay lập tức
5. Điền thông tin khác và nhấn "Lưu"

#### 2. **Cập Nhật Avatar Cho Tin Hữu Hiện Có**

1. Truy cập danh sách: `http://localhost:8080/admin/tin-huu`
2. Click "Sửa" trên tin hữu cần cập nhật
3. Trong phần "Ảnh Đại Diện", chọn ảnh mới
4. Ảnh cũ sẽ được thay thế tự động
5. Nhấn "Cập Nhật" để lưu

#### 3. **Xem Avatar**

- **Danh sách**: Avatar hiển thị 40x40px bên cạnh tên
- **Chi tiết**: Avatar hiển thị 120x120px trong profile card
- **Form**: Avatar hiển thị 120x120px với preview

### 🔧 **Cấu Hình Hệ Thống**

#### **File Upload Configuration** (`application.properties`)

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
spring.servlet.multipart.file-size-threshold=2KB
```

#### **Static Resource Mapping** (`WebConfig.java`)

```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:src/main/resources/static/uploads/");
```

### 🛡️ **Bảo Mật & Validation**

#### **Client-side Validation**

- Kiểm tra file size (5MB)
- Kiểm tra định dạng file
- Preview trước khi upload

#### **Server-side Validation**

- Validate file không rỗng
- Kiểm tra kích thước file
- Kiểm tra extension hợp lệ
- Tạo tên file unique với UUID

### 📁 **Cấu Trúc Thư Mục**

```
src/main/resources/static/
├── images/
│   └── default-avatar.svg          # Avatar mặc định
└── uploads/
    └── avatars/                    # Thư mục lưu avatar
        ├── uuid1.jpg
        ├── uuid2.png
        └── ...
```

### 🎨 **Giao Diện**

#### **Form Upload**

- Avatar preview 120x120px
- File input với accept filter
- Upload status indicator
- Validation messages

#### **List View**

- Avatar 40x40px tròn
- Fallback to default avatar
- Responsive design

#### **Detail View**

- Avatar 120x120px với shadow
- Professional profile layout

### 🔄 **Quy Trình Xử Lý**

1. **Upload**: User chọn file → Client validation → Preview
2. **Submit**: Form submit → Server validation → Save file → Update DB
3. **Display**: Load từ DB → Render với fallback

### 🚨 **Xử Lý Lỗi**

#### **Client-side Errors**

- File quá lớn: Alert + reset input
- Định dạng không hỗ trợ: Alert + reset input
- Không có file: Reset về avatar gốc

#### **Server-side Errors**

- IOException: Flash message error
- Validation failed: Redirect với error message

### 🎯 **Tính Năng Nâng Cao**

#### **Đã Implement**

- ✅ Auto-delete old avatar khi update
- ✅ UUID filename để tránh conflict
- ✅ Responsive image display
- ✅ Fallback to default avatar

#### **Có Thể Mở Rộng**

- 🔄 Image resizing/compression
- 🔄 Multiple image formats
- 🔄 Cloud storage integration
- 🔄 Image cropping tool

### 📊 **Test Cases**

#### **Positive Cases**

- ✅ Upload JPG file < 5MB
- ✅ Upload PNG file < 5MB
- ✅ Update existing avatar
- ✅ Display default avatar

#### **Negative Cases**

- ✅ File > 5MB → Error message
- ✅ Invalid format → Error message
- ✅ Empty file → Error message
- ✅ Network error → Graceful handling

### 🎉 **Kết Quả**

Hệ thống avatar đã hoàn thành với đầy đủ tính năng:

- **Upload**: Smooth và user-friendly
- **Display**: Professional và responsive
- **Security**: Validated và safe
- **Performance**: Optimized file handling

**URL để test**: `http://localhost:8080/admin/tin-huu`
