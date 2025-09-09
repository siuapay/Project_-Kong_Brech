package com.branch.demo.service;

import com.branch.demo.domain.BaiViet;
import com.branch.demo.domain.DanhMuc;
import com.branch.demo.repository.BaiVietRepository;
import com.branch.demo.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RichContentNewsService {

    @Autowired
    private BaiVietRepository baiVietRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Transactional
    public void createRichContentNews() {
        // Check if rich content article already exists
        if (baiVietRepository.existsBySlug("lam-dong-boi-linh-va-huan-luyen-tai-khu-vuc-2-dak-nong")) {
            return;
        }

        // Get categories
        List<DanhMuc> danhMucs = danhMucRepository.findAll();
        DanhMuc tinTuc = danhMucs.stream()
                .filter(dm -> "tin-tuc".equals(dm.getSlug()))
                .findFirst()
                .orElse(danhMucs.isEmpty() ? null : danhMucs.get(0));

        // Skip if no categories available
        if (tinTuc == null) {
            return;
        }

        // Create rich content article similar to the reference
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Lâm Đồng: Bồi linh và huấn luyện tại khu vực 2 - Đắk Nông");
        baiViet.setSlug("lam-dong-boi-linh-va-huan-luyen-tai-khu-vuc-2-dak-nong");
        baiViet.setTomTat(
                "Chương trình bồi linh và huấn luyện tại khu vực 2 - Đắk Nông với sự tham gia của các mục sư, truyền đạo và nhân sự từ nhiều nơi.");
        baiViet.setDanhMuc(tinTuc);
        baiViet.setTacGia("Ban Biên Tập");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.ADMIN);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(2));
        baiViet.setAnhDaiDien(
                "https://httlvn.org/wp-content/uploads/2024/09/z5825936094851_b8b8b8b8b8b8b8b8b8b8b8b8b8b8b8b8-768x576.jpg");

        // Create rich HTML content with embedded images
        String richContent = createRichContentHtml();
        baiViet.setNoiDungRich(richContent);

        // Set basic content as fallback
        baiViet.setNoiDung(
                "Chương trình bồi linh và huấn luyện tại khu vực 2 - Đắk Nông đã diễn ra thành công với sự tham gia của nhiều mục sư và nhân sự.");

        baiViet.setMetaTitle("Lâm Đồng: Bồi linh và huấn luyện tại khu vực 2 - Đắk Nông");
        baiViet.setMetaDescription(
                "Chương trình bồi linh và huấn luyện tại khu vực 2 - Đắk Nông với sự tham gia của các mục sư, truyền đạo và nhân sự từ nhiều nơi.");
        baiViet.setMetaKeywords("bồi linh, huấn luyện, Đắk Nông, mục sư, truyền đạo");
        baiViet.setNoiBat(true);

        baiVietRepository.save(baiViet);

        // Create another rich content article
        createChristmasArticle(tinTuc);
    }

    private void createChristmasArticle(DanhMuc tinTuc) {
        // Check if Christmas article already exists
        if (baiVietRepository.existsBySlug("le-giang-sinh-2023-niem-vui-tran-ngap")) {
            return;
        }

        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe("Lễ Giáng sinh 2023: Niềm vui tràn ngập");
        baiViet.setSlug("le-giang-sinh-2023-niem-vui-tran-ngap");
        baiViet.setTomTat(
                "Lễ Giáng sinh 2023 tại Chi Hội Thánh đã diễn ra thành công với nhiều hoạt động ý nghĩa và niềm vui tràn ngập.");
        baiViet.setDanhMuc(tinTuc);
        baiViet.setTacGia("Ban Truyền Thông");
        baiViet.setLoaiTacGia(BaiViet.LoaiTacGia.ADMIN);
        baiViet.setTrangThai(BaiViet.TrangThaiBaiViet.DA_XUAT_BAN);
        baiViet.setNgayXuatBan(LocalDateTime.now().minusDays(5));
        baiViet.setAnhDaiDien("https://images.unsplash.com/photo-1512389142860-9c449e58a543?w=800");

        // Create rich HTML content for Christmas
        String richContent = createChristmasRichContent();
        baiViet.setNoiDungRich(richContent);

        baiViet.setNoiDung("Lễ Giáng sinh 2023 tại Chi Hội Thánh đã diễn ra thành công với nhiều hoạt động ý nghĩa.");
        baiViet.setMetaTitle("Lễ Giáng sinh 2023: Niềm vui tràn ngập");
        baiViet.setMetaDescription(
                "Lễ Giáng sinh 2023 tại Chi Hội Thánh với nhiều hoạt động ý nghĩa và niềm vui tràn ngập");
        baiViet.setMetaKeywords("Giáng sinh, lễ hội, hội thánh, niềm vui");
        baiViet.setNoiBat(true);

        baiVietRepository.save(baiViet);
    }

    private String createRichContentHtml() {
        return """
                <p>Ngươn trên phần đoạn Kinh Thánh I Sử Ký 13:1-14 và câu gốc: "Hòm của Đức Chúa Trời ở
                tại nhà Phiên-nha O-bết-ê-đôm ba tháng; Đức Chúa Trời ban phước cho nhà O-bết-ê-đôm và mọi
                vật thuộc về người" (1 Sử Ký 13:14), Mục sư Tống Thư ký đã cống bố Lời Chúa với chủ đề:
                HẦU VIỆC CHÚA XỨNG HIỆP để nhắc nhở quy tôi con Chúa phải đặt ý Chúa trên hết, không
                pha tạp với đời, tuân thủ điều Chúa dạy và tôn kính Chúa hết lòng. Lời Chúa vô cùng quý
                báu, đầy sự ngọt ngào và cảnh tỉnh cho những người đang hầu việc Chúa.</p>

                <div class="text-center my-4">
                    <img src="https://httlvn.org/wp-content/uploads/2024/09/z5825936094851_b8b8b8b8b8b8b8b8b8b8b8b8b8b8b8b8-768x576.jpg"
                         class="img-fluid rounded" alt="MS Nguyễn Hữu Bình giảng Lời Chúa">
                    <p class="text-muted mt-2"><em>MS Nguyễn Hữu Bình giảng Lời Chúa</em></p>
                </div>

                <h4>Chương trình 2: Huấn luyện</h4>

                <div class="text-center my-4">
                    <img src="https://httlvn.org/wp-content/uploads/2024/09/z5825936094852_training_session-768x576.jpg"
                         class="img-fluid rounded" alt="Chương trình huấn luyện">
                    <p class="text-muted mt-2"><em>Buổi huấn luyện với sự tham gia của các mục sư và nhân sự</em></p>
                </div>

                <p>HTTLVN, là Điện gia bồi linh và huấn luyện; Mục sư Lemous Philemol, UV TLH – MV tỉnh
                Lâm Đồng; Mục sư Y Djrăn UV HDGP; Mục sư Nguyễn Văn Hoàng, Trưởng ban và Ban Đại
                diện tỉnh; quy Mục sư, Truyền đạo và Chấp sự. Số lượng khoảng 950 người.</p>

                <h4>Chương trình 1: Bồi linh</h4>

                <div class="text-center my-4">
                    <img src="https://httlvn.org/wp-content/uploads/2024/09/z5825936094853_spiritual_training-768x576.jpg"
                         class="img-fluid rounded" alt="Chương trình bồi linh">
                    <p class="text-muted mt-2"><em>MS Hầu Văn Lâu, UV BDD, hướng dẫn chương trình</em></p>
                </div>

                <div class="text-center my-4">
                    <img src="https://httlvn.org/wp-content/uploads/2024/09/z5825936094854_participants-768x576.jpg"
                         class="img-fluid rounded" alt="Các tham gia viên">
                    <p class="text-muted mt-2"><em>Các tham gia viên tham dự chương trình bồi linh</em></p>
                </div>

                <p>Chương trình bồi linh và huấn luyện đã mang lại nhiều kiến thức bổ ích và tăng cường đức tin
                cho các tham gia viên. Đây là cơ hội quý báu để các mục sư, truyền đạo và nhân sự có thể học hỏi,
                chia sẻ kinh nghiệm và cùng nhau phát triển trong công việc phục vụ Chúa.</p>

                <div class="alert alert-info mt-4">
                    <h5><i class="fas fa-info-circle me-2"></i>Thông tin thêm</h5>
                    <p class="mb-0">Chương trình này là một phần trong kế hoạch đào tạo và phát triển nhân sự
                    của HTTLVN, nhằm nâng cao chất lượng phục vụ và truyền bá Lời Chúa đến cộng đồng.</p>
                </div>
                """;
    }

    private String createChristmasRichContent() {
        return """
                <p>Lễ Giáng sinh năm 2023 tại Chi Hội Thánh đã diễn ra với không khí trang trọng và ấm áp.
                Đây là dịp để tất cả tín hữu cùng nhau tôn vinh sự ra đời của Chúa Giê-su Christ và chia sẻ
                tình yêu thương với nhau.</p>

                <div class="text-center my-4">
                    <img src="https://images.unsplash.com/photo-1512389142860-9c449e58a543?w=800"
                         class="img-fluid rounded" alt="Lễ Giáng sinh trang trọng">
                    <p class="text-muted mt-2"><em>Không khí Giáng sinh trang trọng tại hội thánh</em></p>
                </div>

                <h4>Chương trình thờ phượng đặc biệt</h4>

                <p>Chương trình thờ phượng Giáng sinh bắt đầu từ 19h00 với sự tham gia của toàn thể tín hữu.
                Mục sư đã chia sẻ về ý nghĩa sâu sắc của ngày Giáng sinh và tầm quan trọng của việc đón nhận
                Chúa Giê-su vào lòng mình.</p>

                <div class="text-center my-4">
                    <img src="https://images.unsplash.com/photo-1544273677-6e4b999de2a7?w=800"
                         class="img-fluid rounded" alt="Mục sư giảng đạo">
                    <p class="text-muted mt-2"><em>Mục sư chia sẻ Lời Chúa trong đêm Giáng sinh</em></p>
                </div>

                <h4>Hoạt động ca hát và biểu diễn</h4>

                <p>Ban ca đoàn đã trình bày những bài hát Giáng sinh truyền thống và hiện đại, tạo nên
                không khí trang nghiêm và cảm động. Các em thiếu nhi cũng có những tiết mục biểu diễn
                đáng yêu về câu chuyện Giáng sinh.</p>

                <div class="text-center my-4">
                    <img src="https://images.unsplash.com/photo-1576354302919-96748cb8299e?w=800"
                         class="img-fluid rounded" alt="Ban ca đoàn biểu diễn">
                    <p class="text-muted mt-2"><em>Ban ca đoàn trình bày những bài hát Giáng sinh</em></p>
                </div>

                <h4>Chia sẻ và tặng quà</h4>

                <p>Một trong những hoạt động ý nghĩa nhất là việc chia sẻ quà tặng cho các gia đình khó khăn
                và trẻ em mồ côi. Đây là cách thể hiện tình yêu thương và tinh thần chia sẻ trong dịp Giáng sinh.</p>

                <div class="text-center my-4">
                    <img src="https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=800"
                         class="img-fluid rounded" alt="Tặng quà cho trẻ em">
                    <p class="text-muted mt-2"><em>Tặng quà cho các em nhỏ trong dịp Giáng sinh</em></p>
                </div>

                <p>Lễ Giáng sinh 2023 đã khép lại với nhiều kỷ niệm đẹp và ý nghĩa. Đây không chỉ là dịp
                để tôn vinh Chúa mà còn là cơ hội để mọi người gắn kết với nhau trong tình yêu thương và đức tin.</p>

                <div class="alert alert-success mt-4">
                    <h5><i class="fas fa-heart me-2"></i>Lời cảm ơn</h5>
                    <p class="mb-0">Ban tổ chức xin chân thành cảm ơn tất cả tín hữu đã tham gia và đóng góp
                    để làm cho Lễ Giáng sinh 2023 thành công tốt đẹp. Chúc mọi người một mùa Giáng sinh an lành
                    và hạnh phúc!</p>
                </div>
                """;
    }
}