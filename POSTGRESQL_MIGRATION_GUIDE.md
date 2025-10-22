# PostgreSQL Migration Guide

## 🔄 Các thay đổi cần thiết khi migrate từ SQL Server sang PostgreSQL

### ✅ ĐÃ SỬA XONG:

1. **NVARCHAR(MAX) → TEXT**
   - `ThongBao.noiDung`
   - `SuKien.noiDung`  
   - `LienHe.noiDung`, `ghiChuXuLy`, `lyDoViPham`
   - `BaiViet.noiDung`, `noiDungRich`

2. **Native SQL Queries**
   - `TaiChinhNamRepository.findTop5RecentYearsWithData()` - Đã chuyển sang JPQL + Pageable
   - `TaiChinhNamRepository.findTop10RecentYearsWithData()` - Đã chuyển sang JPQL + Pageable

### 🔧 CẦN SỬA THỦ CÔNG:

#### **YEAR() và MONTH() Functions:**

**Tìm và thay thế trong các file sau:**

1. **TinHuuRepository.java:**
   ```java
   // CŨ (SQL Server):
   YEAR(CURRENT_DATE) - t.namSinh
   MONTH(t.ngayBaoTin)
   
   // MỚI (PostgreSQL):
   EXTRACT(YEAR FROM CURRENT_DATE) - t.namSinh
   EXTRACT(MONTH FROM t.ngayBaoTin)
   ```

2. **TaiChinhRepository.java:**
   ```java
   // CŨ:
   YEAR(tc.ngayGiaoDich) = :nam
   MONTH(tc.ngayGiaoDich) = :thang
   
   // MỚI:
   EXTRACT(YEAR FROM tc.ngayGiaoDich) = :nam
   EXTRACT(MONTH FROM tc.ngayGiaoDich) = :thang
   ```

3. **TaiChinhGiaoDichRepository.java:**
   ```java
   // CŨ:
   YEAR(gd.thoiGian) = :nam
   MONTH(gd.thoiGian)
   
   // MỚI:
   EXTRACT(YEAR FROM gd.thoiGian) = :nam
   EXTRACT(MONTH FROM gd.thoiGian)
   ```

4. **SuKienRepository.java:**
   ```java
   // CŨ:
   YEAR(sk.ngayDienRa) = :year
   MONTH(sk.ngayDienRa) = :month
   
   // MỚI:
   EXTRACT(YEAR FROM sk.ngayDienRa) = :year
   EXTRACT(MONTH FROM sk.ngayDienRa) = :month
   ```

5. **LienHeRepository.java:**
   ```java
   // CŨ:
   YEAR(lh.createdAt), MONTH(lh.createdAt)
   
   // MỚI:
   EXTRACT(YEAR FROM lh.createdAt), EXTRACT(MONTH FROM lh.createdAt)
   ```

### 🔍 CÁCH TÌM VÀ THAY THẾ NHANH:

**Trong IDE (IntelliJ/VSCode):**
1. Mở "Find and Replace" (Ctrl+Shift+R)
2. Enable "Regex" mode
3. Tìm: `YEAR\(([^)]+)\)`
4. Thay: `EXTRACT(YEAR FROM $1)`
5. Tìm: `MONTH\(([^)]+)\)`
6. Thay: `EXTRACT(MONTH FROM $1)`

### 📋 CHECKLIST MIGRATION:

- [x] Cập nhật `application-railway.properties`
- [x] Thêm PostgreSQL dependency trong `pom.xml`
- [x] Sửa `NVARCHAR(MAX)` → `TEXT`
- [x] Sửa native SQL queries với `LIMIT`
- [ ] **Thay thế tất cả `YEAR()` → `EXTRACT(YEAR FROM ...)`**
- [ ] **Thay thế tất cả `MONTH()` → `EXTRACT(MONTH FROM ...)`**
- [ ] Test application với PostgreSQL local
- [ ] Export data từ SQL Server
- [ ] Import data vào PostgreSQL
- [ ] Deploy lên Railway

### 🚀 DEPLOY LÊN RAILWAY:

1. **Push code lên GitHub**
2. **Connect Railway với GitHub repo**
3. **Add PostgreSQL database service**
4. **Set environment variable: `SPRING_PROFILES_ACTIVE=railway`**
5. **Deploy tự động**

### 💡 LƯU Ý:

- **95% code sẽ tự động work** với PostgreSQL
- Chỉ cần sửa các SQL functions cụ thể
- JPA/Hibernate sẽ tự động handle data types
- Backup data trước khi migrate

---

**Sau khi sửa xong tất cả YEAR() và MONTH(), project sẽ 100% compatible với PostgreSQL!**