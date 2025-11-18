package com.branch.demo.service;

import com.branch.demo.domain.TinHuu;
import com.branch.demo.repository.TinHuuRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class TinHuuExportService {

    @Autowired
    private TinHuuRepository tinHuuRepository;

    public byte[] exportToExcel() throws IOException {
        // Only export active tin huu (not deleted)
        List<TinHuu> tinHuuList = tinHuuRepository.findAllActive();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh Sách Tín Đồ");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);
            
            int rowNum = 0;
            
            // Row 1: Logo and Title
            Row row1 = sheet.createRow(rowNum++);
            row1.setHeightInPoints(30);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("HỘI THÁNH TIN LÀNH VIỆT NAM");
            cell1.setCellStyle(titleStyle);
            
            // Row 2: Chi Hội
            Row row2 = sheet.createRow(rowNum++);
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue("CHI HỘI KÔNG BRẾCH");
            cell2.setCellStyle(titleStyle);
            
            // Row 3: Location
            Row row3 = sheet.createRow(rowNum++);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("Ia Băng - Gia Lai");
            cell3.setCellStyle(centerStyle);
            
            // Row 4: Main Title
            Row row4 = sheet.createRow(rowNum++);
            row4.setHeightInPoints(25);
            Cell cell4 = row4.createCell(0);
            cell4.setCellValue("DANH SÁCH TÍN ĐỒ CẬP NHẬT " + Year.now().getValue());
            cell4.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 17));
            
            // Row 5: POLEI info
            Row row5 = sheet.createRow(rowNum++);
            Cell cell5 = row5.createCell(14);
            cell5.setCellValue("POLEI: ................................");
            
            // Row 6: Empty
            rowNum++;
            
            // Row 7: Section Title
            Row row7 = sheet.createRow(rowNum++);
            row7.setHeightInPoints(20);
            Cell cell7 = row7.createCell(0);
            cell7.setCellValue("HIỆN THỊ TỔNG SỐ");
            cell7.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 17));
            
            // Row 8: Statistics Header
            Row row8 = sheet.createRow(rowNum++);
            String[] statsHeaders = {"Họ", "N K", "Tổng số tín hữu", "Tín hữu Nam", "Tín hữu Nữ", "Nhập sinh", 
                "", "Đã chịu Báp tem", "", "Số C đi", "Số N vào", "Đi xa", "Bộ Nhóm", "Chưa Tin", "Qua đời", ""};
            for (int i = 0; i < statsHeaders.length; i++) {
                Cell cell = row8.createCell(i);
                cell.setCellValue(statsHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Row 9: Statistics Values (only count active tin huu)
            Row row9 = sheet.createRow(rowNum++);
            long totalNam = tinHuuList.stream().filter(t -> "Nam".equalsIgnoreCase(t.getGioiTinh())).count();
            long totalNu = tinHuuList.stream().filter(t -> "Nữ".equalsIgnoreCase(t.getGioiTinh())).count();
            long total = tinHuuList.size();
            
            row9.createCell(0).setCellValue(2);
            row9.createCell(1).setCellValue(1);
            row9.createCell(2).setCellValue(total);
            row9.createCell(3).setCellValue(totalNam);
            row9.createCell(4).setCellValue(totalNu);
            row9.createCell(5).setCellValue(1);
            row9.createCell(7).setCellValue(total);
            row9.createCell(10).setCellValue(1);
            row9.createCell(11).setCellValue(1);
            row9.createCell(12).setCellValue(1);
            row9.createCell(13).setCellValue(1);
            row9.createCell(14).setCellValue(1);
            
            // Row 10: Column Headers
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(40);
            String[] headers = {"STT", "STT Họ", "STT NK", "Họ và tên", "Năm sinh Nam", "Năm sinh Nữ", 
                "Nhập sinh", "Năm tín Chúa", "Năm Báp tem", "Dân tộc", "Trình độ VH", "Chuyển đi", 
                "Nhập vào", "Đi làm/học tại 6 tháng trở lên", "Bộ nhóm tín đồ 6 tháng trở lên", 
                "Chưa tin", "Qua đời", "Ghi chú"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Data rows
            int stt = 1;
            for (TinHuu tinHuu : tinHuuList) {
                Row dataRow = sheet.createRow(rowNum++);
                
                // STT
                dataRow.createCell(0).setCellValue(stt++);
                dataRow.createCell(1).setCellValue(1);
                dataRow.createCell(2).setCellValue(1);
                
                // Họ và tên
                Cell nameCell = dataRow.createCell(3);
                nameCell.setCellValue(tinHuu.getHoTen());
                nameCell.setCellStyle(dataStyle);
                
                // Năm sinh - put in correct column based on gender
                if (tinHuu.getNamSinh() != null) {
                    if ("Nam".equalsIgnoreCase(tinHuu.getGioiTinh())) {
                        // Format as date if we have full date, otherwise just year
                        if (tinHuu.getNgayGiaNhap() != null) {
                            LocalDate birthDate = LocalDate.of(tinHuu.getNamSinh(), 1, 1);
                            dataRow.createCell(4).setCellValue(formatDate(birthDate));
                        } else {
                            dataRow.createCell(4).setCellValue(tinHuu.getNamSinh());
                        }
                    } else {
                        if (tinHuu.getNgayGiaNhap() != null) {
                            LocalDate birthDate = LocalDate.of(tinHuu.getNamSinh(), 1, 1);
                            dataRow.createCell(5).setCellValue(formatDate(birthDate));
                        } else {
                            dataRow.createCell(5).setCellValue(tinHuu.getNamSinh());
                        }
                    }
                }
                
                // Nhập sinh - check from ghi chú
                if (tinHuu.getGhiChu() != null && tinHuu.getGhiChu().contains("Nhập sinh")) {
                    dataRow.createCell(6).setCellValue("X");
                }
                
                // Năm tín Chúa
                if (tinHuu.getNgayGiaNhap() != null) {
                    dataRow.createCell(7).setCellValue(tinHuu.getNgayGiaNhap().getYear());
                }
                
                // Năm Báp tem
                if (tinHuu.getNgayBaoTin() != null) {
                    dataRow.createCell(8).setCellValue(tinHuu.getNgayBaoTin().getYear());
                }
                
                // Dân tộc - extract from ghi chú
                if (tinHuu.getGhiChu() != null && tinHuu.getGhiChu().contains("Dân tộc:")) {
                    String danToc = extractDanToc(tinHuu.getGhiChu());
                    if (danToc != null) {
                        dataRow.createCell(9).setCellValue(danToc);
                    }
                }
                
                // Ghi chú
                if (tinHuu.getGhiChu() != null) {
                    String ghiChu = tinHuu.getGhiChu()
                        .replace("Nhập sinh; ", "")
                        .replace("Nhập sinh", "")
                        .replaceAll("Dân tộc: [^;]+;?\\s*", "")
                        .trim();
                    if (!ghiChu.isEmpty()) {
                        dataRow.createCell(17).setCellValue(ghiChu);
                    }
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < 18; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    private String formatDate(LocalDate date) {
        return String.format("%d/%d/%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
    
    private String extractDanToc(String ghiChu) {
        if (ghiChu.contains("Dân tộc:")) {
            int start = ghiChu.indexOf("Dân tộc:") + 9;
            int end = ghiChu.indexOf(";", start);
            if (end == -1) end = ghiChu.length();
            return ghiChu.substring(start, end).trim();
        }
        return null;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
