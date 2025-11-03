# 📁 HƯỚNG DẪN CHI TIẾT: GIẢI QUYẾT VẤN ĐỀ MẤT FILE TRÊN RAILWAY

## 🚨 VẤN ĐỀ HIỆN TẠI

### Điều gì xảy ra khi bạn push code mới?
```
1. Bạn upload ảnh avatar cho tín hữu → Lưu trong static/uploads/avatar/
2. Database lưu đường dẫn: /static/uploads/avatar/abc.jpg
3. Ảnh hiển thị bình thường ✅

4. Bạn sửa code và push lên git
5. Railway tự động build lại container mới
6. TẤT CẢ FILE TRONG static/uploads/ BỊ XÓA 💥
7. Ảnh không hiển thị nữa ❌
```

### Tại sao lại như vậy?
- Railway sử dụng **"ephemeral filesystem"**
- Mỗi lần deploy = container mới = file system mới
- Chỉ có code được giữ lại, file upload bị mất

## ✅ GIẢI PHÁP ĐÃ TRIỂN KHAI

### Hệ thống Dual Storage:
```
Development (Local):
├── File lưu: src/main/resources/static/uploads/
├── URL: /static/uploads/avatar/abc.jpg
└── Phù hợp cho dev/test

Production (Railway):
├── File lưu: Cloudinary CDN
├── URL: https://res.cloudinary.com/.../abc.jpg  
└── Không bao giờ mất file
```

### Code tự động detect:
```java
// Nếu có CLOUDINARY config → Upload lên cloud
if ("cloudinary".equals(storageType)) {
    return cloudinaryService.uploadFile(file, folder);
}
// Nếu không → Sử dụng local storage
else {
    return uploadFileLocal(file, folder);
}
```

## 🛠️ HƯỚNG DẪN SETUP CHI TIẾT

### **BƯỚC 1: Đăng ký Cloudinary**

1. **Truy cập**: https://cloudinary.com
2. **Click**: "Sign Up Free"
3. **Điền thông tin**:
   - Email: your-email@gmail.com
   - Password: your-password
   - Company: Your Company (có thể để tên bất kỳ)
4. **Verify email** và đăng nhập

### **BƯỚC 2: Lấy API credentials**

1. Sau khi đăng nhập, bạn sẽ thấy **Dashboard**
2. Phần **"Account Details"** có thông tin:
   ```
   Cloud name: your-cloud-name
   API Key: 123456789012345
   API Secret: abcdef123456789 (click "Reveal" để xem)
   ```
3. **COPY** cả 3 thông tin này

### **BƯỚC 3: Cấu hình Railway Environment Variables**

1. **Vào Railway Dashboard**: https://railway.app
2. **Chọn project** của bạn
3. **Click tab "Variables"** (bên cạnh Deployments)
4. **Click "New Variable"** và thêm từng biến:

   **Biến 1:**
   ```
   Name: CLOUDINARY_CLOUD_NAME
   Value: your-cloud-name
   ```
   
   **Biến 2:**
   ```
   Name: CLOUDINARY_API_KEY  
   Value: 123456789012345
   ```
   
   **Biến 3:**
   ```
   Name: CLOUDINARY_API_SECRET
   Value: abcdef123456789
   ```
   
   **Biến 4:**
   ```
   Name: FILE_STORAGE_TYPE
   Value: cloudinary
   ```

5. **Click "Add"** cho mỗi biến

### **BƯỚC 4: Deploy code**

```bash
# Commit và push code hiện tại
git add .
git commit -m "Add Cloudinary support for file storage"
git push origin main
```

Railway sẽ tự động deploy (2-3 phút)

### **BƯỚC 5: Test thử**

1. **Đợi deploy xong** (check Railway dashboard)
2. **Vào website**, thử upload 1 ảnh avatar
3. **Kiểm tra**: Ảnh có hiển thị không?
4. **Push code mới** bất kỳ lên git
5. **Kiểm tra lại**: Ảnh vẫn còn → **THÀNH CÔNG!** ✅

## 🔍 TROUBLESHOOTING

### Lỗi thường gặp:

**1. "Cloudinary chưa được cấu hình"**
- ✅ Kiểm tra lại environment variables trên Railway
- ✅ Đảm bảo không có dấu cách thừa
- ✅ Restart deployment

**2. Ảnh không upload được**
- ✅ Check Railway logs: Deployments → View Logs
- ✅ Kiểm tra file size < 5MB
- ✅ Kiểm tra định dạng: jpg, png, gif

**3. Ảnh vẫn bị mất**
- ✅ Kiểm tra `FILE_STORAGE_TYPE=cloudinary`
- ✅ Xem logs có lỗi gì không
- ✅ Test upload mới sau khi setup

### Debug commands:
```bash
# Xem environment variables
railway variables

# Xem logs realtime  
railway logs --follow
```

## 📊 CLOUDINARY FREE TIER

- **Storage**: 25GB
- **Bandwidth**: 25GB/tháng
- **Transformations**: 25,000/tháng
- **API calls**: Unlimited
- **CDN**: Global (load nhanh)

→ **Đủ cho website nhỏ/vừa trong nhiều năm!**

## 🎯 KẾT QUẢ SAU KHI SETUP

### Trước khi setup:
```
Upload ảnh → Lưu local → Push code → ❌ Ảnh mất
```

### Sau khi setup:
```
Upload ảnh → Lưu Cloudinary → Push code → ✅ Ảnh vẫn còn
```

### Bonus benefits:
- ✅ **CDN toàn cầu**: Load ảnh nhanh hơn
- ✅ **Auto backup**: Không lo mất dữ liệu  
- ✅ **Auto optimize**: Tự động nén ảnh
- ✅ **Scalable**: Không giới hạn traffic

## 📞 HỖ TRỢ

Nếu gặp vấn đề:
1. **Check file**: `SETUP_CLOUDINARY.md` (hướng dẫn ngắn gọn)
2. **Railway logs**: Xem lỗi cụ thể
3. **Cloudinary dashboard**: Kiểm tra upload có thành công không
4. **Test local**: Set `FILE_STORAGE_TYPE=local` để test code