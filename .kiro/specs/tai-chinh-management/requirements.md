# Requirements Document - Hệ thống Quản lý Tài chính

## Introduction

Hệ thống quản lý tài chính cho phép theo dõi và quản lý các giao dịch thu chi của tổ chức theo năm. Hệ thống sẽ tự động tính toán tổng thu, tổng chi và số dư cho từng năm, đồng thời phân loại các giao dịch theo danh mục và người phụ trách.

## Requirements

### Requirement 1

**User Story:** Là một quản trị viên tài chính, tôi muốn tạo và quản lý các danh mục tài chính, để có thể phân loại các giao dịch thu chi một cách có hệ thống.

#### Acceptance Criteria

1. WHEN tôi tạo danh mục tài chính THEN hệ thống SHALL lưu thông tin gồm tên danh mục, loại (THU/CHI), và mô tả
2. WHEN tôi chọn loại danh mục THEN hệ thống SHALL chỉ cho phép chọn THU hoặc CHI
3. WHEN tôi nhập tên danh mục THEN hệ thống SHALL kiểm tra tên không được trùng lặp
4. WHEN tôi xem danh sách danh mục THEN hệ thống SHALL hiển thị danh mục theo loại (THU/CHI) và sắp xếp theo tên

### Requirement 2

**User Story:** Là một quản trị viên tài chính, tôi muốn ghi nhận các giao dịch tài chính, để theo dõi chi tiết từng khoản thu chi của tổ chức.

#### Acceptance Criteria

1. WHEN tôi tạo giao dịch mới THEN hệ thống SHALL yêu cầu nhập thời gian, loại (THU/CHI), số tiền, nội dung
2. WHEN tôi chọn danh mục THEN hệ thống SHALL chỉ hiển thị danh mục phù hợp với loại giao dịch (THU/CHI)
3. WHEN tôi chọn người phụ trách THEN hệ thống SHALL cho phép chọn từ danh sách nhân sự hiện có
4. WHEN tôi lưu giao dịch THEN hệ thống SHALL tự động cập nhật tổng thu/chi của năm tương ứng
5. WHEN tôi nhập số tiền THEN hệ thống SHALL chỉ chấp nhận số dương và định dạng tiền tệ

### Requirement 3

**User Story:** Là một quản trị viên tài chính, tôi muốn xem tổng hợp tài chính theo năm, để nắm được tình hình tài chính tổng thể của từng năm.

#### Acceptance Criteria

1. WHEN có giao dịch mới THEN hệ thống SHALL tự động tạo hoặc cập nhật bản ghi năm tương ứng
2. WHEN tính toán tổng năm THEN hệ thống SHALL tự động tính số dư = tổng thu - tổng chi
3. WHEN xem danh sách năm THEN hệ thống SHALL hiển thị năm, tổng thu, tổng chi, số dư
4. WHEN xóa giao dịch THEN hệ thống SHALL tự động trừ số tiền khỏi tổng năm tương ứng
5. WHEN sửa giao dịch THEN hệ thống SHALL cập nhật lại tổng năm với số tiền mới

### Requirement 4

**User Story:** Là một quản trị viên tài chính, tôi muốn tìm kiếm và lọc giao dịch, để dễ dàng tra cứu thông tin tài chính theo nhiều tiêu chí.

#### Acceptance Criteria

1. WHEN tôi tìm kiếm giao dịch THEN hệ thống SHALL cho phép lọc theo năm, loại, danh mục, người phụ trách
2. WHEN tôi tìm kiếm theo khoảng thời gian THEN hệ thống SHALL hiển thị giao dịch trong khoảng từ ngày đến ngày
3. WHEN tôi tìm kiếm theo nội dung THEN hệ thống SHALL tìm kiếm không phân biệt hoa thường
4. WHEN tôi tìm kiếm theo khoảng số tiền THEN hệ thống SHALL cho phép nhập số tiền tối thiểu và tối đa
5. WHEN xem kết quả tìm kiếm THEN hệ thống SHALL sắp xếp theo thời gian mới nhất trước

### Requirement 5

**User Story:** Là một quản trị viên tài chính, tôi muốn xem báo cáo thống kê tài chính, để có cái nhìn tổng quan về tình hình tài chính.

#### Acceptance Criteria

1. WHEN xem thống kê theo năm THEN hệ thống SHALL hiển thị biểu đồ thu chi theo tháng
2. WHEN xem thống kê theo danh mục THEN hệ thống SHALL hiển thị tỷ lệ thu/chi của từng danh mục
3. WHEN xem top giao dịch THEN hệ thống SHALL hiển thị các giao dịch có số tiền cao nhất
4. WHEN so sánh năm THEN hệ thống SHALL cho phép so sánh thu chi giữa các năm
5. WHEN xuất báo cáo THEN hệ thống SHALL cho phép xuất dữ liệu ra Excel

### Requirement 6

**User Story:** Là một quản trị viên hệ thống, tôi muốn đảm bảo tính toàn vẹn dữ liệu tài chính, để tránh sai sót trong quản lý tài chính.

#### Acceptance Criteria

1. WHEN xóa danh mục THEN hệ thống SHALL không cho phép xóa nếu còn giao dịch sử dụng danh mục đó
2. WHEN xóa nhân sự THEN hệ thống SHALL cập nhật giao dịch liên quan thành "không xác định"
3. WHEN thay đổi loại danh mục THEN hệ thống SHALL kiểm tra tương thích với giao dịch hiện có
4. WHEN backup dữ liệu THEN hệ thống SHALL đảm bảo tính nhất quán giữa các bảng
5. WHEN khôi phục dữ liệu THEN hệ thống SHALL tự động tính lại tổng các năm