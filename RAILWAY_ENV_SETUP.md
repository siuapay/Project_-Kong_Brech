# 🚀 RAILWAY ENVIRONMENT VARIABLES SETUP

## 🔧 **BẮT BUỘC: Set các Environment Variables sau trên Railway:**

### **Cách 1: Qua Railway Dashboard**
1. Vào Railway Dashboard → Your Project
2. Click tab **"Variables"**
3. Thêm các variables sau:

```bash
# Database Configuration
DATABASE_DRIVER=org.postgresql.Driver
DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
LOB_NON_CONTEXTUAL=true
JDBC_METADATA_DEFAULTS=false

# Profile
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