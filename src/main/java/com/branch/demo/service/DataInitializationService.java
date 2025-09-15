package com.branch.demo.service;

import com.branch.demo.domain.*;
import com.branch.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.time.LocalTime;

@Service
public class DataInitializationService implements CommandLineRunner {

        @Autowired
        private BanNganhRepository banNganhRepository;

        @Autowired
        private DiemNhomRepository diemNhomRepository;

        @Autowired
        private NhomRepository nhomRepository;

        @Autowired
        private TinHuuRepository tinHuuRepository;

        @Autowired
        private NhanSuRepository nhanSuRepository;

        @Autowired
        private NhanSuDiemNhomRepository nhanSuDiemNhomRepository;

        @Autowired
        private ChapSuRepository chapSuRepository;

        @Autowired
        private SuKienRepository suKienRepository;

        @Autowired
        private TaiChinhRepository taiChinhRepository;

        @Autowired
        private LienHeRepository lienHeRepository;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private LoaiSuKienRepository loaiSuKienRepository;

        @Autowired
        private TaiChinhDanhMucRepository taiChinhDanhMucRepository;

        @Autowired
        private TaiChinhGiaoDichRepository taiChinhGiaoDichRepository;

        @Autowired
        private TaiChinhNamRepository taiChinhNamRepository;

        @Autowired
        private RichContentNewsService richContentNewsService;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                // Initialize default admin account if no accounts exist
                if (accountRepository.count() == 0) {
                        initializeDefaultAdmin();
                }
                
                if (banNganhRepository.count() == 0) {
                        initializeData();
                }

