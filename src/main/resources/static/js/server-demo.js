const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = 3000;

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static('.'));

// Tạo thư mục uploads nếu chưa có
if (!fs.existsSync('uploads')) {
    fs.mkdirSync('uploads');
}

// Mock data
const mockCategories = [
    { id: 1, name: 'Chính trị', slug: 'chinh-tri' },
    { id: 2, name: 'Kinh tế', slug: 'kinh-te' },
    { id: 3, name: 'Thể thao', slug: 'the-thao' },
    { id: 4, name: 'Công nghệ', slug: 'cong-nghe' },
    { id: 5, name: 'Giải trí', slug: 'giai-tri' }
];

const mockArticles = [
    {
        id: 1,
        title: 'Chính phủ công bố chính sách mới về kinh tế số',
        slug: 'chinh-phu-cong-bo-chinh-sach-moi-ve-kinh-te-so',
        content: 'Chính phủ vừa công bố chính sách mới nhằm thúc đẩy phát triển kinh tế số, tạo điều kiện thuận lợi cho các doanh nghiệp công nghệ. Chính sách này bao gồm nhiều ưu đãi về thuế, đất đai và nguồn nhân lực cho các doanh nghiệp hoạt động trong lĩnh vực công nghệ số.\n\nTheo đó, các doanh nghiệp công nghệ sẽ được hưởng mức thuế ưu đãi 10% trong 15 năm đầu hoạt động. Bên cạnh đó, chính phủ cũng cam kết đầu tư mạnh vào hạ tầng số và đào tạo nhân lực chất lượng cao.\n\nChính sách này được kỳ vọng sẽ tạo ra bước đột phá cho ngành công nghệ Việt Nam, giúp nước ta trở thành một trong những trung tâm công nghệ hàng đầu khu vực.',
        excerpt: 'Chính sách mới sẽ tạo bước đột phá cho ngành công nghệ Việt Nam',
        category_id: 1,
        category_name: 'Chính trị',
        category_slug: 'chinh-tri',
        author: 'Nguyễn Văn A',
        status: 'published',
        views: 1250,
        created_at: new Date().toISOString()
    },
    {
        id: 2,
        title: 'Thị trường chứng khoán biến động mạnh trong tuần qua',
        slug: 'thi-truong-chung-khoan-bien-dong-manh-trong-tuan-qua',
        content: 'Thị trường chứng khoán Việt Nam ghi nhận những biến động mạnh trong tuần qua với nhiều cổ phiếu tăng giảm bất thường. VN-Index đã có những phiên tăng giảm đáng kể, phản ánh tâm lý thận trọng của nhà đầu tư.\n\nCác chuyên gia phân tích cho rằng, biến động này chủ yếu do ảnh hưởng từ thị trường quốc tế và một số thông tin về chính sách kinh tế mới. Tuy nhiên, xu hướng dài hạn của thị trường vẫn được đánh giá tích cực.\n\nNhà đầu tư được khuyến cáo nên thận trọng và có chiến lược đầu tư phù hợp trong bối cảnh thị trường biến động.',
        excerpt: 'Phân tích chi tiết về tình hình thị trường chứng khoán tuần qua',
        category_id: 2,
        category_name: 'Kinh tế',
        category_slug: 'kinh-te',
        author: 'Trần Thị B',
        status: 'published',
        views: 890,
        created_at: new Date(Date.now() - 86400000).toISOString()
    },
    {
        id: 3,
        title: 'Đội tuyển Việt Nam thắng đậm 3-0 trước đối thủ mạnh',
        slug: 'doi-tuyen-viet-nam-thang-dam-3-0-truoc-doi-thu-manh',
        content: 'Trong trận đấu diễn ra tối qua tại sân vận động Mỹ Đình, đội tuyển Việt Nam đã có chiến thắng ấn tượng 3-0 trước đối thủ được đánh giá cao. Đây là kết quả rất đáng khích lệ cho thầy trò HLV Park Hang-seo.\n\nBàn thắng mở tỷ số được ghi ở phút thứ 15 bởi tiền đạo Nguyễn Tiến Linh. Hai bàn thắng còn lại được thực hiện bởi Phan Văn Đức và Nguyễn Quang Hải trong hiệp hai.\n\nChiến thắng này giúp đội tuyển Việt Nam vươn lên vị trí đầu bảng và tạo lợi thế lớn cho các trận đấu tiếp theo.',
        excerpt: 'Chiến thắng quan trọng giúp Việt Nam vươn lên vị trí đầu bảng',
        category_id: 3,
        category_name: 'Thể thao',
        category_slug: 'the-thao',
        author: 'Lê Văn C',
        status: 'published',
        views: 2100,
        created_at: new Date(Date.now() - 172800000).toISOString()
    },
    {
        id: 4,
        title: 'Công nghệ AI mới được phát triển tại Việt Nam',
        slug: 'cong-nghe-ai-moi-duoc-phat-trien-tai-viet-nam',
        content: 'Một nhóm nghiên cứu tại Việt Nam vừa công bố thành công trong việc phát triển công nghệ trí tuệ nhân tạo mới có khả năng xử lý ngôn ngữ tự nhiên tiếng Việt với độ chính xác cao. Đây là bước đột phá quan trọng trong lĩnh vực AI tại Việt Nam.\n\nCông nghệ này có thể được ứng dụng trong nhiều lĩnh vực như giáo dục, y tế, và dịch vụ khách hàng. Nhóm nghiên cứu cho biết họ đã làm việc trong suốt 3 năm để hoàn thiện sản phẩm này.\n\nViệc phát triển thành công công nghệ AI này mở ra nhiều cơ hội cho các doanh nghiệp Việt Nam trong việc số hóa và tự động hóa quy trình làm việc.',
        excerpt: 'Breakthrough trong lĩnh vực AI có thể thay đổi nhiều ngành công nghiệp',
        category_id: 4,
        category_name: 'Công nghệ',
        category_slug: 'cong-nghe',
        author: 'Phạm Thị D',
        status: 'published',
        views: 1560,
        created_at: new Date(Date.now() - 259200000).toISOString()
    },
    {
        id: 5,
        title: 'Festival âm nhạc quốc tế sắp diễn ra tại Hà Nội',
        slug: 'festival-am-nhac-quoc-te-sap-dien-ra-tai-ha-noi',
        content: 'Festival âm nhạc quốc tế lớn nhất năm sẽ được tổ chức tại Hà Nội với sự tham gia của nhiều nghệ sĩ nổi tiếng trong nước và quốc tế. Sự kiện dự kiến thu hút hàng chục nghìn khán giả.\n\nChương trình sẽ diễn ra trong 3 ngày với nhiều thể loại âm nhạc khác nhau từ pop, rock, jazz đến nhạc dân tộc. Đặc biệt, lần đầu tiên festival sẽ có sân khấu ngoài trời với hệ thống âm thanh và ánh sáng hiện đại.\n\nVé tham dự đã được mở bán và nhận được sự quan tâm lớn từ công chúng. Ban tổ chức khuyến khích khán giả đặt vé sớm để đảm bảo có chỗ.',
        excerpt: 'Sự kiện âm nhạc đáng chờ đợi nhất trong năm',
        category_id: 5,
        category_name: 'Giải trí',
        category_slug: 'giai-tri',
        author: 'Hoàng Văn E',
        status: 'published',
        views: 750,
        created_at: new Date(Date.now() - 345600000).toISOString()
    }
];

