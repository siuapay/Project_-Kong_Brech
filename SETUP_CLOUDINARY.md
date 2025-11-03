# 🌤️ HƯỚNG DẪN SETUP CLOUDINARY CHO RAILWAY

## ⚠️ VẤN ĐỀ
Khi push code mới lên Railway → **TẤT CẢ HÌNH ẢNH SẼ BỊ MẤT**

## ✅ GIẢI PHÁP: CLOUDINARY (MIỄN PHÍ)

### **BƯỚC 1: Đăng ký Cloudinary**
1. Vào: https://cloudinary.com
2. Click **"Sign Up Free"**
3. Đăng ký với email của bạn
4. Xác nhận email

### **BƯỚC 2: Lấy thông tin cấu hình**
1. Sau khi đăng nhập, vào **Dashboard**
2. Bạn sẽ thấy thông tin như này:
```
Cloud name: your-cloud-name
API Key: 123456789012345  
API Secret: abcdef123456789
```
3. **COPY** 3 thông tin này

### **BƯỚC 3: Cấu hình trên Railway**
1. Vào **Railway Dashboard**: https://railway.app
2. Chọn **project** của bạn
3. Click tab **"Variables"**
4. Click **"New Variable"** và thêm từng cái:

```bash
CLOUDINARY_CLOUD_NAME = your-cloud-name
CLOUDINARY_API_KEY = 123456789012345
CLOUDINARY_API_SECRET = abcdef123456789
FILE_STORAGE_TYPE = cloudinary
```

**⚠️ LƯU Ý:** 
- Thay `your-cloud-name`, `123456789012345`, `abcdef123456789` bằng thông tin thực của bạn
- Không có dấu cách xung quanh dấu `=`

### **BƯỚC 4: Deploy lại**
```bash
git add .
git commit -m "Setup Cloudinary for file storage"
git push origin main
```

### **BƯỚC 5: Test**
1. Đợi Railway deploy xong (2-3 phút)
2. Vào website, thử upload 1 ảnh avatar
3. Push code mới lên git
4. Kiểm tra ảnh có còn không → **SẼ KHÔNG MẤT NỮA!**

## 📊 GIỚI HẠN MIỄN PHÍ
- **Storage**: 25GB (rất nhiều!)
- **Bandwidth**: 25GB/tháng
- **Transformations**: 25,000/tháng
- Đủ cho website nhỏ/vừa

## 🔧 CÁCH HOẠT ĐỘNG
- **Development (local)**: File lưu trong `static/uploads/`
- **Production (Railway)**: File upload lên Cloudinary CDN
- Code tự động detect môi trường

## 🆘 NẾU GẶP LỖI
1. **Kiểm tra biến môi trường** trên Railway
2. **Xem logs**: Railway Dashboard → Deployments → View Logs
3. **Test local**: Set `FILE_STORAGE_TYPE=local` để test

## ✅ SAU KHI SETUP XONG
- ✅ File không bị mất khi deploy
- ✅ Load ảnh nhanh hơn (CDN)
- ✅ Tự động backup
- ✅ Không cần lo về storage