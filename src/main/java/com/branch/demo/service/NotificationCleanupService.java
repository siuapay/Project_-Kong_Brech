package com.branch.demo.service;

import com.branch.demo.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationCleanupService {
    
    @Autowired
    private ThongBaoRepository thongBaoRepository;
    
    /**
     * Tự động xóa thông báo cũ hơn 30 ngày
     * Chạy mỗi ngày lúc 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            int deletedCount = thongBaoRepository.deleteOldNotifications(cutoffDate);
            
            if (deletedCount > 0) {
                System.out.println("Đã xóa " + deletedCount + " thông báo cũ hơn 30 ngày");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa thông báo cũ: " + e.getMessage());
        }
    }
    
    /**
     * Method để admin có thể gọi thủ công
     */
    @Transactional
    public int manualCleanup() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return thongBaoRepository.deleteOldNotifications(cutoffDate);
    }
    
    /**
     * Xóa thông báo cũ hơn số ngày chỉ định
     */
    @Transactional
    public int cleanupNotificationsOlderThan(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return thongBaoRepository.deleteOldNotifications(cutoffDate);
    }
}