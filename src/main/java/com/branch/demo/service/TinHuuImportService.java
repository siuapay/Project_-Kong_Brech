package com.branch.demo.service;

import com.branch.demo.domain.TinHuu;
import com.branch.demo.repository.TinHuuRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TinHuuImportService {

    @Autowired
    private TinHuuRepository tinHuuRepository;

    public ImportResult importFromExcel(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header rows - data starts from row 10 (index 9)
            // Row 1-8: Headers
            // Row 9: Column headers (STT, Họ và tên, etc.)
            // Row 10+: Actual data
            int startRow = 9; // Index 9 = Row 10 in Excel
            
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Skip empty rows
                Cell nameCell = row.getCell(3);
                if (nameCell == null || getCellValueAsString(nameCell).trim().isEmpty()) {
                    continue;
                }
                
                // Skip rows with formulas or summary rows
                String nameValue = getCellValueAsString(nameCell);
                if (nameValue.contains("COUNTA") || nameValue.contains("SUM") || 
                    nameValue.contains("Tổng") || nameValue.contains("TỔNG")) {
                    continue;
                }
                
                try {
                    TinHuu tinHuu = parseRowToTinHuu(row, i + 1);
                    if (tinHuu != null && tinHuu.getHoTen() != null && !tinHuu.getHoTen().trim().isEmpty()) {
                        tinHuuRepository.save(tinHuu);
                        result.successCount++;
                    }
                } catch (Exception e) {
                    result.errorCount++;
                    result.errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        
        return result;
    }

    private TinHuu parseRowToTinHuu(Row row, int rowNumber) {
        TinHuu tinHuu = new TinHuu();
        
        // Column mapping based on the image:
        // STT (0), STT Họ (1), STT NK (2), Họ và tên (3), Năm sinh Nam (4), Năm sinh Nữ (5),
        // Nhập sinh (6), Năm tín Chúa (7), Năm Báp tem (8), Dân tộc (9), Trình độ VH (10),
        // Chuyển đi (11), Nhập vào (12), Đi làm/học tại 6 tháng trở lên (13), Bộ nhóm tín đồ 6 tháng trở lên (14),
        // Chưa tin (15), Qua đời (16), Ghi chú (17)
        
        // Họ và tên (column 3)
        Cell nameCell = row.getCell(3);
        if (nameCell != null) {
            String hoTen = getCellValueAsString(nameCell).trim();
            // Skip if name is empty or contains formula
            if (hoTen.isEmpty() || hoTen.contains("COUNTA") || hoTen.contains("=")) {
                return null;
            }
            tinHuu.setHoTen(hoTen);
        }
        
        // Năm sinh - check both Nam (4) and Nữ (5) columns
        Cell namSinhNamCell = row.getCell(4);
        Cell namSinhNuCell = row.getCell(5);
        
        Integer namSinh = null;
        String gioiTinh = null;
        
        // Try to get from Nam column first
        if (namSinhNamCell != null) {
            String namValue = getCellValueAsString(namSinhNamCell).trim();
            if (!namValue.isEmpty() && !namValue.equalsIgnoreCase("X") && !namValue.equalsIgnoreCase("x")) {
                namSinh = parseYearFromString(namValue);
                if (namSinh != null) {
                    gioiTinh = "Nam";
                }
            }
        }
        
        // If not found in Nam column, try Nữ column
        if (namSinh == null && namSinhNuCell != null) {
            String nuValue = getCellValueAsString(namSinhNuCell).trim();
            if (!nuValue.isEmpty() && !nuValue.equalsIgnoreCase("X") && !nuValue.equalsIgnoreCase("x")) {
                namSinh = parseYearFromString(nuValue);
                if (namSinh != null) {
                    gioiTinh = "Nữ";
                }
            }
        }
        
        if (namSinh != null) {
            tinHuu.setNamSinh(namSinh);
        }
        if (gioiTinh != null) {
            tinHuu.setGioiTinh(gioiTinh);
        }
        
        // Nhập sinh (column 6)
        Cell nhapSinhCell = row.getCell(6);
        if (nhapSinhCell != null) {
            String nhapSinh = getCellValueAsString(nhapSinhCell);
            if (nhapSinh.equalsIgnoreCase("X") || nhapSinh.equalsIgnoreCase("x")) {
                // Mark as nhập sinh in ghi chú
                tinHuu.setGhiChu("Nhập sinh");
            }
        }
        
        // Năm tín Chúa (column 7) - map to ngayGiaNhap
        Cell namTinChuaCell = row.getCell(7);
        if (namTinChuaCell != null) {
            try {
                String yearStr = getCellValueAsString(namTinChuaCell);
                if (!yearStr.isEmpty()) {
                    int year = Integer.parseInt(yearStr);
                    tinHuu.setNgayGiaNhap(LocalDate.of(year, 1, 1));
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Năm Báp tem (column 8) - map to ngayBaoTin
        Cell namBapTemCell = row.getCell(8);
        if (namBapTemCell != null) {
            try {
                String yearStr = getCellValueAsString(namBapTemCell);
                if (!yearStr.isEmpty()) {
                    int year = Integer.parseInt(yearStr);
                    tinHuu.setNgayBaoTin(LocalDate.of(year, 1, 1));
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Dân tộc (column 9) - add to ghi chú
        Cell danTocCell = row.getCell(9);
        if (danTocCell != null) {
            String danToc = getCellValueAsString(danTocCell);
            if (!danToc.isEmpty()) {
                String currentGhiChu = tinHuu.getGhiChu() != null ? tinHuu.getGhiChu() + "; " : "";
                tinHuu.setGhiChu(currentGhiChu + "Dân tộc: " + danToc);
            }
        }
        
        // Ghi chú (column 17)
        Cell ghiChuCell = row.getCell(17);
        if (ghiChuCell != null) {
            String ghiChu = getCellValueAsString(ghiChuCell);
            if (!ghiChu.isEmpty()) {
                String currentGhiChu = tinHuu.getGhiChu() != null ? tinHuu.getGhiChu() + "; " : "";
                tinHuu.setGhiChu(currentGhiChu + ghiChu);
            }
        }
        
        // Set default status
        tinHuu.setTrangThai("HOAT_DONG");
        
        return tinHuu;
    }

    private Integer parseYearFromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        value = value.trim();
        
        try {
            // Try to parse as date format (dd/MM/yyyy or d/M/yyyy)
            if (value.contains("/")) {
                String[] parts = value.split("/");
                if (parts.length >= 3) {
                    // Get year part (last part)
                    String yearStr = parts[2].trim();
                    int year = Integer.parseInt(yearStr);
                    // If year is 2-digit, convert to 4-digit
                    if (year < 100) {
                        year = year < 50 ? 2000 + year : 1900 + year;
                    }
                    return year;
                } else if (parts.length == 2) {
                    // Format: MM/yyyy
                    String yearStr = parts[1].trim();
                    int year = Integer.parseInt(yearStr);
                    if (year < 100) {
                        year = year < 50 ? 2000 + year : 1900 + year;
                    }
                    return year;
                }
            }
            
            // Try to parse as plain year
            int year = Integer.parseInt(value);
            // If year is 2-digit, convert to 4-digit
            if (year < 100) {
                year = year < 50 ? 2000 + year : 1900 + year;
            }
            // Validate year range
            if (year >= 1900 && year <= 2100) {
                return year;
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
        
        return null;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    // Return as dd/MM/yyyy format
                    return String.format("%d/%d/%d", 
                        localDate.getDayOfMonth(), 
                        localDate.getMonthValue(), 
                        localDate.getYear());
                } else {
                    // Return as integer if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Try to get cached value instead of formula
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue().trim();
                        case NUMERIC:
                            double numericValue = cell.getNumericCellValue();
                            if (numericValue == (long) numericValue) {
                                return String.valueOf((long) numericValue);
                            } else {
                                return String.valueOf(numericValue);
                            }
                        default:
                            return "";
                    }
                } catch (Exception e) {
                    return "";
                }
            default:
                return "";
        }
    }

    public static class ImportResult {
        public int successCount = 0;
        public int errorCount = 0;
        public List<String> errors = new ArrayList<>();
        
        public int getSuccessCount() { return successCount; }
        public int getErrorCount() { return errorCount; }
        public List<String> getErrors() { return errors; }
        public int getTotalProcessed() { return successCount + errorCount; }
    }
}
