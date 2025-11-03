# 🚀 SETUP CLOUDINARY CHO RAILWAY - HƯỚNG DẪN NHANH

## 📋 TÓM TẮT VẤN ĐỀ
- **Railway xóa tất cả file upload** mỗi khi push code mới
- **Giải pháp**: Upload file lên Cloudinary (cloud storage miễn phí)

## ⚡ SETUP NHANH (5 PHÚT)

### 1. Đăng ký Cloudinary
- Vào: https://cloudinary.com → Sign Up Free
- Lấy 3 thông tin: **Cloud name**, **API Key**, **API Secret**

### 2. Cấu hình Railway
Vào Railway Dashboard → Variables → Add:
```
CLOUDINARY_CLOUD_NAME = your-cloud-name
CLOUDINARY_API_KEY = your-api-key  
CLOUDINARY_API_SECRET = your-api-secret
FILE_STORAGE_TYPE = cloudinary
```

### 3. Deploy
```bash
git add .
git commit -m "Add Cloudinary support"
git push origin main
```

### 4. Test
- Upload 1 ảnh → Push code mới → Ảnh vẫn còn ✅

## 📚 TÀI LIỆU CHI TIẾT

- **`SETUP_CLOUDINARY.md`** - Hướng dẫn từng bước
- **`RAILWAY_FILE_STORAGE_GUIDE.md`** - Giải thích chi tiết + troubleshooting

## 🔧 FILES ĐÃ TẠO

- ✅ `CloudinaryService.java` - Service upload lên cloud
- ✅ `FileUploadService.java` - Đã cập nhật hỗ trợ cloud
- ✅ `application.properties` - Đã thêm config

## 🎯 KẾT QUẢ
- **Development**: File lưu local (như cũ)
- **Production**: File lưu Cloudinary (không mất)
- **Code**: Không cần thay đổi gì thêm

## 🆘 NẾU GẶP VẤN ĐỀ
1. Đọc `SETUP_CLOUDINARY.md`
2. Check Railway logs
3. Verify environment variables