                // Always try to create rich content news (will check if exists)
                try {
                        richContentNewsService.createRichContentNews();
                } catch (Exception e) {
                        System.out.println("Rich content news already exists or error: " + e.getMessage());
                }
        }

        private void initializeDefaultAdmin() {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@chihoi.com");
                admin.setFullName("Quản trị viên hệ thống");
                admin.setRole(Account.Role.ADMIN);
                admin.setStatus(Account.AccountStatus.ACTIVE);
                
                accountRepository.save(admin);
                System.out.println("✅ Đã tạo tài khoản admin mặc định:");
                System.out.println("   Username: admin");
                System.out.println("   Password: admin123");
                System.out.println("   Email: admin@chihoi.com");
        }

        private void initializeData() {
                // Tạo các loại sự kiện trước
                LoaiSuKien loaiLeHoi = createLoaiSuKien("Lễ hội tôn giáo", "Các lễ hội và ngày lễ tôn giáo", "#dc3545",
                                "fas fa-church");
                LoaiSuKien loaiSinhHoatThuongKy = createLoaiSuKien("Sinh hoạt thường kỳ",
                                "Các hoạt động sinh hoạt định kỳ", "#17a2b8", "fas fa-calendar-week");
                LoaiSuKien loaiTuThien = createLoaiSuKien("Hoạt động từ thiện", "Các chương trình từ thiện và xã hội",
                                "#28a745", "fas fa-heart");
                LoaiSuKien loaiSinhHoatDacBiet = createLoaiSuKien("Sinh hoạt đặc biệt",
                                "Các hoạt động sinh hoạt đặc biệt", "#ffc107", "fas fa-star");
                LoaiSuKien loaiCongTacXaHoi = createLoaiSuKien("Công tác xã hội", "Các hoạt động công tác xã hội",
                                "#6f42c1", "fas fa-users");
                LoaiSuKien loaiKhac = createLoaiSuKien("Khác", "Các sự kiện khác", "#6c757d", "fas fa-ellipsis-h");

                // Tạo các ban ngành
                BanNganh banMucVu = createBanNganh("Ban Mục vụ", "MUCVU",
                                "Phụ trách công tác chăm sóc tâm linh và mục vụ hội thánh", "mucvu@httlkongbrech.org");
                BanNganh banGiaoDuc = createBanNganh("Ban Giáo dục", "GIAODUC",
                                "Phụ trách công tác giáo dục và đào tạo",
                                "giaoduc@httlkongbrech.org");
                BanNganh banThanhNien = createBanNganh("Ban Thanh niên", "THANHNIEN",
                                "Phụ trách công tác thanh niên và sinh viên", "thanhnien@httlkongbrech.org");
                BanNganh banHoiPhuNu = createBanNganh("Ban Hội phụ nữ", "HOIPHUNU",
                                "Phụ trách công tác hội phụ nữ và gia đình",
                                "hoiphunu@httlkongbrech.org");
                BanNganh banTaiChinh = createBanNganh("Ban Tài chính", "TAICHINH",
                                "Phụ trách quản lý tài chính và kế toán",
                                "taichinh@httlkongbrech.org");
                BanNganh banXayDung = createBanNganh("Ban Xây dựng", "XAYDUNG",
                                "Phụ trách công tác xây dựng và bảo trì",
                                "xaydung@httlkongbrech.org");
                BanNganh banTruyenThong = createBanNganh("Ban Truyền thông", "TRUYENTHONG",
                                "Phụ trách công tác truyền thông và công nghệ", "truyenthong@httlkongbrech.org");

                // Tạo chấp sự
                createChapSu("Mục sư Nguyễn Văn A", "Chấp sự trưởng", "Mục sư", "0123456789", "mucsu@httlkongbrech.org",
                                1970);
                createChapSu("Truyền đạo Trần Văn B", "Phó Chấp sự", "Truyền đạo", "0123456790",
                                "truyendao@httlkongbrech.org",
                                1975);
                createChapSu("Anh Lê Văn C", "Thư ký", "Chấp sự", "0123456791", "thuky@httlkongbrech.org", 1980);
                createChapSu("Chị Phạm Thị D", "Thủ quỹ", "Chấp sự", "0123456792", "thuquy@httlkongbrech.org", 1978);

                // Tạo điểm nhóm
                DiemNhom diemTrungTam = createDiemNhom("Kông Brech Trung tâm", "123 Đường Trần Hưng Đạo, Kông Brech");
                DiemNhom diemDong = createDiemNhom("Kông Brech Đông", "456 Đường Lê Lợi, Kông Brech Đông");
                DiemNhom diemTay = createDiemNhom("Kông Brech Tây", "789 Đường Nguyễn Huệ, Kông Brech Tây");
                DiemNhom diemEaSup = createDiemNhom("Ea Súp", "321 Thôn Ea Súp, Kông Brech");

                // Tạo nhóm
                Nhom nhom1 = createNhom("Nhóm 1", "Nhóm tín hữu trung tâm", diemTrungTam);
                Nhom nhom2 = createNhom("Nhóm 2", "Nhóm tín hữu khu vực đông", diemDong);
                Nhom nhom3 = createNhom("Nhóm 3", "Nhóm tín hữu khu vực tây", diemTay);
                Nhom nhom4 = createNhom("Nhóm 4", "Nhóm tín hữu Ea Súp", diemEaSup);

                // Tạo tín hữu
                createTinHuu("Nguyễn Văn An", 1975, "NAM", nhom1, "0123456801", "123 Đường ABC, Kông Brech",
                                "Trưởng gia đình");
                createTinHuu("Trần Thị Bình", 1978, "NU", nhom1, "0123456802", "123 Đường ABC, Kông Brech",
                                "Vợ anh An");
                createTinHuu("Lê Văn Cường", 1995, "NAM", nhom2, "0123456803", "456 Đường DEF, Kông Brech",
                                "Sinh viên");
                createTinHuu("Phạm Thị Dung", 1992, "NU", nhom2, "0123456804", "789 Đường GHI, Kông Brech",
                                "Giáo viên");
                createTinHuu("Hoàng Văn Em", 1955, "NAM", nhom3, "0123456805", "321 Đường JKL, Kông Brech", "Hưu trí");

                // Tạo nhân sự
                createNhanSu("Truyền đạo Nguyễn Văn X", NhanSu.ChucVu.TRUYEN_DAO, banMucVu, "0123456810",
                                "mucvu@httlkongbrech.org");
                createNhanSu("Anh Trần Văn Y", NhanSu.ChucVu.CHAP_SU, banGiaoDuc, "0123456811",
                                "giaoduc@httlkongbrech.org");
                createNhanSu("Chị Lê Thị Z", NhanSu.ChucVu.THU_KY, banThanhNien, "0123456812",
                                "thanhnien@httlkongbrech.org");

                // Tạo nhân sự điểm nhóm
                createNhanSuDiemNhom("Anh Nguyễn Văn Phúc", "Trưởng nhóm", diemTrungTam, "0123456820");
                createNhanSuDiemNhom("Chị Trần Thị Hạnh", "Phó nhóm", diemTrungTam, "0123456821");
                createNhanSuDiemNhom("Anh Lê Văn Tài", "Trưởng nhóm", diemDong, "0123456822");

                // Tạo sự kiện
                createSuKien("Lễ thờ phượng Chúa nhật", "Lễ thờ phượng hàng tuần", LocalDate.now().plusDays(1),
                                LocalTime.of(8, 0), LocalTime.of(10, 30), "Hội thánh chính", loaiLeHoi);
                createSuKien("Họp Ban Thanh niên", "Họp định kỳ ban thanh niên", LocalDate.now().plusDays(3),
                                LocalTime.of(19, 0), LocalTime.of(21, 0), "Phòng họp", loaiSinhHoatThuongKy);
                createSuKien("Chương trình từ thiện", "Thăm và tặng quà cho người nghèo", LocalDate.now().plusDays(7),
                                LocalTime.of(14, 0), LocalTime.of(17, 0), "Thôn Ea Súp", loaiTuThien);

                // Tạo danh mục tài chính
                TaiChinhDanhMuc dmQuyenGop = createTaiChinhDanhMuc("Quyên góp", TaiChinhDanhMuc.LoaiDanhMuc.THU, "Các khoản quyên góp từ thành viên");
                TaiChinhDanhMuc dmBanHang = createTaiChinhDanhMuc("Bán hàng", TaiChinhDanhMuc.LoaiDanhMuc.THU, "Doanh thu từ bán hàng");
                TaiChinhDanhMuc dmTaiTro = createTaiChinhDanhMuc("Tài trợ", TaiChinhDanhMuc.LoaiDanhMuc.THU, "Các khoản tài trợ từ bên ngoài");
                TaiChinhDanhMuc dmDienNuoc = createTaiChinhDanhMuc("Điện nước", TaiChinhDanhMuc.LoaiDanhMuc.CHI, "Chi phí điện nước hàng tháng");
                TaiChinhDanhMuc dmVanPhongPham = createTaiChinhDanhMuc("Văn phòng phẩm", TaiChinhDanhMuc.LoaiDanhMuc.CHI, "Chi phí mua văn phòng phẩm");
                TaiChinhDanhMuc dmSuaChua = createTaiChinhDanhMuc("Sửa chữa", TaiChinhDanhMuc.LoaiDanhMuc.CHI, "Chi phí sửa chữa cơ sở vật chất");

                // Tạo giao dịch tài chính
                createTaiChinhGiaoDich(LocalDateTime.now().minusDays(1), TaiChinhGiaoDich.LoaiGiaoDich.THU, 
                                new BigDecimal("15500000"), "Dâng hiến Chúa nhật", dmQuyenGop, null);
                createTaiChinhGiaoDich(LocalDateTime.now().minusDays(2), TaiChinhGiaoDich.LoaiGiaoDich.CHI, 
                                new BigDecimal("2000000"), "Mua vật dụng thờ phượng", dmVanPhongPham, null);
                createTaiChinhGiaoDich(LocalDateTime.now().minusDays(3), TaiChinhGiaoDich.LoaiGiaoDich.THU, 
                                new BigDecimal("5000000"), "Quyên góp từ thiện", dmQuyenGop, null);
                createTaiChinhGiaoDich(LocalDateTime.now().minusDays(5), TaiChinhGiaoDich.LoaiGiaoDich.CHI, 
                                new BigDecimal("1500000"), "Thanh toán tiền điện tháng 12", dmDienNuoc, null);
                createTaiChinhGiaoDich(LocalDateTime.now().minusDays(7), TaiChinhGiaoDich.LoaiGiaoDich.THU, 
                                new BigDecimal("3000000"), "Tài trợ từ doanh nghiệp", dmTaiTro, null);

                // Tạo giao dịch tài chính (legacy - keep for compatibility)
                createTaiChinh(TaiChinh.LoaiGiaoDich.THU, new BigDecimal("15500000"), "Dâng hiến Chúa nhật",
                                "Dâng hiến",
                                LocalDate.now().minusDays(1));
                createTaiChinh(TaiChinh.LoaiGiaoDich.CHI, new BigDecimal("2000000"), "Mua vật dụng thờ phượng",
                                "Vật dụng",
                                LocalDate.now().minusDays(2));
                createTaiChinh(TaiChinh.LoaiGiaoDich.THU, new BigDecimal("5000000"), "Quyên góp từ thiện", "Từ thiện",
                                LocalDate.now().minusDays(3));

                // // Tạo thông báo
                // createThongBao("Thông báo lễ Giáng sinh 2024", "Kính mời tất cả tín hữu tham
                // dự lễ Giáng sinh...",
                // ThongBao.LoaiThongBao.SU_KIEN, ThongBao.MucDoUuTien.CAO);
                // createThongBao("Họp Ban Chấp sự", "Thông báo họp Ban Chấp sự tháng 1/2025",
                // ThongBao.LoaiThongBao.HOP,
                // ThongBao.MucDoUuTien.BINH_THUONG);

                // Tạo liên hệ
                createLienHe("Nguyễn Văn Test", "test@example.com", "0987654321", "Góp ý",
                                "Tôi muốn góp ý về chương trình...",
                                LienHe.LoaiLienHe.GOP_Y);
                createLienHe("Trần Thị Demo", "demo@example.com", "0987654322", "Hợp tác",
                                "Tôi muốn hợp tác với hội thánh...",
                                LienHe.LoaiLienHe.HOP_TAC);
        }

        private BanNganh createBanNganh(String tenBan, String maBan, String moTa, String email) {
                BanNganh banNganh = new BanNganh(tenBan, maBan);
                banNganh.setMoTa(moTa);
                banNganh.setEmailLienHe(email);
                return banNganhRepository.save(banNganh);
        }

        private ChapSu createChapSu(String hoTen, String chucVu, String capBac, String dienThoai, String email,
                        int namSinh) {
                ChapSu chapSu = new ChapSu(hoTen, chucVu, capBac);
                chapSu.setDienThoai(dienThoai);
                chapSu.setEmail(email);
                chapSu.setNamSinh(namSinh);
                chapSu.setNhiemKyBatDau(LocalDate.of(2020, 1, 1));
                chapSu.setNhiemKyKetThuc(LocalDate.of(2025, 12, 31));
                return chapSuRepository.save(chapSu);
        }

        private DiemNhom createDiemNhom(String tenDiemNhom, String diaChi) {
                DiemNhom diemNhom = new DiemNhom(tenDiemNhom, diaChi);
                return diemNhomRepository.save(diemNhom);
        }

        private Nhom createNhom(String tenNhom, String moTa, DiemNhom diemNhom) {
                Nhom nhom = new Nhom(tenNhom);
                nhom.setMoTa(moTa);
                nhom.setDiemNhom(diemNhom);
                return nhomRepository.save(nhom);
        }

        private TinHuu createTinHuu(String hoTen, int namSinh, String gioiTinh, Nhom nhom, String dienThoai,
                        String diaChi,
                        String ghiChu) {
                TinHuu tinHuu = new TinHuu(hoTen);
                tinHuu.setNamSinh(namSinh);
                tinHuu.setGioiTinh(gioiTinh);
                tinHuu.setNhom(nhom);
                tinHuu.setDienThoai(dienThoai);
                tinHuu.setDiaChi(diaChi);
                tinHuu.setGhiChu(ghiChu);
                tinHuu.setNgayGiaNhap(LocalDate.now().minusYears(2));
                return tinHuuRepository.save(tinHuu);
        }

        private NhanSu createNhanSu(String hoTen, NhanSu.ChucVu chucVu, BanNganh banNganh, String dienThoai,
                        String email) {
                NhanSu nhanSu = new NhanSu(hoTen, chucVu);
                nhanSu.setBanNganh(banNganh);
                nhanSu.setDienThoai(dienThoai);
                nhanSu.setEmail(email);
                nhanSu.setNgayBatDauPhucVu(LocalDate.now().minusYears(1));
                return nhanSuRepository.save(nhanSu);
        }

        private NhanSuDiemNhom createNhanSuDiemNhom(String hoTen, String chucVu, DiemNhom diemNhom, String dienThoai) {
                NhanSuDiemNhom nhanSu = new NhanSuDiemNhom(hoTen, chucVu, diemNhom);
                nhanSu.setDienThoai(dienThoai);
                nhanSu.setNgayBatDauPhucVu(LocalDate.now().minusMonths(6));
                return nhanSuDiemNhomRepository.save(nhanSu);
        }

        private LoaiSuKien createLoaiSuKien(String tenLoai, String moTa, String mauSac, String icon) {
                // Kiểm tra xem loại sự kiện đã tồn tại chưa
                if (loaiSuKienRepository.existsByTenLoai(tenLoai)) {
                        return loaiSuKienRepository.findByKichHoatTrueOrderByTenLoaiAsc().stream()
                                        .filter(l -> l.getTenLoai().equals(tenLoai))
                                        .findFirst()
                                        .orElse(null);
                }

                LoaiSuKien loaiSuKien = new LoaiSuKien(tenLoai, moTa, mauSac);
                loaiSuKien.setIcon(icon);
                loaiSuKien.setKichHoat(true);
                return loaiSuKienRepository.save(loaiSuKien);
        }

        private SuKien createSuKien(String tenSuKien, String moTa, LocalDate ngayDienRa, LocalTime gioBatDau,
                        LocalTime gioKetThuc, String diaDiem, LoaiSuKien loaiSuKien) {
                LocalDateTime ngayGioDienRa = LocalDateTime.of(ngayDienRa, gioBatDau);
                SuKien suKien = new SuKien(tenSuKien, ngayGioDienRa, loaiSuKien);
                suKien.setMoTa(moTa);
                suKien.setDiaDiem(diaDiem);
                suKien.setTrangThai(SuKien.TrangThaiSuKien.DANG_CHUAN_BI);
                return suKienRepository.save(suKien);
        }

        private TaiChinh createTaiChinh(TaiChinh.LoaiGiaoDich loaiGiaoDich, BigDecimal soTien, String moTa,
                        String danhMuc,
                        LocalDate ngayGiaoDich) {
                TaiChinh taiChinh = new TaiChinh(loaiGiaoDich, soTien, moTa);
                taiChinh.setDanhMuc(danhMuc);
                taiChinh.setNgayGiaoDich(ngayGiaoDich);
                taiChinh.setPhuongThucThanhToan(TaiChinh.PhuongThucThanhToan.TIEN_MAT);
                return taiChinhRepository.save(taiChinh);
        }

        // private ThongBao createThongBao(String tieuDe, String noiDung,
        // ThongBao.LoaiThongBao loaiThongBao,
        // ThongBao.MucDoUuTien mucDoUuTien) {
        // ThongBao thongBao = new ThongBao(tieuDe, noiDung, loaiThongBao);
        // thongBao.setMucDoUuTien(mucDoUuTien);
        // thongBao.setNguoiGui("Admin");
        // thongBao.setDoiTuongNhan("Tất cả");
        // thongBao.setNgayGui(LocalDateTime.now());
        // thongBao.setTrangThai(ThongBao.TrangThaiThongBao.DA_GUI);
        // return thongBaoRepository.save(thongBao);
        // }

        private LienHe createLienHe(String hoTen, String email, String dienThoai, String chuDe, String noiDung,
                        LienHe.LoaiLienHe loaiLienHe) {
                LienHe lienHe = new LienHe(hoTen, email, chuDe, noiDung);
                lienHe.setDienThoai(dienThoai);
                lienHe.setLoaiLienHe(loaiLienHe);
                return lienHeRepository.save(lienHe);
        }
      
  // Financial Management Helper Methods
        private TaiChinhDanhMuc createTaiChinhDanhMuc(String tenDanhMuc, TaiChinhDanhMuc.LoaiDanhMuc loai, String moTa) {
                TaiChinhDanhMuc danhMuc = new TaiChinhDanhMuc(tenDanhMuc, loai, moTa);
                return taiChinhDanhMucRepository.save(danhMuc);
        }

        private TaiChinhGiaoDich createTaiChinhGiaoDich(LocalDateTime thoiGian, TaiChinhGiaoDich.LoaiGiaoDich loai, 
                        BigDecimal soTien, String noiDung, TaiChinhDanhMuc danhMuc, NhanSu nguoiPhuTrach) {
                TaiChinhGiaoDich giaoDich = new TaiChinhGiaoDich(thoiGian, loai, soTien, noiDung, danhMuc, nguoiPhuTrach);
                TaiChinhGiaoDich saved = taiChinhGiaoDichRepository.save(giaoDich);
                
                // Update year summary
                updateTaiChinhNam(thoiGian.getYear(), loai, soTien);
                
                return saved;
        }

        private void updateTaiChinhNam(Integer nam, TaiChinhGiaoDich.LoaiGiaoDich loai, BigDecimal soTien) {
                TaiChinhNam taiChinhNam = taiChinhNamRepository.findByNam(nam)
                        .orElse(new TaiChinhNam(nam));
                
                if (loai == TaiChinhGiaoDich.LoaiGiaoDich.THU) {
                        taiChinhNam.addThu(soTien);
                } else {
                        taiChinhNam.addChi(soTien);
                }
                
                taiChinhNamRepository.save(taiChinhNam);
        }
}