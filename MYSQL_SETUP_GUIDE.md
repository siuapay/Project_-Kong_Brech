# MySQL Setup Guide

## 1. Cài đặt MySQL

### Windows:
1. Tải MySQL từ: https://dev.mysql.com/downloads/mysql/
2. Cài đặt MySQL Server
3. Thiết lập password cho user `root`

### macOS:
```bash
brew install mysql
brew services start mysql
```

### Ubuntu/Linux:
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

## 2. Tạo Database

Đăng nhập vào MySQL:
```bash
mysql -u root -p
```

Tạo database:
```sql
CREATE DATABASE chi_hoi_kong_brech CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Tạo user riêng (tùy chọn):
```sql
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON chi_hoi_kong_brech.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

## 3. Cấu hình Application

### Môi trường Local:
Sử dụng profile `local`:
```bash
java -jar app.jar --spring.profiles.active=local
```

Hoặc trong IDE, set environment variable:
```
SPRING_PROFILES_ACTIVE=local
```

### Cấu hình trong application-local.properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chi_hoi_kong_brech?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

## 4. Môi trường Production (Railway)

### Cấu hình Environment Variables:
```
DATABASE_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=false&serverTimezone=UTC
MYSQL_USER=root
MYSQL_PASSWORD=your_railway_mysql_password
SPRING_PROFILES_ACTIVE=prod
```

## 5. Migration từ PostgreSQL

### Bước 1: Export data từ PostgreSQL
```bash
pg_dump -h your_postgres_host -U your_user -d your_database --data-only --inserts > data_export.sql
```

### Bước 2: Chuyển đổi SQL syntax
- Thay `SERIAL` → `AUTO_INCREMENT`
- Thay `BOOLEAN` → `TINYINT(1)`
- Thay `TEXT` → `LONGTEXT`
- Thay `TIMESTAMP` → `DATETIME`

### Bước 3: Import vào MySQL
```bash
mysql -u root -p chi_hoi_kong_brech < data_export.sql
```

## 6. Kiểm tra kết nối

Chạy application và kiểm tra logs:
```
2024-xx-xx xx:xx:xx.xxx  INFO --- [main] com.zaxxer.hikari.HikariDataSource: HikariPool-1 - Starting...
2024-xx-xx xx:xx:xx.xxx  INFO --- [main] com.zaxxer.hikari.HikariDataSource: HikariPool-1 - Start completed.
```

## 7. Troubleshooting

### Lỗi thường gặp:

1. **Connection refused**
   - Kiểm tra MySQL service đang chạy
   - Kiểm tra port 3306

2. **Access denied**
   - Kiểm tra username/password
   - Kiểm tra user permissions

3. **Database không tồn tại**
   - Thêm `createDatabaseIfNotExist=true` vào URL
   - Hoặc tạo database thủ công

4. **Timezone issues**
   - Thêm `serverTimezone=UTC` vào URL

### Kiểm tra MySQL status:
```bash
# Windows
net start mysql

# macOS/Linux
sudo systemctl status mysql
# hoặc
brew services list | grep mysql
```

## 8. Performance Tuning

### MySQL Configuration (my.cnf):
```ini
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
query_cache_size = 64M
```

### Application Properties:
```properties
# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```