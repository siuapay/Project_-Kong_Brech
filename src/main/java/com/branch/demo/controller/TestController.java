// package com.branch.demo.controller;

// import com.branch.demo.domain.*;
// import com.branch.demo.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/test")
// public class TestController {

//     @Autowired
//     private TinHuuRepository tinHuuRepository;
    
//     @Autowired
//     private NhomRepository nhomRepository;
    
//     @Autowired
//     private DiemNhomRepository diemNhomRepository;
    
//     @Autowired
//     private BanNganhRepository banNganhRepository;
    
//     @Autowired
//     private NhanSuRepository nhanSuRepository;
    
//     @Autowired
//     private ChapSuRepository chapSuRepository;
    
//     @Autowired
//     private SuKienRepository suKienRepository;
    
//     @Autowired
//     private TaiChinhRepository taiChinhRepository;
    
//     @Autowired
//     private ThongBaoRepository thongBaoRepository;
    
//     @Autowired
//     private LienHeRepository lienHeRepository;

//     @GetMapping("/overview")
//     public Map<String, Object> getOverview() {
//         Map<String, Object> overview = new HashMap<>();
        
//         overview.put("totalTinHuu", tinHuuRepository.count());
//         overview.put("totalNhom", nhomRepository.count());
//         overview.put("totalDiemNhom", diemNhomRepository.count());
//         overview.put("totalBanNganh", banNganhRepository.count());
//         overview.put("totalNhanSu", nhanSuRepository.count());
//         overview.put("totalChapSu", chapSuRepository.count());
//         overview.put("totalSuKien", suKienRepository.count());
//         overview.put("totalTaiChinh", taiChinhRepository.count());
//         overview.put("totalThongBao", thongBaoRepository.count());
//         overview.put("totalLienHe", lienHeRepository.count());
        
//         return overview;
//     }

//     @GetMapping("/tin-huu/count")
//     public long getTinHuuCount() {
//         return tinHuuRepository.count();
//     }

//     @GetMapping("/nhom/count")
//     public long getNhomCount() {
//         return nhomRepository.count();
//     }

//     @GetMapping("/diem-nhom/count")
//     public long getDiemNhomCount() {
//         return diemNhomRepository.count();
//     }

//     @GetMapping("/ban-nganh")
//     public List<BanNganh> getAllBanNganh() {
//         return banNganhRepository.findAll();
//     }

//     @GetMapping("/nhan-su")
//     public List<NhanSu> getAllNhanSu() {
//         return nhanSuRepository.findAll();
//     }

//     @GetMapping("/chap-su")
//     public List<ChapSu> getAllChapSu() {
//         return chapSuRepository.findAll();
//     }

//     @GetMapping("/su-kien")
//     public List<SuKien> getAllSuKien() {
//         return suKienRepository.findAll();
//     }

//     @GetMapping("/tai-chinh")
//     public List<TaiChinh> getAllTaiChinh() {
//         return taiChinhRepository.findAll();
//     }

//     @GetMapping("/thong-bao")
//     public List<ThongBao> getAllThongBao() {
//         return thongBaoRepository.findAll();
//     }

//     @GetMapping("/lien-he")
//     public List<LienHe> getAllLienHe() {
//         return lienHeRepository.findAll();
//     }

//     @GetMapping("/thong-ke")
//     public Map<String, Object> getThongKe() {
//         Map<String, Object> thongKe = new HashMap<>();
        
//         // Thống kê tín hữu theo giới tính
//         List<Object[]> gioiTinhData = tinHuuRepository.countByGioiTinh();
//         long namCount = 0;
//         long nuCount = 0;
//         for (Object[] row : gioiTinhData) {
//             if ("NAM".equals(row[0])) namCount = (Long) row[1];
//             if ("NU".equals(row[0])) nuCount = (Long) row[1];
//         }
        
//         Map<String, Long> gioiTinhStats = new HashMap<>();
//         gioiTinhStats.put("nam", namCount);
//         gioiTinhStats.put("nu", nuCount);
//         thongKe.put("gioiTinhStats", gioiTinhStats);
        
//         // Thống kê theo nhóm tuổi
//         List<Object[]> nhomTuoiStats = tinHuuRepository.countByAgeGroup();
//         thongKe.put("nhomTuoiStats", nhomTuoiStats);
        
//         // Thống kê nhân sự theo ban ngành
//         List<Object[]> banNganhStats = nhanSuRepository.getThongKeNhanSuTheoBanNganh(NhanSu.TrangThaiNhanSu.HOAT_DONG);
//         thongKe.put("banNganhStats", banNganhStats);
        
//         return thongKe;
//     }
// }