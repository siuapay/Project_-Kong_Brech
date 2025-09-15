# Implementation Plan - Hệ thống Quản lý Tài chính

- [x] 1. Create core domain entities and enums
  - Implement TaiChinhDanhMuc entity with LoaiDanhMuc enum (THU/CHI)
  - Implement TaiChinhGiaoDich entity with LoaiGiaoDich enum and relationships
  - Implement TaiChinhNam entity with auto-calculation logic for soDu
  - Add JPA annotations, validation constraints, and helper methods
  - _Requirements: 1.1, 1.2, 2.1, 2.2, 3.1, 3.2_

- [ ] 2. Create repository interfaces with custom queries
  - [x] 2.1 Implement TaiChinhDanhMucRepository with search and validation methods
    - Create basic CRUD operations and custom finder methods
    - Add methods for finding by loai, searching by name, and duplicate validation
    - Implement pagination support for category listing
    - _Requirements: 1.3, 1.4, 6.1_

  - [x] 2.2 Implement TaiChinhGiaoDichRepository with advanced search capabilities
    - Create complex search method with multiple filter criteria
    - Add aggregation methods for calculating totals by year and category
    - Implement methods for finding distinct years and top transactions
    - Add statistical query methods for reporting features
    - _Requirements: 2.4, 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.3_

  - [x] 2.3 Implement TaiChinhNamRepository with year-based operations
    - Create methods for finding by year and year ranges
    - Add aggregation methods for total calculations across all years
    - Implement methods for finding years with data and recent years
    - _Requirements: 3.3, 3.4, 5.2, 5.4_

- [ ] 3. Create service layer with business logic
  - [x] 3.1 Implement TaiChinhService with CRUD operations for all entities
    - Create service methods for managing categories with validation
    - Implement transaction management with automatic year summary updates
    - Add year summary management with calculation methods
    - Include data validation and business rule enforcement
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 3.1, 3.2, 6.2, 6.3_

  - [x] 3.2 Implement automatic year summary update logic
    - Create methods to automatically update TaiChinhNam when transactions change
    - Implement transaction event handlers for create, update, delete operations
    - Add recalculation methods for fixing data inconsistencies
    - Include validation to ensure year summary accuracy
    - _Requirements: 2.4, 3.1, 3.4, 3.5, 6.4, 6.5_

  - [x] 3.3 Implement advanced search and statistical methods
    - Create search service with multiple filter criteria support
    - Implement statistical calculation methods for reports and charts
    - Add export functionality for financial data
    - Include caching for frequently accessed statistics
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 4. Create DTOs and validation classes
  - [x] 4.1 Create TaiChinhGiaoDichDTO with validation annotations
    - Implement DTO for transaction data transfer with all required fields
    - Add validation annotations for data integrity (NotNull, Positive, etc.)
    - Include conversion methods between entity and DTO
    - Add display formatting methods for currency and dates
    - _Requirements: 2.1, 2.5, 6.1_

  - [x] 4.2 Create TaiChinhDanhMucDTO and search criteria classes
    - Implement DTO for category management with validation
    - Create GiaoDichSearchCriteria class for advanced search
    - Add TaiChinhThongKeDTO for statistical reports
    - Include validation for search parameters and date ranges
    - _Requirements: 1.1, 1.2, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2_

- [ ] 5. Implement controller layer with REST endpoints
  - [x] 5.1 Create TaiChinhController with category management endpoints
    - Implement CRUD endpoints for TaiChinhDanhMuc (/admin/tai-chinh/danh-muc/**)
    - Add validation and error handling for category operations
    - Create API endpoints for dropdown data and AJAX requests
    - Include pagination and sorting for category lists
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 6.1_

  - [x] 5.2 Implement transaction management endpoints
    - Create CRUD endpoints for TaiChinhGiaoDich (/admin/tai-chinh/giao-dich/**)
    - Add advanced search endpoint with multiple filter support
    - Implement transaction validation and business rule enforcement
    - Create API endpoints for dependent dropdowns (categories by type)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 4.1, 4.2, 4.3, 4.4, 4.5, 6.2, 6.3_

  - [x] 5.3 Create statistical and reporting endpoints
    - Implement year summary endpoints (/admin/tai-chinh/thong-ke/**)
    - Add chart data endpoints for financial visualization
    - Create export endpoints for Excel/CSV download
    - Include dashboard summary endpoints for overview statistics
    - _Requirements: 3.3, 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 6. Create Thymeleaf templates for user interface
  - [x] 6.1 Create category management templates
    - Implement danh-muc/list.html with search and pagination
    - Create danh-muc/form.html for add/edit category with validation
    - Add danh-muc/view.html for category details and related transactions
    - Include AJAX functionality for real-time validation
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 6.2 Implement transaction management templates
    - Create giao-dich/list.html with advanced search filters and pagination
    - Implement giao-dich/form.html with dependent dropdowns and validation
    - Add giao-dich/view.html for transaction details and edit options
    - Include searchable dropdowns for categories and personnel
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 4.1, 4.2, 4.3, 4.4, 4.5_

  - [x] 6.3 Create statistical dashboard and report templates
    - Implement thong-ke/dashboard.html with year overview and charts
    - Create thong-ke/nam.html for detailed year analysis
    - Add thong-ke/so-sanh.html for year comparison functionality
    - Include interactive charts using Chart.js for data visualization
    - _Requirements: 3.3, 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 7. Add navigation and integrate with existing admin system
  - Update admin navigation menu to include financial management section
  - Add financial management links to admin dashboard
  - Create breadcrumb navigation for financial module pages
  - Include financial statistics in main dashboard overview
  - _Requirements: All requirements - system integration_

- [ ] 8. Create comprehensive unit and integration tests
  - [ ] 8.1 Write repository layer tests
    - Test all custom query methods in repositories
    - Verify aggregation calculations and statistical queries
    - Test pagination and sorting functionality
    - Include edge cases and boundary value testing
    - _Requirements: All repository-related requirements_

  - [ ] 8.2 Write service layer tests with business logic validation
    - Test automatic year summary update logic
    - Verify transaction validation and business rules
    - Test search functionality with various filter combinations
    - Include transaction rollback and error handling tests
    - _Requirements: All service-related requirements_

  - [ ] 8.3 Write controller integration tests
    - Test all REST endpoints with various input scenarios
    - Verify request/response handling and validation
    - Test error handling and exception scenarios
    - Include security and authorization testing
    - _Requirements: All controller-related requirements_

- [ ] 9. Add sample data initialization for testing and demo
  - Create sample categories for common income and expense types
  - Generate sample transactions across multiple years for testing
  - Initialize year summaries with calculated totals
  - Add data migration scripts for existing systems if needed
  - _Requirements: System usability and testing support_