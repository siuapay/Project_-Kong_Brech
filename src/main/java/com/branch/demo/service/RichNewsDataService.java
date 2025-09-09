package com.branch.demo.service;

import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class RichNewsDataService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private BaiVietRepository baiVietRepository;

    @PostConstruct
    @Transactional
    public void initRichData() {
        try {
            Thread.sleep(4000); // Wait for tables

            if (danhMucRepository.count() == 0) {
                initDanhMuc();
                System.out.println("✅ Đã khởi tạo danh mục tin tức");
            }

            if (baiVietRepository.count() == 0) {
                initRichBaiViet();
                System.out.println("✅ Đã khởi tạo bài viết phong phú");
            }
        } catch (Exception e) {
            System.out.println("⚠️ RichNewsDataService error: " + e.getMessage());
        }
    }

    private void initDanhMuc() {
        List<DanhMuc> danhMucList = Arrays.asList(
                createDanhMuc("Tin Tức Chung", "tin-tuc-chung", "Các tin tức chung về hoạt động của chi hội thánh",
                        "#007bff", "newspaper", 1),
                createDanhMuc("Hoạt Động Thánh", "hoat-dong-thanh", "Các hoạt động thánh lễ, cầu nguyện", "#28a745",
                        "pray", 2),
                createDanhMuc("Sự Kiện", "su-kien", "Các sự kiện đặc biệt, lễ hội", "#ffc107", "calendar", 3),
                createDanhMuc("Chia Sẻ", "chia-se", "Chia sẻ kinh nghiệm, lời chứng", "#17a2b8", "heart", 4),
                createDanhMuc("Thông Báo", "thong-bao", "Các thông báo quan trọng", "#dc3545", "bullhorn", 5));
        danhMucRepository.saveAll(danhMucList);
    }

    private void initRichBaiViet() {
        List<DanhMuc> danhMucList = danhMucRepository.findAll();
        if (danhMucList.isEmpty())
            return;

        List<BaiViet> baiVietList = Arrays.asList(
                createBaiViet1(danhMucList),
                createBaiViet2(danhMucList),
                createBaiViet3(danhMucList),
                createBaiViet4(danhMucList),
                createBaiViet5(danhMucList));

        baiVietRepository.saveAll(baiVietList);
    }

    private BaiViet createBaiViet1(List<DanhMuc> danhMucList) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Lễ Giáng Sinh 2023 - Niềm Vui Tràn Ngập Chi Hội Thánh");
        baiViet.setSlug("le-giang-sinh-2023-niem-vui-tran-ngap");
        baiViet.setTomTat(
                "Lễ Giáng Sinh năm 2023 đã diễn ra thành công tốt đẹp với sự tham gia của hơn 800 tín hữu, mang đến không khí ấm áp và tràn đầy niềm vui.");

        String noiDung = "<h3>Không khí Giáng Sinh tràn ngập</h3>" +
                "<p>Tối ngày 24 tháng 12 năm 2023, chi hội thánh chúng ta đã tổ chức buổi lễ Giáng Sinh đặc biệt với chủ đề \"Ánh sáng của thế gian\". Buổi lễ bắt đầu từ 19h00 với sự tham gia của hơn 800 tín hữu từ khắp các điểm nhóm.</p>"
                +

                "<h3>Chương trình phong phú và ý nghĩa</h3>" +
                "<p>Chương trình Giáng Sinh năm nay được chia thành nhiều phần với các hoạt động đa dạng:</p>" +
                "<ul>" +
                "<li><strong>Thánh ca Giáng Sinh:</strong> Ban hát lễ đã trình bày những bài thánh ca truyền thống như \"Đêm Thánh\", \"Niềm Vui Giáng Sinh\"</li>"
                +
                "<li><strong>Kịch Giáng Sinh:</strong> Các em thiếu nhi đã biểu diễn vở kịch về câu chuyện Chúa Giêsu ra đời</li>"
                +
                "<li><strong>Chia sẻ Lời Chúa:</strong> Mục sư Nguyễn Văn Minh đã chia sẻ về ý nghĩa sâu sắc của Giáng Sinh</li>"
                +
                "</ul>" +

                "<h3>Những khoảnh khắc đáng nhớ</h3>" +
                "<p>Điểm nhấn của buổi lễ là màn trình diễn của dàn hợp xướng gồm 50 thành viên từ các nhóm khác nhau. Tiếng hát hòa quyện tạo nên không khí thiêng liêng và cảm động.</p>"
                +

                "<p>Đặc biệt, trong buổi lễ còn có nghi thức thắp nến cầu nguyện, nơi mỗi gia đình đều được tham gia thắp nến và cầu nguyện cho năm mới an lành.</p>"
                +

                "<h3>Tiệc thân mật và trao quà</h3>" +
                "<p>Sau phần lễ chính thức, tất cả mọi người đã cùng nhau thưởng thức bữa tiệc thân mật với các món ăn truyền thống của mùa Giáng Sinh. Ban tổ chức cũng đã chuẩn bị quà Giáng Sinh cho tất cả các em thiếu nhi tham dự.</p>"
                +

                "<h3>Lời cảm ơn và hẹn gặp lại</h3>" +
                "<p>Ban tổ chức xin chân thành cảm ơn tất cả anh chị em tín hữu đã tham gia và đóng góp để buổi lễ thành công. Chúng ta hẹn gặp lại trong các hoạt động sắp tới của chi hội thánh.</p>";

        baiViet.setNoiDung(noiDung);
        baiViet.setDanhMuc(danhMucList.get(1)); // Hoạt Động Thánh
        baiViet.setTacGia("Mục sư Nguyễn Văn Minh");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.MUC_SU);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNoiBat(true);
        baiViet.setLuotXem(1250L);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(5));

        // Add rich media content
        baiViet.setAnhDaiDien("https://picsum.photos/800/400?random=christmas2023");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas1");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas2");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas3");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas4");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas5");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=christmas6");

        // Add videos
        baiViet.addVideo("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        baiViet.addVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

        return baiViet;
    }

    private DanhMuc createDanhMuc(String ten, String slug, String moTa, String mauSac, String icon, int thuTu) {
        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setTenDanhMuc(ten);
        danhMuc.setSlug(slug);
        danhMuc.setMoTa(moTa);
        danhMuc.setMauSac(mauSac);
        danhMuc.setIcon(icon);
        danhMuc.setThuTu(thuTu);
        danhMuc.setTrangThai(DanhMuc.TrangThaiDanhMuc.HOAT_DONG);
        return danhMuc;
    }

    private BaiViet createBaiViet2(List<DanhMuc> danhMucList) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Chương Trình Từ Thiện 'Tình Yêu Lan Tỏa' - Mang Ấm No Đến Bà Con Nghèo");
        baiViet.setSlug("chuong-trinh-tu-thien-tinh-yeu-lan-toa");
        baiViet.setTomTat(
                "Chương trình từ thiện 'Tình Yêu Lan Tỏa' đã thành công tốt đẹp, mang đến sự giúp đỡ thiết thực cho 150 gia đình có hoàn cảnh khó khăn tại 5 xã vùng sâu.");

        String noiDung = "<h3>Khởi động chương trình ý nghĩa</h3>" +
                "<p>Sáng ngày 15 tháng 1 năm 2024, chi hội thánh đã chính thức khởi động chương trình từ thiện 'Tình Yêu Lan Tỏa' với mục tiêu hỗ trợ những gia đình có hoàn cảnh khó khăn trong vùng.</p>"
                +

                "<p>Chương trình được chuẩn bị kỹ lưỡng trong suốt 2 tháng với sự tham gia tích cực của hơn 200 tình nguyện viên từ các nhóm khác nhau trong chi hội thánh.</p>"
                +

                "<h3>Những con số ấn tượng</h3>" +
                "<p>Sau 3 ngày triển khai, chương trình đã đạt được những kết quả đáng khích lệ:</p>" +
                "<ul>" +
                "<li><strong>150 gia đình</strong> được hỗ trợ trực tiếp</li>" +
                "<li><strong>300 phần quà</strong> gồm gạo, dầu ăn, mì tôm, và các nhu yếu phẩm</li>" +
                "<li><strong>50 triệu đồng</strong> tiền mặt được trao tặng</li>" +
                "<li><strong>100 bộ quần áo</strong> cho trẻ em và người già</li>" +
                "<li><strong>50 chiếc xe đạp</strong> cho các em học sinh nghèo</li>" +
                "</ul>" +

                "<h3>Những câu chuyện cảm động</h3>" +
                "<p>Trong chuyến đi, đoàn từ thiện đã gặp gỡ bà Nguyễn Thị Hoa (72 tuổi) ở xã Tân Phú, một người phụ nữ góa chồng nuôi 2 cháu nhỏ. Khi nhận được phần quà, bà không kìm được nước mắt và nói: 'Cảm ơn các con đã không quên những người như bà. Đây thực sự là món quà quý giá nhất bà nhận được trong năm qua.'</p>"
                +

                "<p>Hay như câu chuyện của em Nguyễn Văn Tú (12 tuổi), một học sinh lớp 6 ở xã Hòa Bình. Em đã phải nghỉ học 2 tháng vì không có xe đạp để đi học (nhà cách trường 8km). Khi nhận được chiếc xe đạp mới, em vui mừng khôn xiết và hứa sẽ học thật chăm chỉ.</p>"
                +

                "<h3>Hoạt động khám bệnh miễn phí</h3>" +
                "<p>Bên cạnh việc trao quà, đoàn còn tổ chức khám bệnh miễn phí cho bà con với sự tham gia của 5 bác sĩ tình nguyện. Hơn 200 người dân đã được khám và cấp thuốc miễn phí.</p>"
                +

                "<h3>Lời cảm ơn và kế hoạch tương lai</h3>" +
                "<p>Ban tổ chức xin chân thành cảm ơn tất cả anh chị em đã đóng góp và tham gia chương trình. Đặc biệt cảm ơn các nhà hài lòng đã tài trợ kinh phí và phương tiện.</p>"
                +

                "<p>Chi hội thánh dự định sẽ tổ chức chương trình từ thiện định kỳ 6 tháng/lần để tiếp tục mang yêu thương đến với cộng đồng.</p>";

        baiViet.setNoiDung(noiDung);
        baiViet.setDanhMuc(danhMucList.get(2)); // Sự Kiện
        baiViet.setTacGia("Trưởng Ban Từ Thiện Phạm Thị Lan");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.NHAN_SU);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNoiBat(true);
        baiViet.setLuotXem(890L);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(3));

        // Rich media content
        baiViet.setAnhDaiDien("https://picsum.photos/800/400?random=charity2024");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity1");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity2");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity3");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity4");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity5");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity6");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity7");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=charity8");

        baiViet.addVideo("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4");

        return baiViet;
    }

    private BaiViet createBaiViet3(List<DanhMuc> danhMucList) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Hành Trình Đức Tin 30 Năm - Lời Chứng Của Anh Phêrô Nguyễn");
        baiViet.setSlug("hanh-trinh-duc-tin-30-nam-anh-phero");
        baiViet.setTomTat(
                "Anh Phêrô Nguyễn chia sẻ về hành trình 30 năm theo Chúa, từ một người lạc lối đến việc trở thành trụ cột trong chi hội thánh và cộng đồng.");

        String noiDung = "<h3>Khởi đầu từ những ngày tăm tối</h3>" +
                "<p>Năm 1994, khi tôi 25 tuổi, cuộc sống của tôi đang ở trong giai đoạn khó khăn nhất. Thất nghiệp, nợ nần chồng chất, và quan trọng nhất là tôi đã mất phương hướng trong cuộc sống.</p>"
                +

                "<p>Tôi từng là một người hay nóng giận, thường xuyên cãi vã với vợ con, và không biết yêu thương gia đình. Những ngày tháng đó, tôi cảm thấy như đang sống trong bóng tối, không thấy được ánh sáng hy vọng nào.</p>"
                +

                "<h3>Cuộc gặp gỡ định mệnh</h3>" +
                "<p>Một ngày tháng 8 năm 1994, khi tôi đang ngồi buồn bã trong công viên, một người bạn cũ đã đến ngồi cạnh và chia sẻ với tôi về đức tin. Lúc đầu tôi không quan tâm lắm, nhưng những lời chia sẻ chân thành của anh ấy đã chạm đến trái tim tôi.</p>"
                +

                "<p>'Phêrô ơi, Chúa yêu thương anh và muốn ban cho anh một cuộc sống mới', anh ấy nói. Những lời này như một tia sáng chiếu vào tâm hồn tăm tối của tôi.</p>"
                +

                "<h3>Những thay đổi đầu tiên</h3>" +
                "<p>Sau khi quyết định theo Chúa, tôi bắt đầu tham gia các buổi sinh hoạt tại chi hội thánh. Ban đầu, tôi cảm thấy khá lạ lẫm với môi trường mới này, nhưng sự ấm áp và yêu thương của anh chị em đã giúp tôi dần hòa nhập.</p>"
                +

                "<p>Thay đổi đầu tiên mà vợ tôi nhận ra là tôi bớt nóng giận hơn. Tôi bắt đầu lắng nghe vợ con nhiều hơn, và học cách tha thứ cho những lỗi lầm nhỏ nhặt trong gia đình.</p>"
                +

                "<h3>Hành trình phục vụ</h3>" +
                "<p>Năm 1998, tôi được mời tham gia ban hát lễ. Mặc dù không có giọng hát hay, nhưng tôi cảm thấy đây là cách để tôi phục vụ Chúa và cộng đồng. Từ đó, tôi bắt đầu tham gia tích cực hơn vào các hoạt động của chi hội thánh.</p>"
                +

                "<p>Năm 2005, tôi được bầu làm trưởng nhóm. Đây là một trách nhiệm lớn, nhưng cũng là cơ hội để tôi học hỏi và phát triển bản thân. Tôi đã học cách lãnh đạo với tình yêu thương, không phải bằng quyền lực.</p>"
                +

                "<h3>Những thử thách và bài học</h3>" +
                "<p>Năm 2010, gia đình tôi gặp phải khó khăn tài chính lớn khi công ty tôi làm việc phải đóng cửa. Đây là lúc đức tin của tôi bị thử thách mạnh mẽ nhất. Tôi đã từng hoài nghi và tự hỏi tại sao Chúa lại để những điều này xảy ra.</p>"
                +

                "<p>Nhưng chính trong giai đoạn khó khăn này, tôi đã cảm nhận được sự yêu thương và hỗ trợ từ cộng đồng chi hội thánh. Anh chị em đã giúp đỡ gia đình tôi vượt qua khó khăn, và điều này đã củng cố thêm đức tin của tôi.</p>"
                +

                "<h3>Ngày hôm nay</h3>" +
                "<p>Sau 30 năm theo Chúa, tôi có thể nói rằng cuộc sống của tôi đã thay đổi hoàn toàn. Từ một người nóng giận và ích kỷ, tôi đã trở thành một người biết yêu thương và chia sẻ. Gia đình tôi hạnh phúc, các con đều thành đạt và cũng đang theo Chúa.</p>"
                +

                "<p>Hiện tại, tôi đang phục vụ trong ban điều hành chi hội thánh và tham gia tích cực vào các hoạt động từ thiện. Mỗi ngày, tôi đều cảm ơn Chúa vì đã thay đổi cuộc đời tôi.</p>"
                +

                "<h3>Lời khuyên cho những người đang tìm kiếm</h3>" +
                "<p>Đối với những ai đang trong giai đoạn khó khăn hoặc đang tìm kiếm ý nghĩa cuộc sống, tôi muốn chia sẻ rằng: Đừng bao giờ từ bỏ hy vọng. Chúa luôn có kế hoạch tốt đẹp cho mỗi người chúng ta, dù đôi khi chúng ta không hiểu được.</p>"
                +

                "<p>Hãy mở lòng đón nhận tình yêu thương từ Chúa và từ cộng đồng. Đức tin không phải là điều gì đó xa vời, mà là một mối quan hệ thực tế và sống động với Đấng Tạo Hóa.</p>";

        baiViet.setNoiDung(noiDung);
        baiViet.setDanhMuc(danhMucList.get(3)); // Chia Sẻ
        baiViet.setTacGia("Phêrô Nguyễn");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.NHAN_SU);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNoiBat(false);
        baiViet.setLuotXem(650L);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(7));

        // Media content
        baiViet.setAnhDaiDien("https://picsum.photos/800/400?random=testimony");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=testimony1");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=testimony2");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=testimony3");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=testimony4");

        baiViet.addVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");

        return baiViet;
    }

    private BaiViet createBaiViet4(List<DanhMuc> danhMucList) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Thông Báo Lịch Sinh Hoạt Tháng 2/2024 - Nhiều Hoạt Động Đặc Sắc");
        baiViet.setSlug("thong-bao-lich-sinh-hoat-thang-2-2024");
        baiViet.setTomTat(
                "Chi hội thánh thông báo lịch sinh hoạt tháng 2/2024 với nhiều hoạt động đặc sắc: thánh lễ đặc biệt, khóa học Kinh Thánh, và chương trình giao lưu văn hóa.");

        String noiDung = "<h3>Lịch Thánh Lễ Chủ Nhật</h3>" +
                "<p>Các buổi thánh lễ Chủ nhật trong tháng 2/2024 sẽ được tổ chức theo lịch trình sau:</p>" +
                "<ul>" +
                "<li><strong>Chủ nhật 4/2:</strong> Thánh lễ thường - 8h00 sáng</li>" +
                "<li><strong>Chủ nhật 11/2:</strong> Thánh lễ Tết Nguyên đán - 8h00 sáng (có bánh chưng, bánh tét)</li>"
                +
                "<li><strong>Chủ nhật 18/2:</strong> Thánh lễ và báp têm - 8h00 sáng</li>" +
                "<li><strong>Chủ nhật 25/2:</strong> Thánh lễ kỷ niệm 25 năm thành lập chi hội thánh - 8h00 sáng</li>" +
                "</ul>" +

                "<h3>Khóa Học Kinh Thánh Cơ Bản</h3>" +
                "<p>Từ ngày 5/2 đến 26/2, chi hội thánh sẽ tổ chức khóa học Kinh Thánh cơ bản dành cho:</p>" +
                "<ul>" +
                "<li>Tín hữu mới</li>" +
                "<li>Những ai muốn tìm hiểu sâu hơn về đức tin</li>" +
                "<li>Các bậc phụ huynh muốn dạy con em về Kinh Thánh</li>" +
                "</ul>" +
                "<p><strong>Thời gian:</strong> Thứ 2 và Thứ 5 hàng tuần, từ 19h30 - 21h00<br>" +
                "<strong>Địa điểm:</strong> Phòng học tầng 2, chi hội thánh<br>" +
                "<strong>Giảng viên:</strong> Mục sư Nguyễn Văn Minh và Truyền đạo viên Lê Thị Hoa</p>" +

                "<h3>Chương Trình Giao Lưu Văn Hóa Tết</h3>" +
                "<p>Nhân dịp Tết Nguyên đán, chi hội thánh sẽ tổ chức chương trình giao lưu văn hóa với các hoạt động:</p>"
                +
                "<ul>" +
                "<li><strong>Ngày 10/2 (Mùng 1 Tết):</strong> Gói bánh chưng, bánh tét cùng nhau</li>" +
                "<li><strong>Ngày 11/2 (Mùng 2 Tết):</strong> Thánh lễ đặc biệt và tiệc Tết</li>" +
                "<li><strong>Ngày 12/2 (Mùng 3 Tết):</strong> Thăm viếng các gia đình có hoàn cảnh khó khăn</li>" +
                "</ul>" +

                "<h3>Sinh Hoạt Các Nhóm</h3>" +
                "<p><strong>Nhóm Thanh Niên:</strong></p>" +
                "<ul>" +
                "<li>Thứ 3 hàng tuần: 19h30 - 21h00</li>" +
                "<li>Chủ đề tháng 2: 'Sống đẹp trong thời đại số'</li>" +
                "<li>Hoạt động đặc biệt: Cắm trại 2 ngày 1 đêm (24-25/2)</li>" +
                "</ul>" +

                "<p><strong>Nhóm Phụ Nữ:</strong></p>" +
                "<ul>" +
                "<li>Thứ 5 hàng tuần: 19h00 - 20h30</li>" +
                "<li>Chủ đề tháng 2: 'Người phụ nữ trong gia đình Cơ Đốc'</li>" +
                "<li>Workshop: Làm bánh kẹo truyền thống (17/2)</li>" +
                "</ul>" +

                "<p><strong>Nhóm Thiếu Nhi:</strong></p>" +
                "<ul>" +
                "<li>Thứ 7 hàng tuần: 15h00 - 17h00</li>" +
                "<li>Chương trình: Học Kinh Thánh qua trò chơi</li>" +
                "<li>Hoạt động đặc biệt: Vẽ tranh về Tết (10/2)</li>" +
                "</ul>" +

                "<h3>Các Hoạt Động Đặc Biệt Khác</h3>" +
                "<p><strong>Thăm viếng người bệnh:</strong> Thứ 6 đầu tiên của tháng (2/2)<br>" +
                "<strong>Dọn dẹp chi hội thánh:</strong> Thứ 7 cuối tháng (24/2)<br>" +
                "<strong>Cầu nguyện cho cộng đồng:</strong> Mỗi tối Chủ nhật, 20h00 - 21h00</p>" +

                "<h3>Thông Tin Liên Hệ</h3>" +
                "<p>Mọi thắc mắc về lịch sinh hoạt, xin liên hệ:</p>" +
                "<ul>" +
                "<li><strong>Ban Thư Ký:</strong> 0123.456.789</li>" +
                "<li><strong>Email:</strong> chihoi@email.com</li>" +
                "<li><strong>Địa chỉ:</strong> 123 Đường ABC, Phường XYZ, TP.HCM</li>" +
                "</ul>" +

                "<p><em>Chi hội thánh kính mời tất cả anh chị em tham gia đầy đủ các hoạt động để cùng nhau xây dựng cộng đồng đức tin vững mạnh.</em></p>";

        baiViet.setNoiDung(noiDung);
        baiViet.setDanhMuc(danhMucList.get(4)); // Thông Báo
        baiViet.setTacGia("Ban Thư Ký");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.THU_KY);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNoiBat(false);
        baiViet.setLuotXem(420L);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(1));

        // Media content
        baiViet.setAnhDaiDien("https://picsum.photos/800/400?random=schedule2024");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=schedule1");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=schedule2");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=schedule3");

        return baiViet;
    }

    private BaiViet createBaiViet5(List<DanhMuc> danhMucList) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Khóa Tu Tâm Linh 3 Ngày 2 Đêm - Hành Trình Tìm Về Với Chính Mình");
        baiViet.setSlug("khoa-tu-tam-linh-3-ngay-2-dem");
        baiViet.setTomTat(
                "Khóa tu tâm linh 3 ngày 2 đêm tại Đà Lạt đã mang đến những trải nghiệm sâu sắc cho 80 tín hữu tham gia, giúp mọi người tìm lại sự bình an trong tâm hồn.");

        String noiDung = "<h3>Khởi hành với tâm hồn khao khát</h3>" +
                "<p>Sáng ngày 20/1/2024, đoàn 80 tín hữu từ chi hội thánh đã khởi hành đến Đà Lạt để tham gia khóa tu tâm linh 3 ngày 2 đêm. Mọi người đều mang trong lòng sự khao khát được gần gũi với Chúa hơn và tìm lại sự bình an trong tâm hồn.</p>"
                +

                "<p>Chuyến đi được chuẩn bị kỹ lưỡng trong 3 tháng với sự phối hợp của ban tổ chức và trung tâm tu tâm linh Đà Lạt. Đây là lần đầu tiên chi hội thánh tổ chức một khóa tu quy mô lớn như vậy.</p>"
                +

                "<h3>Ngày đầu - Buông bỏ và đón nhận</h3>" +
                "<p>Buổi chiều ngày đầu, sau khi nhận phòng và ăn trưa, tất cả mọi người tham gia buổi chia sẻ mở đầu với chủ đề 'Buông bỏ để đón nhận'. Mục sư Nguyễn Văn Minh đã dẫn dắt mọi người suy ngẫm về những gánh nặng trong cuộc sống mà chúng ta cần buông bỏ.</p>"
                +

                "<p>Buổi tối là thời gian cầu nguyện im lặng trong 2 tiếng đồng hồ. Nhiều người đã rơi nước mắt khi được ở một mình với Chúa, không có sự xao nhãng từ điện thoại hay công việc hàng ngày.</p>"
                +

                "<h3>Ngày thứ hai - Sự chữa lành và phục hồi</h3>" +
                "<p>Ngày thứ hai bắt đầu với buổi cầu nguyện chung lúc 6h sáng. Không khí trong lành của Đà Lạt và tiếng chim hót đã tạo nên một bầu không khí thiêng liêng đặc biệt.</p>"
                +

                "<p>Buổi sáng, mọi người tham gia workshop 'Tha thứ và được tha thứ' do chị Maria Lê hướng dẫn. Đây là một trong những buổi cảm động nhất của khóa tu, khi nhiều người đã chia sẻ những tổn thương sâu trong lòng và học cách tha thứ.</p>"
                +

                "<p>Buổi chiều là thời gian tự do để mọi người suy ngẫm cá nhân. Một số người chọn đi dạo trong vườn, một số khác ngồi yên lặng trong nhà nguyện. Tất cả đều tìm thấy sự bình an theo cách riêng của mình.</p>"
                +

                "<h3>Ngày cuối - Cam kết và hành động</h3>" +
                "<p>Ngày cuối cùng tập trung vào việc cam kết thay đổi và hành động cụ thể. Mỗi người đã viết một lá thư cho chính mình về những điều muốn thay đổi sau khóa tu. Những lá thư này sẽ được gửi lại cho họ sau 6 tháng.</p>"
                +

                "<p>Buổi thánh lễ kết thúc khóa tu đã diễn ra trong không khí trang trọng và cảm động. Nhiều người đã chia sẻ rằng họ cảm thấy như được tái sinh, với một tâm hồn nhẹ nhàng và đầy hy vọng.</p>"
                +

                "<h3>Những chia sẻ cảm động</h3>" +
                "<p>Chị Nguyễn Thị Hạnh (45 tuổi) chia sẻ: 'Tôi đã mang theo khóa tu này rất nhiều lo lắng về công việc và gia đình. Nhưng sau 3 ngày, tôi nhận ra rằng mình đã quá căng thẳng và quên mất việc tin tưởng vào Chúa. Bây giờ tôi cảm thấy bình an hơn rất nhiều.'</p>"
                +

                "<p>Anh Trần Văn Đức (38 tuổi) nói: 'Đây là lần đầu tiên trong 10 năm qua tôi có thể ngủ ngon mà không nghĩ về công việc. Khóa tu đã giúp tôi nhận ra tầm quan trọng của việc dành thời gian cho tâm linh.'</p>"
                +

                "<h3>Kế hoạch tiếp theo</h3>" +
                "<p>Sau thành công của khóa tu này, ban tổ chức đã lên kế hoạch tổ chức khóa tu tâm linh định kỳ 6 tháng/lần. Khóa tu tiếp theo dự kiến sẽ diễn ra vào tháng 7/2024 tại Vũng Tàu với chủ đề 'Sống trong ơn phước'.</p>"
                +

                "<p>Chi hội thánh cũng sẽ tổ chức các buổi chia sẻ định kỳ để mọi người có thể tiếp tục nuôi dưỡng những gì đã học được từ khóa tu.</p>"
                +

                "<h3>Lời cảm ơn</h3>" +
                "<p>Ban tổ chức xin chân thành cảm ơn trung tâm tu tâm linh Đà Lạt đã tạo điều kiện tốt nhất cho khóa tu. Đặc biệt cảm ơn các mục sư, truyền đạo viên đã dành thời gian hướng dẫn và chia sẻ.</p>"
                +

                "<p>Cảm ơn tất cả anh chị em đã tham gia với tâm hồn cởi mở và sẵn sàng học hỏi. Chúng ta hãy cùng nhau tiếp tục hành trình tâm linh này trong cuộc sống hàng ngày.</p>";

        baiViet.setNoiDung(noiDung);
        baiViet.setDanhMuc(danhMucList.get(1)); // Hoạt Động Thánh
        baiViet.setTacGia("Mục sư Nguyễn Văn Minh");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.MUC_SU);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNoiBat(true);
        baiViet.setLuotXem(1100L);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(10));

        // Rich media content
        baiViet.setAnhDaiDien("https://picsum.photos/800/400?random=retreat2024");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat1");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat2");
        baiViet.addHinhAnh("https://picsum.photos/600/400?rdom=retreat3");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat4");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat5");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat6");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat7");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat8");
        baiViet.addHinhAnh("https://picsum.photos/600/400?random=retreat9");

        // Multiple videos
        baiViet.addVideo("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4");
        baiViet.addVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        baiViet.addVideo(
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4");

        return baiViet;
    }
}