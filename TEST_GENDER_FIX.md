# 🔧 Sửa Lỗi CHECK Constraint cho Giới Tính

## ❌ **Lỗi Gặp Phải**
```
The INSERT statement conflicted with the CHECK constraint "CK__tin_huu__gioi_ti__6D0D32F4". 
The conflict occurred in database "httl_kong_brech_db", table "dbo.tin_huu", column 'gioi_tinh'.
```

## 🔍 **Nguyên Nhân**
- Database có CHECK constraint cho trường `gioi_tinh`
- Form có thể gửi giá trị rỗng (`""`) 
- CHECK constraint không cho phép giá trị rỗng

## ✅ **Giải Pháp Đã Áp Dụng**

### 1. **Cập Nhật Entity** (`TinHuu.java`)
```java
@Column(name = "gioi_tinh", length = 10)
private String gioiTinh = "Nam";  // Giá trị mặc định
```

### 2. **Cập Nhật Service** (`AdminService.java`)
```java
public com.branch.demo.domain.TinHuu saveTinHuu(com.branch.demo.domain.TinHuu tinHuu) {
    // Đảm bảo giới tính có giá trị mặc định
    if (tinHuu.getGioiTinh() == null || tinHuu.getGioiTinh().trim().isEmpty()) {
        tinHuu.setGioiTinh("Nam");
    }
    // ... rest of method
}
```

### 3. **Cập Nhật Form** (`form.html`)
```html
<select class="form-select" id="gioiTinh" th:field="*{gioiTinh}" required>
    <option value="Nam">Nam</option>
    <option value="Nữ">Nữ</option>
</select>
```

## 🎯 **Kết Quả Mong Đợi**
- ✅ Form luôn có giá trị giới tính hợp lệ
- ✅ Entity có giá trị mặc định "Nam"
- ✅ Service validate và set default nếu cần
- ✅ Không còn lỗi CHECK constraint

## 🧪 **Test Cases**

### Test 1: Tạo tin hữu mới chỉ với tên
- Input: Họ tên = "Nguyễn Văn A", Giới tính = "Nam" (default)
- Expected: Lưu thành công

### Test 2: Tạo tin hữu với avatar
- Input: Họ tên + Avatar file
- Expected: Lưu thành công với avatar URL

### Test 3: Update tin hữu existing
- Input: Update thông tin tin hữu có sẵn
- Expected: Giữ nguyên createdAt, update các field khác

## 🚀 **Cách Test**
1. Restart ứng dụng để load code mới
2. Truy cập: `http://localhost:8080/admin/tin-huu/new`
3. Chỉ nhập tên và chọn avatar
4. Submit form
5. Kiểm tra kết quả

## 📝 **Lưu Ý**
- Giá trị mặc định "Nam" được set ở 3 tầng: Entity, Service, Form
- Form có `required` attribute để đảm bảo user phải chọn
- Service có validation để đảm bảo không có giá trị rỗng