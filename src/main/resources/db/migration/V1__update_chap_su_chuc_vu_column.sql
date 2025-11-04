-- Migration to update chuc_vu column length in chap_su table
-- This fixes the "Data truncated" error when saving CHAP_SU enum value

ALTER TABLE chap_su MODIFY COLUMN chuc_vu VARCHAR(50);