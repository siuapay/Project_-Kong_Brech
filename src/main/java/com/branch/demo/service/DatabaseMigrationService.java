package com.branch.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseMigrationService implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Drop existing CHECK constraint if exists
            try {
                jdbcTemplate.execute("ALTER TABLE nhan_su DROP CONSTRAINT CK__nhan_su__trang_t__5629CD9C");
                System.out.println("✅ Dropped old CHECK constraint for nhan_su.trang_thai");
            } catch (Exception e) {
                System.out.println("⚠️ Old constraint not found or already dropped: " + e.getMessage());
            }
            
            // Add new CHECK constraint with correct enum values
            jdbcTemplate.execute(
                "ALTER TABLE nhan_su ADD CONSTRAINT CK_nhan_su_trang_thai " +
                "CHECK (trang_thai IN ('HOAT_DONG', 'DANG_PHUC_VU', 'TAM_NGHI', 'NGHI_VIEC', 'CHUYEN_BAN'))"
            );
            System.out.println("✅ Added new CHECK constraint for nhan_su.trang_thai");
            
        } catch (Exception e) {
            System.out.println("⚠️ Migration warning: " + e.getMessage());
            // Don't fail the application if migration fails
        }
    }
}