let mockMessages = [];

// API Routes
app.get('/api/articles', (req, res) => {
    const { category, limit = 10, page = 1 } = req.query;
    let filteredArticles = mockArticles.filter(article => article.status === 'published');
    
    if (category) {
        filteredArticles = filteredArticles.filter(article => article.category_slug === category);
    }
    
    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + parseInt(limit);
    const paginatedArticles = filteredArticles.slice(startIndex, endIndex);
    
    res.json(paginatedArticles);
});

app.get('/api/articles/:id', (req, res) => {
    const { id } = req.params;
    const article = mockArticles.find(a => a.id === parseInt(id));
    
    if (!article) {
        return res.status(404).json({ error: 'Không tìm thấy bài viết' });
    }
    
    // Tăng lượt xem
    article.views += 1;
    
    res.json(article);
});

app.get('/api/categories', (req, res) => {
    res.json(mockCategories);
});

app.get('/api/search', (req, res) => {
    const { q, category, limit = 20 } = req.query;
    
    if (!q) {
        return res.status(400).json({ error: 'Từ khóa tìm kiếm là bắt buộc' });
    }
    
    let results = mockArticles.filter(article => {
        const matchesSearch = article.title.toLowerCase().includes(q.toLowerCase()) ||
                            article.content.toLowerCase().includes(q.toLowerCase()) ||
                            article.excerpt.toLowerCase().includes(q.toLowerCase());
        
        const matchesCategory = !category || article.category_slug === category;
        
        return matchesSearch && matchesCategory && article.status === 'published';
    });
    
    results = results.slice(0, parseInt(limit));
    res.json(results);
});

app.post('/api/contact', (req, res) => {
    const { name, email, phone, subject, message, newsletter } = req.body;
    
    if (!name || !email || !subject || !message) {
        return res.status(400).json({ error: 'Vui lòng điền đầy đủ thông tin bắt buộc' });
    }
    
    const newMessage = {
        id: mockMessages.length + 1,
        name,
        email,
        phone: phone || null,
        subject,
        message,
        newsletter: newsletter || false,
        status: 'new',
        created_at: new Date().toISOString()
    };
    
    mockMessages.push(newMessage);
    
    res.json({ 
        message: 'Tin nhắn đã được gửi thành công',
        id: newMessage.id 
    });
});

app.get('/api/contact-messages', (req, res) => {
    res.json(mockMessages.reverse());
});

// Serve static files
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.get('/admin', (req, res) => {
    res.sendFile(path.join(__dirname, 'admin.html'));
});

app.get('/about', (req, res) => {
    res.sendFile(path.join(__dirname, 'about.html'));
});

app.get('/contact', (req, res) => {
    res.sendFile(path.join(__dirname, 'contact.html'));
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Server demo đang chạy tại http://localhost:${PORT}`);
    console.log(`📱 Trang chủ: http://localhost:${PORT}`);
    console.log(`👤 Giới thiệu: http://localhost:${PORT}/about.html`);
    console.log(`📞 Liên hệ: http://localhost:${PORT}/contact.html`);
    console.log(`⚙️  Quản trị: http://localhost:${PORT}/admin.html`);
    console.log(`\n✅ Website đã sẵn sàng với dữ liệu mẫu!`);
});

module.exports = app;