package com.branch.demo.service;

import com.branch.demo.domain.TaiChinhDanhMuc;
import com.branch.demo.domain.TaiChinhGiaoDich;
import com.branch.demo.domain.TaiChinhNam;
import com.branch.demo.domain.NhanSu;
import com.branch.demo.dto.GiaoDichSearchCriteria;
import com.branch.demo.repository.TaiChinhDanhMucRepository;
import com.branch.demo.repository.TaiChinhGiaoDichRepository;
import com.branch.demo.repository.TaiChinhNamRepository;
import com.branch.demo.repository.NhanSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaiChinhService {
    
    @Autowired
    private TaiChinhDanhMucRepository danhMucRepository;
    
    @Autowired
    private TaiChinhGiaoDichRepository giaoDichRepository;
    
    @Autowired
    private TaiChinhNamRepository namRepository;
    
    @Autowired
    private NhanSuRepository nhanSuRepository;
    
    // ==================== DANH MỤC MANAGEMENT ====================
    
    public List<TaiChinhDanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAllOrderByLoaiAndTenDanhMuc();
    }
    
    public Page<TaiChinhDanhMuc> getDanhMucPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("loai").and(Sort.by("tenDanhMuc")));
        if (search != null && !search.trim().isEmpty()) {
            return danhMucRepository.findByTenDanhMucContainingIgnoreCase(search.trim(), pageable);
        }
        return danhMucRepository.findAll(pageable);
    }
    
    public Page<TaiChinhDanhMuc> getDanhMucPageWithFilters(int page, String search, TaiChinhDanhMuc.LoaiDanhMuc loai) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("tenDanhMuc"));
        return danhMucRepository.findByMultipleConditions(search, loai, pageable);
    }
    
    public long countGiaoDichByDanhMuc(Long danhMucId) {
        return giaoDichRepository.countByDanhMucId(danhMucId);
    }
    
    public TaiChinhDanhMuc getDanhMucById(Long id) {
        return danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }
    
    public List<TaiChinhDanhMuc> getDanhMucByLoai(TaiChinhDanhMuc.LoaiDanhMuc loai) {
        return danhMucRepository.findByLoaiOrderByTenDanhMucAsc(loai);
    }
    
    public TaiChinhDanhMuc saveDanhMuc(TaiChinhDanhMuc danhMuc) {
        // Validate tên danh mục không trùng lặp
        if (danhMuc.getId() == null) {
            if (danhMucRepository.existsByTenDanhMucIgnoreCase(danhMuc.getTenDanhMuc())) {
                throw new RuntimeException("Tên danh mục đã tồn tại: " + danhMuc.getTenDanhMuc());
            }
        } else {
            if (danhMucRepository.existsByTenDanhMucIgnoreCaseAndIdNot(danhMuc.getTenDanhMuc(), danhMuc.getId())) {
                throw new RuntimeException("Tên danh mục đã tồn tại: " + danhMuc.getTenDanhMuc());
            }
        }
        
        return danhMucRepository.save(danhMuc);
    }
    
    public void deleteDanhMuc(Long id) {
        // Kiểm tra danh mục có đang được sử dụng không
        if (danhMucRepository.isBeingUsed(id)) {
            throw new RuntimeException("Không thể xóa danh mục đang được sử dụng trong giao dịch");
        }
        
        danhMucRepository.deleteById(id);
    }
    
    // ==================== GIAO DỊCH MANAGEMENT ====================
    
    public Page<TaiChinhGiaoDich> getGiaoDichPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "thoiGian"));
        if (search != null && !search.trim().isEmpty()) {
            return giaoDichRepository.findByNoiDungContainingIgnoreCase(search.trim(), pageable);
        }
        return giaoDichRepository.findAll(pageable);
    }
    
    public Page<TaiChinhGiaoDich> getGiaoDichPageWithFilters(int page, String search, 
            TaiChinhGiaoDich.LoaiGiaoDich loai, Long danhMucId, Long nguoiPhuTrachId,
            LocalDateTime tuNgay, LocalDateTime denNgay, BigDecimal soTienMin, BigDecimal soTienMax) {
        
        // Bỏ Sort khỏi Pageable vì query đã có ORDER BY
        Pageable pageable = PageRequest.of(page, 20);
        return giaoDichRepository.findByMultipleConditions(loai, danhMucId, nguoiPhuTrachId, 
                search, tuNgay, denNgay, soTienMin, soTienMax, pageable);
    }
    
    public List<TaiChinhGiaoDich> getAllGiaoDichWithFilters(GiaoDichSearchCriteria criteria) {
        // Convert criteria to method parameters
        TaiChinhGiaoDich.LoaiGiaoDich loai = null;
        if (criteria.getLoai() != null && !criteria.getLoai().isEmpty()) {
            try {
                loai = TaiChinhGiaoDich.LoaiGiaoDich.valueOf(criteria.getLoai());
            } catch (IllegalArgumentException e) {
                // Invalid loai, ignore
            }
        }
        
        // Build date range from nam/thang
        LocalDateTime tuNgay = null;
        LocalDateTime denNgay = null;
        
        if (criteria.getNam() != null) {
            if (criteria.getThang() != null) {
                // Specific month
                tuNgay = LocalDateTime.of(criteria.getNam(), criteria.getThang(), 1, 0, 0);
                denNgay = tuNgay.plusMonths(1).minusSeconds(1);
            } else {
                // Whole year
                tuNgay = LocalDateTime.of(criteria.getNam(), 1, 1, 0, 0);
                denNgay = LocalDateTime.of(criteria.getNam(), 12, 31, 23, 59, 59);
            }
        }
        
        // Use repository method to get all records (no pagination)
        return giaoDichRepository.findAllByMultipleConditions(loai, criteria.getDanhMucId(), 
                criteria.getNguoiPhuTrachId(), criteria.getNoiDung(), tuNgay, denNgay, null, null);
    }
    
    public TaiChinhGiaoDich getGiaoDichById(Long id) {
        return giaoDichRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + id));
    }
    
    public List<TaiChinhGiaoDich> getGiaoDichByNam(Integer nam) {
        return giaoDichRepository.findByNam(nam);
    }
    
    public Page<TaiChinhGiaoDich> getGiaoDichByNam(Integer nam, int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "thoiGian"));
        return giaoDichRepository.findByNam(nam, pageable);
    }
    
    public TaiChinhGiaoDich saveGiaoDich(TaiChinhGiaoDich giaoDich) {
        // Validate business rules
        validateGiaoDich(giaoDich);
        
        // Lưu giao dịch
        TaiChinhGiaoDich savedGiaoDich = giaoDichRepository.save(giaoDich);
        
        // Cập nhật tổng năm
        updateNamSummary(savedGiaoDich.getNam());
        
        return savedGiaoDich;
    }
    
    public TaiChinhGiaoDich updateGiaoDich(TaiChinhGiaoDich giaoDich) {
        // Lấy giao dịch cũ để so sánh
        TaiChinhGiaoDich oldGiaoDich = getGiaoDichById(giaoDich.getId());
        Integer oldNam = oldGiaoDich.getNam();
        
        // Validate business rules
        validateGiaoDich(giaoDich);
        
        // Lưu giao dịch mới
        TaiChinhGiaoDich savedGiaoDich = giaoDichRepository.save(giaoDich);
        
        // Cập nhật tổng năm (cả năm cũ và năm mới nếu khác nhau)
        updateNamSummary(oldNam);
        if (!oldNam.equals(savedGiaoDich.getNam())) {
            updateNamSummary(savedGiaoDich.getNam());
        }
        
        return savedGiaoDich;
    }
    
    public void deleteGiaoDich(Long id) {
        TaiChinhGiaoDich giaoDich = getGiaoDichById(id);
        Integer nam = giaoDich.getNam();
        
        giaoDichRepository.deleteById(id);
        
        // Cập nhật tổng năm
        updateNamSummary(nam);
    }
    
    private void validateGiaoDich(TaiChinhGiaoDich giaoDich) {
        if (giaoDich.getDanhMuc() == null) {
            throw new RuntimeException("Danh mục không được để trống");
        }
        
        if (giaoDich.getSoTien() == null || giaoDich.getSoTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền phải lớn hơn 0");
        }
        
        if (giaoDich.getThoiGian() == null) {
            giaoDich.setThoiGian(LocalDateTime.now());
        }
        
        // Validate loại giao dịch khớp với loại danh mục
        if (giaoDich.getLoai() != null && giaoDich.getDanhMuc() != null) {
            boolean isValid = (giaoDich.getLoai() == TaiChinhGiaoDich.LoaiGiaoDich.THU && 
                              giaoDich.getDanhMuc().getLoai() == TaiChinhDanhMuc.LoaiDanhMuc.THU) ||
                             (giaoDich.getLoai() == TaiChinhGiaoDich.LoaiGiaoDich.CHI && 
                              giaoDich.getDanhMuc().getLoai() == TaiChinhDanhMuc.LoaiDanhMuc.CHI);
            
            if (!isValid) {
                String errorMsg = String.format("Loại giao dịch không khớp với loại danh mục. " +
                    "GiaoDich.loai=%s, DanhMuc.loai=%s, DanhMuc.id=%d, DanhMuc.ten=%s", 
                    giaoDich.getLoai(), 
                    giaoDich.getDanhMuc().getLoai(),
                    giaoDich.getDanhMuc().getId(),
                    giaoDich.getDanhMuc().getTenDanhMuc());
                throw new RuntimeException(errorMsg);
            }
        }
    }
    
    // ==================== NĂM SUMMARY MANAGEMENT ====================
    
    public List<TaiChinhNam> getAllNam() {
        return namRepository.findAllByOrderByNamDesc();
    }
    
    public List<TaiChinhNam> getNamWithData() {
        return namRepository.findNamWithData();
    }
    
    public TaiChinhNam getNamById(Integer nam) {
        return namRepository.findByNam(nam)
                .orElse(new TaiChinhNam(nam));
    }
    
    public TaiChinhNam saveNam(TaiChinhNam nam) {
        return namRepository.save(nam);
    }
    
    public void updateNamSummary(Integer nam) {
        if (nam == null) return;
        
        // Tính tổng thu và chi từ giao dịch
        BigDecimal tongThu = giaoDichRepository.getTongThuByNam(nam);
        BigDecimal tongChi = giaoDichRepository.getTongChiByNam(nam);
        
        // Tìm hoặc tạo mới record năm
        TaiChinhNam taiChinhNam = namRepository.findByNam(nam)
                .orElse(new TaiChinhNam(nam));
        
        // Cập nhật số liệu
        taiChinhNam.setTongThu(tongThu);
        taiChinhNam.setTongChi(tongChi);
        
        // Lưu vào database
        namRepository.save(taiChinhNam);
    }
    
    public void recalculateAllNamSummaries() {
        List<Integer> years = giaoDichRepository.findDistinctYears();
        for (Integer year : years) {
            updateNamSummary(year);
        }
    }
    
    // ==================== THỐNG KÊ VÀ BÁO CÁO ====================
    
    public List<Object[]> getThongKeTheoThang(Integer nam) {
        return giaoDichRepository.getThongKeTheoThang(nam);
    }
    
    public List<Object[]> getThongKeTheoDanhMuc(Integer nam) {
        List<Object[]> result = giaoDichRepository.getThongKeTheoDanhMuc(nam);
        return result != null ? result : new ArrayList<>();
    }
    
    public List<Object[]> getThongKeTongQuan() {
        return namRepository.getThongKeTongQuan();
    }
    
    public Page<TaiChinhGiaoDich> getTopGiaoDichThu(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return giaoDichRepository.findTopByLoaiOrderBySoTienDesc(TaiChinhGiaoDich.LoaiGiaoDich.THU, pageable);
    }
    
    public Page<TaiChinhGiaoDich> getTopGiaoDichChi(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return giaoDichRepository.findTopByLoaiOrderBySoTienDesc(TaiChinhGiaoDich.LoaiGiaoDich.CHI, pageable);
    }
    
    public List<Integer> getAvailableYears() {
        return giaoDichRepository.findDistinctYears();
    }
    
    // ==================== HELPER METHODS ====================
    
    public List<NhanSu> getAllNhanSu() {
        return nhanSuRepository.findAll();
    }
    
    public List<NhanSu> getActiveNhanSu() {
        return nhanSuRepository.findByTrangThai(NhanSu.TrangThaiNhanSu.HOAT_DONG);
    }
    
    public boolean isDanhMucBeingUsed(Long danhMucId) {
        return danhMucRepository.isBeingUsed(danhMucId);
    }
    
    public long getTotalTransactionCount() {
        return giaoDichRepository.getTotalTransactionCount();
    }
    
    public long getTransactionCountByYear(Integer nam) {
        return giaoDichRepository.countByNam(nam);
    }
    
    public BigDecimal getTotalThuAllTime() {
        return namRepository.getTotalThu();
    }
    
    public BigDecimal getTotalChiAllTime() {
        return namRepository.getTotalChi();
    }
    
    public BigDecimal getTotalSoDuAllTime() {
        return namRepository.getTotalSoDu();
    }

  
  public long getSoGiaoDichThuByNam(Integer nam) {
        return giaoDichRepository.countByLoaiAndNam(TaiChinhGiaoDich.LoaiGiaoDich.THU, nam);
    }
    
    public long getSoGiaoDichChiByNam(Integer nam) {
        return giaoDichRepository.countByLoaiAndNam(TaiChinhGiaoDich.LoaiGiaoDich.CHI, nam);
    }
    
    public long getTotalGiaoDichByNam(Integer nam) {
        return giaoDichRepository.countByNam(nam);
    }
    }    