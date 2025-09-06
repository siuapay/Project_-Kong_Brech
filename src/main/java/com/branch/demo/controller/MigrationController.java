// package com.branch.demo.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/admin/migration")
// public class MigrationController {

//     @Autowired
//     private JdbcTemplate jdbcTemplate;

//     @GetMapping("/fix-enum")
//     public String fixEnumValues() {
//         try {
//             // Check current data
//             Integer count = jdbcTemplate.queryForObject(
//                 "SELECT COUNT(*) FROM nhan_su WHERE trang_thai = 'DANG_PHUC_VU'", 
//                 Integer.class
//             );
            
//             if (count != null && count > 0) {
//                 // Update old enum values to new ones
//                 int updatedRows = jdbcTemplate.update(
//                     "UPDATE nhan_su SET trang_thai = 'HOAT_DONG' WHERE trang_thai = 'DANG_PHUC_VU'"
//                 );
                
//                 return "✅ Migration completed! Updated " + updatedRows + " records from DANG_PHUC_VU to HOAT_DONG";
//             } else {
//                 return "ℹ️ No records found with DANG_PHUC_VU status";
//             }
            
//         } catch (Exception e) {
//             return "❌ Migration failed: " + e.getMessage();
//         }
//     }

//     @GetMapping("/check-data")
//     public String checkData() {
//         try {
//             Integer totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nhan_su", Integer.class);
            
//             String result = "📊 Database Status:\n";
//             result += "Total NhanSu records: " + totalCount + "\n";
            
//             // Check enum values
//             try {
//                 Integer dangPhucVuCount = jdbcTemplate.queryForObject(
//                     "SELECT COUNT(*) FROM nhan_su WHERE trang_thai = 'DANG_PHUC_VU'", 
//                     Integer.class
//                 );
//                 result += "DANG_PHUC_VU: " + dangPhucVuCount + "\n";
//             } catch (Exception e) {
//                 result += "DANG_PHUC_VU: 0\n";
//             }
            
//             try {
//                 Integer hoatDongCount = jdbcTemplate.queryForObject(
//                     "SELECT COUNT(*) FROM nhan_su WHERE trang_thai = 'HOAT_DONG'", 
//                     Integer.class
//                 );
//                 result += "HOAT_DONG: " + hoatDongCount + "\n";
//             } catch (Exception e) {
//                 result += "HOAT_DONG: 0\n";
//             }
            
//             return result;
            
//         } catch (Exception e) {
//             return "❌ Check failed: " + e.getMessage();
//         }
//     }
// }