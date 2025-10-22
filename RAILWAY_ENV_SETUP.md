# 🚀 RAILWAY DATABASE SETUP - URGENT FIX

## 🚨 **VẤN ĐỀ HIỆN TẠI:**
Railway chưa có PostgreSQL database → `DATABASE_URL` không tồn tại

## 🔧 **CÁCH SỬA NGAY:**

### **Bước 1: Tạo PostgreSQL Database trên Railway**
1. Vào Railway Dashboard → Your Project
2. Click **"+ New"** → **"Database"** → **"Add PostgreSQL"**
3. Đợi database được tạo (1-2 phút)

### **Bước 2: Connect Database với Service**
1. Sau khi database tạo xong
2. Vào **"Variables"** tab của service
3. Railway sẽ **TỰ ĐỘNG** tạo `DATABASE_URL`

### **Bước 3: Set Environment Variables**
```bash
# Profile (BẮT BUỘC)
SPRING_PROFILES_ACTIVE=production
```

### **Cách 2: Qua Railway CLI**
```bash
railway variables set DATABASE_DRIVER=org.postgresql.Driver
railway variables set DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
railway variables set LOB_NON_CONTEXTUAL=true
railway variables set JDBC_METADATA_DEFAULTS=false
railway variables set SPRING_PROFILES_ACTIVE=production
```

## 📋 **Variables Railway tự động cung cấp:**
- ✅ `DATABASE_URL` - PostgreSQL connection string
- ✅ `PORT` - Application port

## 🔄 **Sau khi set variables:**
1. **Redeploy** project trên Railway
2. **Check logs** để đảm bảo kết nối PostgreSQL thành công
3. **Test application** qua Railway URL

## 🎯 **Expected Result:**
```
INFO: HikariPool-1 - Starting...
INFO: HikariPool-1 - Start completed.
INFO: Started DemoApplication in X.XXX seconds
```

## 🚨 **Nếu vẫn lỗi:**
Gửi **full logs** từ Railway Dashboard để debug tiếp!