# MySQL Query Conversion Summary

## Các thay đổi đã thực hiện để chuyển đổi từ PostgreSQL sang MySQL

### 1. TaiChinhGiaoDichRepository.java

#### Đã chuyển đổi:
- `EXTRACT(YEAR FROM ...)` → `YEAR(...)`
- `EXTRACT(MONTH FROM ...)` → `MONTH(...)`

#### Chi tiết các query đã sửa:

1. **getTongByDanhMucAndNam**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian) = :nam`
   - Sau: `YEAR(gd.thoiGian) = :nam`

2. **getTongByDanhMucLoaiAndNam**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian) = :nam`
   - Sau: `YEAR(gd.thoiGian) = :nam`

3. **findDistinctYears**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian)`
   - Sau: `YEAR(gd.thoiGian)`

4. **countByLoaiAndNam**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian) = :nam`
   - Sau: `YEAR(gd.thoiGian) = :nam`

5. **getThongKeTheoThang**
   - Trước: `EXTRACT(MONTH FROM gd.thoiGian)` và `EXTRACT(YEAR FROM gd.thoiGian)`
   - Sau: `MONTH(gd.thoiGian)` và `YEAR(gd.thoiGian)`

6. **getThongKeTheoDanhMuc**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian) = :nam`
   - Sau: `YEAR(gd.thoiGian) = :nam`

7. **countByNam**
   - Trước: `EXTRACT(YEAR FROM gd.thoiGian) = :nam`
   - Sau: `YEAR(gd.thoiGian) = :nam`

### 2. SuKienRepository.java

#### Đã chuyển đổi:
- `CAST(...AS DATE)` → `DATE(...)`

#### Chi tiết các query đã sửa:

1. **searchSuKien**
   - Trước: `CAST(sk.ngayDienRa AS DATE) >= :fromDate`
   - Sau: `DATE(sk.ngayDienRa) >= :fromDate`
   - Trước: `CAST(sk.ngayDienRa AS DATE) <= :toDate`
   - Sau: `DATE(sk.ngayDienRa) <= :toDate`

2. **findDeletedWithSearchAndDateFilter**
   - Trước: `CAST(sk.deletedAt AS DATE) >= :fromDate`
   - Sau: `DATE(sk.deletedAt) >= :fromDate`
   - Trước: `CAST(sk.deletedAt AS DATE) <= :toDate`
   - Sau: `DATE(sk.deletedAt) <= :toDate`

### 3. Các repository khác

#### Đã kiểm tra và không cần thay đổi:
- **TaiChinhDanhMucRepository.java** - Không có query đặc biệt cần chuyển đổi
- **TaiChinhNamRepository.java** - Không có query đặc biệt cần chuyển đổi
- **BaiVietRepository.java** - Không có query đặc biệt cần chuyển đổi
- **NhanSuRepository.java** - Không có query đặc biệt cần chuyển đổi

#### Các function vẫn tương thích:
- `CURRENT_TIMESTAMP` - Hoạt động tốt trong cả PostgreSQL và MySQL
- `CONCAT()` - Hoạt động tốt trong cả hai database
- `LOWER()` - Hoạt động tốt trong cả hai database
- `COALESCE()` - Hoạt động tốt trong cả hai database

## Kết luận

Tất cả các query trong repository đã được chuyển đổi thành công từ PostgreSQL sang MySQL. Các thay đổi chính:

1. **EXTRACT functions** → **MySQL date functions** (YEAR, MONTH)
2. **CAST AS DATE** → **DATE() function**

Các query hiện tại đã tương thích hoàn toàn với MySQL và sẵn sàng để triển khai.