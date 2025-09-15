# Design Document - Hệ thống Quản lý Tài chính

## Overview

Hệ thống quản lý tài chính được thiết kế theo mô hình 3-layer architecture với Spring Boot, sử dụng JPA/Hibernate để quản lý dữ liệu. Hệ thống bao gồm 3 entities chính: TaiChinhGiaoDich, TaiChinhDanhMuc, và TaiChinhNam, với các mối quan hệ được thiết kế để đảm bảo tính toàn vẹn dữ liệu và hiệu suất truy vấn.

## Architecture

### Database Schema Design

```
tai_chinh_danh_muc
├── id (PK, AUTO_INCREMENT)
├── ten_danh_muc (VARCHAR(255), NOT NULL)
├── loai (ENUM: THU, CHI)
├── mo_ta (TEXT)
├── created_at (DATETIME)
└── updated_at (DATETIME)

tai_chinh_giao_dich
├── id (PK, AUTO_INCREMENT)
├── thoi_gian (DATETIME, NOT NULL)
├── loai (ENUM: THU, CHI)
├── so_tien (DECIMAL(15,2), NOT NULL)
├── noi_dung (VARCHAR(1000), NOT NULL)
├── danh_muc_id (FK -> tai_chinh_danh_muc.id)
├── nguoi_phu_trach_id (FK -> nhan_su.id)
├── created_at (DATETIME)
└── updated_at (DATETIME)

tai_chinh_nam
├── nam (PK, INT)
├── tong_thu (DECIMAL(15,2), DEFAULT 0)
├── tong_chi (DECIMAL(15,2), DEFAULT 0)
├── so_du (DECIMAL(15,2), COMPUTED)
└── updated_at (DATETIME)
```

### Entity Relationships

- **TaiChinhGiaoDich** ManyToOne **TaiChinhDanhMuc**: Mỗi giao dịch thuộc về một danh mục
- **TaiChinhGiaoDich** ManyToOne **NhanSu**: Mỗi giao dịch có một người phụ trách
- **TaiChinhNam**: Standalone entity, được cập nhật tự động qua business logic

## Components and Interfaces

### 1. Domain Layer

#### TaiChinhDanhMuc Entity
```java
@Entity
@Table(name = "tai_chinh_danh_muc")
public class TaiChinhDanhMuc {
    // Fields: id, tenDanhMuc, loai, moTa, createdAt, updatedAt
    // Enum: LoaiDanhMuc {THU("Thu"), CHI("Chi")}
    // Validation: tenDanhMuc unique constraint
}
```

#### TaiChinhGiaoDich Entity
```java
@Entity
@Table(name = "tai_chinh_giao_dich")
public class TaiChinhGiaoDich {
    // Fields: id, thoiGian, loai, soTien, noiDung, danhMuc, nguoiPhuTrach
    // Enum: LoaiGiaoDich {THU("Thu"), CHI("Chi")}
    // Helper methods: getNam(), isThu(), isChi()
    // Validation: soTien > 0, loai matches danhMuc.loai
}
```

#### TaiChinhNam Entity
```java
@Entity
@Table(name = "tai_chinh_nam")
public class TaiChinhNam {
    // Fields: nam (PK), tongThu, tongChi, soDu, updatedAt
    // Auto-calculation: soDu = tongThu - tongChi
    // Helper methods: addThu(), addChi(), subtractThu(), subtractChi()
}
```

### 2. Repository Layer

#### TaiChinhDanhMucRepository
- Basic CRUD operations
- Find by loai (THU/CHI)
- Search by tenDanhMuc (case-insensitive)
- Validation queries for duplicate names
- Pagination support

#### TaiChinhGiaoDichRepository
- Advanced search with multiple filters
- Aggregation queries by year, category
- Statistical queries (sum by year, category)
- Find distinct years with transactions
- Top transactions by amount

#### TaiChinhNamRepository
- Find by year
- Statistical aggregations (total thu/chi/sodu)
- Year range queries
- Recent years with data

### 3. Service Layer

#### TaiChinhService
```java
@Service
public class TaiChinhService {
    // CRUD operations for all entities
    // Business logic for transaction processing
    // Automatic year summary updates
    // Statistical calculations
    // Data validation and integrity checks
}
```

Key Methods:
- `saveGiaoDich(TaiChinhGiaoDich)`: Save transaction and update year summary
- `deleteGiaoDich(Long id)`: Delete transaction and adjust year summary
- `updateGiaoDich(TaiChinhGiaoDich)`: Update transaction and recalculate year
- `getThongKeNam(Integer nam)`: Get year statistics
- `searchGiaoDich(SearchCriteria)`: Advanced search with filters

### 4. Controller Layer

