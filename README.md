# HTTL Kông Brech - Hệ thống quản lý hội thánh

## Mô tả dự án
Hệ thống quản lý hội thánh HTTL Kông Brech được xây dựng bằng Spring Boot với JPA/Hibernate, hỗ trợ đầy đủ tiếng Việt (Unicode).

## Các thực thể (Entities) đã tạo

### 1. TinHuu (Tín hữu)
- Quản lý thông tin các thành viên hội thánh
- Hỗ trợ phân loại theo nhóm, giới tính, độ tuổi
- Lưu trữ thông tin cá nhân, liên hệ, ghi chú

### 2. Nhom (Nhóm)
- Quản lý các nhóm tín hữu
- Liên kết với điểm nhóm và danh sách tín hữu

### 3. DiemNhom (Điểm nhóm)
- Quản lý các điểm sinh hoạt nhóm
- Thông tin địa chỉ, thời gian sinh hoạt
- Liên kết với nhân sự phụ trách

### 4. NhanSu (Nhân sự)
- Quản lý nhân sự các ban ngành
- Thông tin chức vụ, trách nhiệm, liên hệ

### 5. BanNganh (Ban ngành)
- Quản lý cơ cấu tổ chức các ban ngành
- Thông tin liên hệ, mô tả chức năng

### 6. NhanSuDiemNhom (Nhân sự điểm nhóm)
- Quản lý nhân sự phụ trách các điểm nhóm
- Trưởng nhóm, phó nhóm cho từng điểm

### 7. ChapSu (Chấp sự)
- Quản lý ban chấp sự hội thánh
- Thông tin cấp bậc, nhiệm kỳ, tiểu sử

### 8. SuKien (Sự kiện)
- Quản lý các sự kiện, hoạt động của hội thánh
- Phân loại theo loại sự kiện, trạng thái

### 9. TaiChinh (Tài chính)
- Quản lý thu chi tài chính
- Theo dõi dâng hiến, chi tiêu
- Báo cáo tài chính

### 10. ThongBao (Thông báo)
- Hệ thống thông báo nội bộ
- Phân loại theo mức độ ưu tiên, đối tượng nhận

### 11. LienHe (Liên hệ)
- Quản lý tin nhắn liên hệ từ bên ngoài
- Phân loại, trạng thái xử lý

## Tính năng chính

### Hỗ trợ tiếng Việt
- Tất cả các trường text sử dụng `NVARCHAR` để hỗ trợ Unicode
- Cấu hình database connection với UTF-8 encoding
- Tìm kiếm không phân biệt hoa thường cho tiếng Việt

### Repository với các query phức tạp
- Tìm kiếm theo nhiều điều kiện
- Thống kê và báo cáo
- Phân trang (Pagination)
- Query tùy chỉnh với JPQL

### Quan hệ giữa các entity
- One-to-Many: DiemNhom -> Nhom -> TinHuu
- Many-to-One: NhanSu -> BanNganh
- Cascade operations được cấu hình phù hợp

## Cấu hình database

### SQL Server
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1234;databaseName=httl_kong_brech_db;encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;useUnicode=true;sendStringParametersAsUnicode=true
spring.datasource.username=adminProVjp
spring.datasource.password=123456
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### JPA/Hibernate
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.connection.characterEncoding=UTF-8
spring.jpa.properties.hibernate.connection.useUnicode=true
```

## API Test Endpoints

### Tổng quan
- `GET /api/test/overview` - Thống kê tổng quan
- `GET /api/test/thong-ke` - Thống kê chi tiết

### Danh sách entities
- `GET /api/test/tin-huu` - Danh sách tín hữu
- `GET /api/test/nhom` - Danh sách nhóm
- `GET /api/test/diem-nhom` - Danh sách điểm nhóm
- `GET /api/test/ban-nganh` - Danh sách ban ngành
- `GET /api/test/nhan-su` - Danh sách nhân sự
- `GET /api/test/chap-su` - Danh sách chấp sự
- `GET /api/test/su-kien` - Danh sách sự kiện
- `GET /api/test/tai-chinh` - Danh sách giao dịch tài chính
- `GET /api/test/thong-bao` - Danh sách thông báo
- `GET /api/test/lien-he` - Danh sách liên hệ

## Khởi chạy ứng dụng

1. Đảm bảo SQL Server đang chạy
2. Tạo database `httl_kong_brech_db`
3. Cập nhật thông tin kết nối trong `application.properties`
4. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

5. Truy cập: `http://localhost:8080/api/test/overview`

## Dữ liệu mẫu
Ứng dụng sẽ tự động tạo dữ liệu mẫu khi khởi chạy lần đầu, bao gồm:
- 7 ban ngành
- 4 chấp sự
- 4 điểm nhóm với 4 nhóm
- 5 tín hữu mẫu
- 3 nhân sự ban ngành
- 3 nhân sự điểm nhóm
- 3 sự kiện
- 3 giao dịch tài chính
- 2 thông báo
- 2 liên hệ

## Cấu trúc thư mục
```
src/main/java/com/branch/demo/
├── domain/          # Các entity classes
├── repository/      # Repository interfaces
├── service/         # Service classes
├── controller/      # REST controllers
└── DemoApplication.java
```

## Lưu ý
- Tất cả các trường text đều sử dụng `NVARCHAR` để hỗ trợ tiếng Việt
- Các enum được định nghĩa bằng tiếng Việt không dấu để tránh lỗi encoding
- Sử dụng `@PrePersist` và `@PreUpdate` để tự động cập nhật timestamp
- Repository có sẵn các method tìm kiếm phức tạp và thống kê