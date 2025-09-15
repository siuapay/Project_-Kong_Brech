package com.branch.demo.service;

import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.TaiChinhGiaoDich.LoaiGiaoDich;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportGiaoDichToExcel(List<TaiChinhGiaoDich> giaoDichList, String title) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách giao dịch");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create currency style
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.cloneStyleFrom(dataStyle);
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            // Create title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            
            // Merge title cells
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));
            
            // Create header row
            Row headerRow = sheet.createRow(2);
            String[] headers = {"STT", "Thời gian", "Loại", "Danh mục", "Nội dung", "Số tiền (VNĐ)", "Người phụ trách"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int rowNum = 3;
            int stt = 1;
            
            for (TaiChinhGiaoDich giaoDich : giaoDichList) {
                Row row = sheet.createRow(rowNum++);
                
                // STT
                Cell sttCell = row.createCell(0);
                sttCell.setCellValue(stt++);
                sttCell.setCellStyle(dataStyle);
                
                // Thời gian
                Cell thoiGianCell = row.createCell(1);
                thoiGianCell.setCellValue(giaoDich.getThoiGian().format(formatter));
                thoiGianCell.setCellStyle(dataStyle);
                
                // Loại
                Cell loaiCell = row.createCell(2);
                loaiCell.setCellValue(giaoDich.getLoai() == LoaiGiaoDich.THU ? "Thu" : "Chi");
                loaiCell.setCellStyle(dataStyle);
                
                // Danh mục
                Cell danhMucCell = row.createCell(3);
                danhMucCell.setCellValue(giaoDich.getDanhMuc() != null ? giaoDich.getDanhMuc().getTenDanhMuc() : "");
                danhMucCell.setCellStyle(dataStyle);
                
                // Nội dung
                Cell noiDungCell = row.createCell(4);
                noiDungCell.setCellValue(giaoDich.getNoiDung() != null ? giaoDich.getNoiDung() : "");
                noiDungCell.setCellStyle(dataStyle);
                
                // Số tiền
                Cell soTienCell = row.createCell(5);
                soTienCell.setCellValue(giaoDich.getSoTien().doubleValue());
                soTienCell.setCellStyle(currencyStyle);
                
                // Người phụ trách
                Cell nguoiPhuTrachCell = row.createCell(6);
                nguoiPhuTrachCell.setCellValue(giaoDich.getNguoiPhuTrach() != null ? giaoDich.getNguoiPhuTrach().getHoTen() : "");
                nguoiPhuTrachCell.setCellStyle(dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}