#### TaiChinhController
```java
@Controller
@RequestMapping("/admin/tai-chinh")
public class TaiChinhController {
    // RESTful endpoints for all CRUD operations
    // Search and filter endpoints
    // Statistical report endpoints
    // Export functionality
}
```

Endpoints:
- `/danh-muc/**`: Category management
- `/giao-dich/**`: Transaction management  
- `/thong-ke/**`: Statistics and reports
- `/api/**`: AJAX endpoints for dropdowns and search

## Data Models

### DTOs for Data Transfer

#### TaiChinhGiaoDichDTO
```java
public class TaiChinhGiaoDichDTO {
    private Long id;
    private LocalDateTime thoiGian;
    private String loai;
    private BigDecimal soTien;
    private String noiDung;
    private Long danhMucId;
    private String tenDanhMuc;
    private Long nguoiPhuTrachId;
    private String tenNguoiPhuTrach;
    // Getters/Setters and validation annotations
}
```

#### TaiChinhThongKeDTO
```java
public class TaiChinhThongKeDTO {
    private Integer nam;
    private BigDecimal tongThu;
    private BigDecimal tongChi;
    private BigDecimal soDu;
    private List<ThongKeDanhMuc> thongKeDanhMuc;
    private List<ThongKeThang> thongKeThang;
}
```

### Search Criteria Classes

#### GiaoDichSearchCriteria
```java
public class GiaoDichSearchCriteria {
    private String loai;
    private Long danhMucId;
    private Long nguoiPhuTrachId;
    private String noiDung;
    private LocalDateTime tuNgay;
    private LocalDateTime denNgay;
    private BigDecimal soTienMin;
    private BigDecimal soTienMax;
    private Integer nam;
}
```

## Error Handling

### Validation Rules
1. **TaiChinhDanhMuc**: Tên danh mục không được trùng lặp
2. **TaiChinhGiaoDich**: Số tiền phải > 0, loại giao dịch phải khớp với loại danh mục
3. **Business Rules**: Không được xóa danh mục đang được sử dụng

### Exception Handling
```java
@ControllerAdvice
public class TaiChinhExceptionHandler {
    @ExceptionHandler(DanhMucDangSuDungException.class)
    @ExceptionHandler(GiaoDichKhongHopLeException.class)
    @ExceptionHandler(DataIntegrityViolationException.class)
}
```

### Error Messages
- Validation errors: Field-level validation với Spring Validation
- Business logic errors: Custom exceptions với user-friendly messages
- Database errors: Generic error handling với logging

## Testing Strategy

### Unit Tests
- **Repository Tests**: Test custom queries và aggregations
- **Service Tests**: Test business logic và data integrity
- **Controller Tests**: Test endpoints và request/response handling

### Integration Tests
- **Database Integration**: Test với H2 in-memory database
- **Transaction Tests**: Test automatic year summary updates
- **Search Tests**: Test complex search queries

### Test Data Setup
```java
@TestConfiguration
public class TaiChinhTestConfig {
    // Setup test data for categories, transactions, and years
    // Mock NhanSu entities for foreign key relationships
}
```

### Key Test Scenarios
1. **Transaction Creation**: Verify year summary auto-update
2. **Transaction Deletion**: Verify year summary adjustment
3. **Category Deletion**: Verify constraint validation
4. **Search Functionality**: Test all filter combinations
5. **Statistical Calculations**: Verify aggregation accuracy

## Performance Considerations

### Database Indexing
```sql
-- Indexes for optimal query performance
CREATE INDEX idx_giao_dich_thoi_gian ON tai_chinh_giao_dich(thoi_gian);
CREATE INDEX idx_giao_dich_nam ON tai_chinh_giao_dich(YEAR(thoi_gian));
CREATE INDEX idx_giao_dich_danh_muc ON tai_chinh_giao_dich(danh_muc_id);
CREATE INDEX idx_giao_dich_nguoi_phu_trach ON tai_chinh_giao_dich(nguoi_phu_trach_id);
CREATE INDEX idx_danh_muc_loai ON tai_chinh_danh_muc(loai);
```

### Caching Strategy
- Cache danh mục lists (rarely changed)
- Cache year statistics (updated only on transaction changes)
- Use Spring Cache với Redis for production

### Pagination
- All list views implement pagination
- Default page size: 20 items
- Search results paginated for performance

## Security Considerations

### Access Control
- All endpoints require ADMIN role
- Transaction modification logged for audit
- Sensitive financial data protected

### Data Validation
- Server-side validation for all inputs
- SQL injection prevention via JPA
- XSS protection in templates

### Audit Trail
```java
@EntityListeners(AuditingEntityListener.class)
public class TaiChinhGiaoDich {
    @CreatedBy private String createdBy;
    @LastModifiedBy private String lastModifiedBy;
}
```