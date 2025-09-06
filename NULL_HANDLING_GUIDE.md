# Hướng Dẫn Xử Lý Null Values

## Tin Hữu (TinHuu)

### Field Bắt Buộc:
- ✅ **hoTen** - Họ và tên (NOT NULL)

### Field Không Bắt Buộc (có thể NULL):
- ❌ gioiTinh - Giới tính
- ❌ email - Email
- ❌ dienThoai - Điện thoại  
- ❌ diaChi - Địa chỉ
- ❌ ngheNghiep - Nghề nghiệp
- ❌ ghiChu - Ghi chú
- ❌ tinhTrangHonNhan - Tình trạng hôn nhân
- ❌ avatarUrl - Đường dẫn avatar
- ❌ namSinh - Năm sinh
- ❌ ngayBaoTin - Ngày báo tin
- ❌ nhom - Nhóm

## Ban Ngành (BanNganh)

### Field Bắt Buộc:
- ✅ **tenBan** - Tên ban ngành (NOT NULL)
- ✅ **maBan** - Mã ban ngành (NOT NULL, UPPERCASE)

### Field Không Bắt Buộc (có thể NULL):
- ❌ moTa - Mô tả
- ❌ emailLienHe - Email liên hệ
- ❌ dienThoaiLienHe - Điện thoại liên hệ

## Nhóm (Nhom)

### Field Bắt Buộc:
- ✅ **tenNhom** - Tên nhóm (NOT NULL)
- ✅ **diemNhom** - Điểm nhóm (NOT NULL)
- ✅ **trangThai** - Trạng thái (NOT NULL)

### Field Không Bắt Buộc (có thể NULL):
- ❌ moTa - Mô tả

## Logic Xử Lý:

1. **Empty String → NULL**: Nếu field không bắt buộc và user để trống, sẽ lưu NULL thay vì empty string
2. **Validation**: Chỉ validate field bắt buộc
3. **Database**: NULL values được lưu chính xác trong database
4. **Display**: NULL values hiển thị "Chưa cập nhật" hoặc "Chưa có" trong UI

## Ví Dụ:

```java
// Trước khi lưu
if (field != null && field.trim().isEmpty()) {
    field = null; // Convert empty string thành null
}
```

```html
<!-- Hiển thị trong template -->
<span th:if="${tinHuu.email}" th:text="${tinHuu.email}"></span>
<span th:unless="${tinHuu.email}" class="text-muted">Chưa có email</span>
```