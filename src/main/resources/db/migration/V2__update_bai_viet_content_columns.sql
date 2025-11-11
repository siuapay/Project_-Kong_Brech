-- Migration to update noi_dung and noi_dung_rich columns to MEDIUMTEXT
-- This fixes the "Data too long for column" error when saving rich content

ALTER TABLE bai_viet MODIFY COLUMN noi_dung LONGTEXT;
ALTER TABLE bai_viet MODIFY COLUMN noi_dung_rich LONGTEXT;