-- -- MySQL Data Initialization
-- -- This file will be executed when spring.sql.init.mode=always

-- -- Insert default admin account if not exists
-- INSERT IGNORE INTO account (id, username, password, email, ho_ten, vai_tro, trang_thai, created_at, updated_at)
-- VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb0VeCzaOeRXZfhkqbHjbU5EiRg5.30C2M8z8.AC6', 'admin@httl.com', 'Administrator', 'ADMIN', 'ACTIVE', NOW(), NOW());

-- -- Insert default categories if not exists
-- INSERT IGNORE INTO danh_muc (id, ten_danh_muc, mo_ta, created_at, updated_at)
-- VALUES (1, 'Tin tức chung', 'Tin tức và thông báo chung của hội thánh', NOW(), NOW());

-- INSERT IGNORE INTO danh_muc (id, ten_danh_muc, mo_ta, created_at, updated_at)
-- VALUES (2, 'Sự kiện', 'Các sự kiện và hoạt động của hội thánh', NOW(), NOW());

-- -- Insert default financial categories if not exists
-- INSERT IGNORE INTO tai_chinh_danh_muc (id, ten_danh_muc, mo_ta, loai, created_at, updated_at)
-- VALUES (1, 'Dâng hiến', 'Tiền dâng hiến từ tín hữu', 'THU', NOW(), NOW());

-- INSERT IGNORE INTO tai_chinh_danh_muc (id, ten_danh_muc, mo_ta, loai, created_at, updated_at)
-- VALUES (2, 'Chi phí hoạt động', 'Chi phí vận hành hội thánh', 'CHI', NOW(), NOW());

-- -- Insert default event types if not exists
-- INSERT IGNORE INTO loai_su_kien (id, ten_loai, mo_ta, created_at, updated_at)
-- VALUES (1, 'Thờ phượng', 'Các buổi thờ phượng chính thức', NOW(), NOW());

-- INSERT IGNORE INTO loai_su_kien (id, ten_loai, mo_ta, created_at, updated_at)
-- VALUES (2, 'Sinh hoạt', 'Các hoạt động sinh hoạt cộng đồng', NOW(), NOW());