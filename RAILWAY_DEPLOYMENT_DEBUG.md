# 🚀 RAILWAY DEPLOYMENT DEBUG GUIDE

## 🔧 Đã sửa các vấn đề phổ biến:

### 1. **Database Schema Issues** ✅
```properties
# Đã sửa từ validate → update
spring.jpa.hibernate.ddl-auto=update
```

### 2. **PostgreSQL Specific Settings** ✅
```properties
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

### 3. **Profile Configuration** ✅
```properties
spring.profiles.active=railway
```

## 📋 Cách kiểm tra logs trên Railway:

### **Bước 1: Xem Application Logs**
```bash
# Trên Railway Dashboard:
1. Vào project của bạn
2. Click tab "Deployments" 
3. Click vào deployment mới nhất
4. Xem "Build Logs" và "Deploy Logs"
```

### **Bước 2: Các lỗi thường gặp và cách fix:**

#### **Lỗi Connection Timeout:**
```
Caused by: java.net.ConnectException: Connection timed out
```
**Fix:** Railway tự động cung cấp `DATABASE_URL`, không cần config thêm.

#### **Lỗi Schema Validation:**
```
Schema-validation: missing table [table_name]
```
**Fix:** Đã sửa `ddl-auto=update` để tự tạo tables.

#### **Lỗi Port Binding:**
```
Port 8080 is already in use
```
**Fix:** Railway tự động assign port qua `${PORT}` variable.

### **Bước 3: Test Connection**
Sau khi deploy thành công, test các endpoints:
- `https://your-app.railway.app/` - Trang chủ
- `https://your-app.railway.app/admin` - Admin panel
- `https://your-app.railway.app/api/health` - Health check (nếu có)

## 🔍 Debug Commands:

### **Kiểm tra Environment Variables:**
Railway tự động set:
- `DATABASE_URL` - PostgreSQL connection string
- `PORT` - Application port
- `RAILWAY_ENVIRONMENT` - deployment environment

### **Kiểm tra Database Connection:**
```sql
-- Railway PostgreSQL Console
SELECT version();
\dt -- List tables
\d account -- Describe account table
```

## 🚨 Nếu vẫn lỗi:

1. **Copy full error logs** từ Railway Dashboard
2. **Kiểm tra Build Logs** xem có lỗi compile không
3. **Kiểm tra Deploy Logs** xem ứng dụng có start được không
4. **Test local** với PostgreSQL trước khi deploy

## 📞 Cần hỗ trợ:
Gửi **full error logs** từ Railway để tôi có thể debug chính xác!