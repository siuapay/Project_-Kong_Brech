# 🗑️ Hướng Dẫn Hệ Thống Xóa Mềm & Xóa Cứng

## ✨ Tính Năng Hoàn Thành

### 🎯 **Soft Delete System**
- ✅ Xóa mềm (Soft Delete) - Chuyển vào thùng rác
- ✅ Xóa cứng (Hard Delete) - Xóa vĩnh viễn
- ✅ Khôi phục (Restore) - Phục hồi từ thùng rác
- ✅ Quản lý thùng rác với giao diện riêng
- ✅ Modal xác nhận cho từng hành động

### 📋 **Quy Trình Xóa**

#### 1. **Xóa Mềm (Soft Delete)**
- **Trigger**: Nhấn nút "Xóa" trong danh sách tin hữu
- **Action**: Set `deletedAt = LocalDateTime.now()`
- **Result**: Tin hữu biến mất khỏi danh sách chính
- **Recovery**: Có thể khôi phục từ thùng rác

#### 2. **Xóa Cứng (Hard Delete)**
- **Trigger**: Nhấn nút "Xóa vĩnh viễn" trong thùng rác
- **Condition**: Chỉ áp dụng cho tin hữu đã bị xóa mềm
- **Action**: Xóa hoàn toàn khỏi database
- **Recovery**: Không thể khôi phục

#### 3. **Khôi Phục (Restore)**
- **Trigger**: Nhấn nút "Khôi phục" trong thùng rác
- **Action**: Set `deletedAt = null`
- **Result**: Tin hữu xuất hiện lại trong danh sách chính

### 🔧 **Cấu Trúc Database**

#### **TinHuu Entity**
```java
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

// Helper methods
public boolean isDeleted() { return deletedAt != null; }
public void softDelete() { this.deletedAt = LocalDateTime.now(); }
public void restore() { this.deletedAt = null; }
```

### 📊 **Repository Queries**

#### **Active Records**
```java
@Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NULL")
Page<TinHuu> findActiveWithSearch(@Param("search") String search, Pageable pageable);
```

#### **Deleted Records**
```java
@Query("SELECT t FROM TinHuu t WHERE t.deletedAt IS NOT NULL")
Page<TinHuu> findDeletedWithSearch(@Param("search") String search, Pageable pageable);
```

#### **Statistics**
```java
@Query("SELECT COUNT(t) FROM TinHuu t WHERE t.deletedAt IS NULL")
long countActive();

@Query("SELECT COUNT(t) FROM TinHuu t WHERE t.deletedAt IS NOT NULL")
long countDeleted();
```

### 🎮 **Controller Endpoints**

#### **Soft Delete**
- **URL**: `POST /admin/tin-huu/delete/{id}`
- **Action**: Chuyển tin hữu vào thùng rác
- **Redirect**: Về danh sách chính

#### **Hard Delete**
- **URL**: `POST /admin/tin-huu/hard-delete/{id}`
- **Action**: Xóa vĩnh viễn tin hữu
- **Redirect**: Về thùng rác

#### **Restore**
- **URL**: `POST /admin/tin-huu/restore/{id}`
- **Action**: Khôi phục tin hữu
- **Redirect**: Về thùng rác

#### **Trash View**
- **URL**: `GET /admin/tin-huu/deleted`
- **Action**: Hiển thị danh sách tin hữu đã xóa

### 🎨 **Giao Diện**

#### **Danh Sách Chính**
- Chỉ hiển thị tin hữu active (`deletedAt IS NULL`)
- Nút "Xóa" → Modal xác nhận xóa mềm
- Nút "Thùng Rác" → Chuyển đến trang thùng rác

#### **Thùng Rác**
- Hiển thị tin hữu đã xóa (`deletedAt IS NOT NULL`)
- Avatar mờ đi (opacity-50)
- Tên có gạch ngang (text-decoration-line-through)
- Hiển thị thời gian xóa
- 2 nút: "Khôi phục" và "Xóa vĩnh viễn"

#### **Modal Confirmations**

**Soft Delete Modal:**
- Màu warning (vàng)
- Thông báo có thể khôi phục
- Nút "Chuyển Vào Thùng Rác"

**Hard Delete Modal:**
- Màu danger (đỏ)
- Cảnh báo không thể hoàn tác
- Nút "Xóa Vĩnh Viễn"

### 🔄 **Service Layer**

#### **AdminService Methods**
```java
// Soft delete
public void softDeleteTinHuu(Long id)

// Hard delete  
public void hardDeleteTinHuu(Long id)

// Restore
public void restoreTinHuu(Long id)

// Get deleted records
public Page<TinHuu> getDeletedTinHuuPage(int page, String search)
```

### 📈 **Dashboard Statistics**
- **Active Tin Huu**: Đếm tin hữu chưa bị xóa
- **Deleted Tin Huu**: Đếm tin hữu trong thùng rác
- Cập nhật real-time

### 🛡️ **Validation & Security**

#### **Business Rules**
- Chỉ có thể hard delete tin hữu đã soft delete
- Chỉ có thể restore tin hữu đã soft delete
- Không thể soft delete tin hữu đã bị xóa

#### **Error Handling**
- Validation messages rõ ràng
- Flash messages cho từng action
- Graceful error recovery

### 🎯 **User Experience**

#### **Workflow Tự Nhiên**
1. **Xóa nhầm** → Soft delete → Vào thùng rác → Restore
2. **Xóa thật** → Soft delete → Vào thùng rác → Hard delete
3. **Dọn dẹp** → Bulk operations trong thùng rác

#### **Visual Indicators**
- **Active**: Avatar bình thường, tên rõ ràng
- **Deleted**: Avatar mờ, tên gạch ngang, thời gian xóa
- **Actions**: Icon và màu sắc phù hợp

### 🔧 **Navigation**

#### **Sidebar Menu**
```
Tin Hữu
├── Danh Sách (active records)
└── Thùng Rác (deleted records)
```

#### **Breadcrumb**
- Danh sách: Admin > Tin Hữu
- Thùng rác: Admin > Tin Hữu > Thùng Rác

### 📱 **Responsive Design**
- Mobile-friendly modals
- Touch-friendly buttons
- Responsive table layout
- Optimized for all screen sizes

### 🚀 **Performance**

#### **Database Optimization**
- Index trên `deleted_at` column
- Separate queries cho active/deleted
- Pagination cho cả 2 views

#### **Memory Management**
- Lazy loading relationships
- Efficient queries với WHERE clauses
- Minimal data transfer

### 🎊 **Kết Quả**

**Hệ thống xóa mềm hoàn chỉnh với:**
- ✅ User-friendly workflow
- ✅ Data safety với soft delete
- ✅ Recovery options
- ✅ Clean interface
- ✅ Professional UX
- ✅ Mobile responsive
- ✅ Error handling
- ✅ Performance optimized

**URLs để test:**
- 📋 **Danh sách**: `http://localhost:8080/admin/tin-huu`
- 🗑️ **Thùng rác**: `http://localhost:8080/admin/tin-huu/deleted`

### 🎯 **Tính Năng Mở Rộng**

#### **Có Thể Thêm**
- 🔄 Bulk operations (xóa/khôi phục nhiều)
- ⏰ Auto-cleanup sau X ngày
- 📊 Audit log cho delete actions
- 🔍 Advanced search trong thùng rác
- 📧 Email notification khi xóa
- 💾 Export deleted records