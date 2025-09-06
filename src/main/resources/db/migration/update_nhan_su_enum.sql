-- Update old enum values to new ones
UPDATE nhan_su 
SET trang_thai = 'HOAT_DONG' 
WHERE trang_thai = 'DANG_PHUC_VU';

-- Add any other enum mappings if needed
-- UPDATE nhan_su SET trang_thai = 'NEW_VALUE' WHERE trang_thai = 'OLD_VALUE';