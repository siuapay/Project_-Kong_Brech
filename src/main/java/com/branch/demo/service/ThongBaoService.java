package com.branch.demo.service;

import com.branch.demo.domain.Account;
import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.LienHe;
import com.branch.demo.domain.ThongBao;
import com.branch.demo.domain.ThongBao.LoaiThongBao;
import com.branch.demo.domain.ThongBao.TrangThaiThongBao;
import com.branch.demo.repository.AccountRepository;
import com.branch.demo.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ThongBaoService {
    
    @Autowired
    private ThongBaoRepository thongBaoRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * Tạo thông báo khi có bài viết mới cần duyệt
     */
    public void taoThongBaoBaiVietMoi(BaiViet baiViet, String nguoiGui) {
        List<Account> adminAccounts = accountRepository.findByRole(Account.Role.ADMIN);
        
        for (Account admin : adminAccounts) {
            ThongBao thongBao = new ThongBao();
            thongBao.setTieuDe("Bài viết mới cần duyệt");
            thongBao.setNoiDung(String.format("Người dùng %s đã đăng bài viết \"%s\" cần được duyệt", 
                                            nguoiGui, baiViet.getTieuDe()));
            thongBao.setLoaiThongBao(LoaiThongBao.BAI_VIET_MOI);
            thongBao.setNguoiGui(nguoiGui);
            thongBao.setNguoiNhan(admin.getUsername());
            thongBao.setDoiTuongId(baiViet.getId());
            thongBao.setDoiTuongType("BAI_VIET");
            thongBao.setLinkAction("/admin/bai-viet/view/" + baiViet.getId());
            thongBao.setIcon("fas fa-newspaper");
            thongBao.setMauSac("primary");
            
            thongBaoRepository.save(thongBao);
        }
    }
    
    /**
     * Tạo thông báo khi có báo cáo vi phạm liên hệ
     */
    public void taoThongBaoBaoCaoViPham(LienHe lienHe, String nguoiGui, String lyDo) {
        List<Account> adminAccounts = accountRepository.findByRole(Account.Role.ADMIN);
        
        for (Account admin : adminAccounts) {
            ThongBao thongBao = new ThongBao();
            thongBao.setTieuDe("Báo cáo vi phạm liên hệ");
            thongBao.setNoiDung(String.format("Moderator %s đã báo cáo vi phạm liên hệ từ %s. Lý do: %s", 
                                            nguoiGui, lienHe.getHoTen(), lyDo));
            thongBao.setLoaiThongBao(LoaiThongBao.BAO_CAO_VI_PHAM);
            thongBao.setNguoiGui(nguoiGui);
            thongBao.setNguoiNhan(admin.getUsername());
            thongBao.setDoiTuongId(lienHe.getId());
            thongBao.setDoiTuongType("LIEN_HE");
            thongBao.setLinkAction("/admin/lien-he/view/" + lienHe.getId());
            thongBao.setIcon("fas fa-exclamation-triangle");
            thongBao.setMauSac("danger");
            
            thongBaoRepository.save(thongBao);
        }
    }
    
    /**
     * Tạo thông báo khi bài viết được cập nhật
     */
    public void taoThongBaiBaiVietCapNhat(BaiViet baiViet, String nguoiGui) {
        List<Account> adminAccounts = accountRepository.findByRole(Account.Role.ADMIN);
        
        for (Account admin : adminAccounts) {
            ThongBao thongBao = new ThongBao();
            thongBao.setTieuDe("Bài viết được cập nhật");
            thongBao.setNoiDung(String.format("Người dùng %s đã cập nhật bài viết \"%s\" cần duyệt lại", 
                                            nguoiGui, baiViet.getTieuDe()));
            thongBao.setLoaiThongBao(LoaiThongBao.BAI_VIET_CAP_NHAT);
            thongBao.setNguoiGui(nguoiGui);
            thongBao.setNguoiNhan(admin.getUsername());
            thongBao.setDoiTuongId(baiViet.getId());
            thongBao.setDoiTuongType("BAI_VIET");
            thongBao.setLinkAction("/admin/bai-viet/view/" + baiViet.getId());
            thongBao.setIcon("fas fa-edit");
            thongBao.setMauSac("warning");
            
            thongBaoRepository.save(thongBao);
        }
    }
    
    /**
     * Lấy danh sách thông báo mới nhất cho admin
     */
    public List<ThongBao> getThongBaoMoiNhat(String adminUsername, int limit) {
        return thongBaoRepository.findTop10ByNguoiNhanOrderByCreatedAtDesc(
            adminUsername, PageRequest.of(0, limit));
    }
    
    /**
     * Đếm số thông báo chưa đọc
     */
    public long countThongBaoChuaDoc(String adminUsername) {
        return thongBaoRepository.countByNguoiNhanAndTrangThai(
            adminUsername, TrangThaiThongBao.CHUA_DOC);
    }
    
    /**
     * Đánh dấu thông báo là đã đọc
     */
    public void daDocThongBao(Long thongBaoId) {
        thongBaoRepository.markAsRead(thongBaoId, TrangThaiThongBao.DA_DOC);
    }
    
    /**
     * Đánh dấu tất cả thông báo là đã đọc
     */
    public void daDocTatCaThongBao(String adminUsername) {
        thongBaoRepository.markAllAsRead(adminUsername, 
                                       TrangThaiThongBao.DA_DOC, 
                                       TrangThaiThongBao.CHUA_DOC);
    }
    
    /**
     * Lấy tất cả thông báo của admin
     */
    public List<ThongBao> getTatCaThongBao(String adminUsername) {
        return thongBaoRepository.findByNguoiNhanOrderByCreatedAtDesc(adminUsername);
    }
    
    /**
     * Lấy thông báo chưa đọc
     */
    public List<ThongBao> getThongBaoChuaDoc(String adminUsername) {
        return thongBaoRepository.findByNguoiNhanAndTrangThaiOrderByCreatedAtDesc(
            adminUsername, TrangThaiThongBao.CHUA_DOC);
    }
    
    /**
     * Xóa thông báo cũ (được gọi bởi NotificationCleanupService)
     */
    public int xoaThongBaoCu() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return thongBaoRepository.deleteOldNotifications(cutoffDate);
    }
    
    /**
     * Tạo thông báo hệ thống
     */
    public void taoThongBaoHeThong(String tieuDe, String noiDung, String nguoiNhan) {
        ThongBao thongBao = new ThongBao();
        thongBao.setTieuDe(tieuDe);
        thongBao.setNoiDung(noiDung);
        thongBao.setLoaiThongBao(LoaiThongBao.HE_THONG);
        thongBao.setNguoiGui("SYSTEM");
        thongBao.setNguoiNhan(nguoiNhan);
        thongBao.setIcon("fas fa-cog");
        thongBao.setMauSac("info");
        
        thongBaoRepository.save(thongBao);
    }
    
    /**
     * Tạo thông báo cho tất cả admin
     */
    public void taoThongBaoChoTatCaAdmin(String tieuDe, String noiDung, LoaiThongBao loaiThongBao) {
        List<Account> adminAccounts = accountRepository.findByRole(Account.Role.ADMIN);
        
        for (Account admin : adminAccounts) {
            ThongBao thongBao = new ThongBao();
            thongBao.setTieuDe(tieuDe);
            thongBao.setNoiDung(noiDung);
            thongBao.setLoaiThongBao(loaiThongBao);
            thongBao.setNguoiGui("SYSTEM");
            thongBao.setNguoiNhan(admin.getUsername());
            thongBao.setIcon("fas fa-bell");
            thongBao.setMauSac("info");
            
            thongBaoRepository.save(thongBao);
        }
    }
    

}