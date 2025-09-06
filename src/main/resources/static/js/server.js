const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static('public'));
app.use('/uploads', express.static('uploads'));

// Tạo thư mục uploads nếu chưa có
if (!fs.existsSync('uploads')) {
    fs.mkdirSync('uploads');
}

// Cấu hình multer để upload file
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, file.fieldname + '-' + uniqueSuffix + path.extname(file.originalname));
    }
});

const upload = multer({ 
    storage: storage,
    limits: {
        fileSize: 10 * 1024 * 1024 // 10MB
    },
    fileFilter: function (req, file, cb) {
        const allowedTypes = /jpeg|jpg|png|gif|mp4|avi|mov/;
        const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
        const mimetype = allowedTypes.test(file.mimetype);
        
        if (mimetype && extname) {
            return cb(null, true);
        } else {
            cb(new Error('Chỉ cho phép upload ảnh và video!'));
        }
    }
});

// Kết nối MySQL
const db = mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'news_db'
});

// Kết nối database
db.connect((err) => {
    if (err) {
        console.error('Lỗi kết nối database:', err);
        return;
    }
    console.log('Đã kết nối MySQL database');
    
    // Tạo bảng nếu chưa có
    createTables();
});

// Tạo các bảng cần thiết
function createTables() {
    // Bảng categories
    const createCategoriesTable = `
        CREATE TABLE IF NOT EXISTS categories (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL UNIQUE,
            slug VARCHAR(100) NOT NULL UNIQUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `;
    
    // Bảng articles
    const createArticlesTable = `
        CREATE TABLE IF NOT EXISTS articles (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            slug VARCHAR(255) NOT NULL UNIQUE,
            content TEXT NOT NULL,
            excerpt TEXT,
            featured_image VARCHAR(255),
            video_url VARCHAR(255),
            category_id INT,
            author VARCHAR(100) DEFAULT 'Admin',
            status ENUM('draft', 'published') DEFAULT 'draft',
            views INT DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (category_id) REFERENCES categories(id)
        )
    `;
    
    // Bảng contact messages
    const createContactTable = `
        CREATE TABLE IF NOT EXISTS contact_messages (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            email VARCHAR(150) NOT NULL,
            phone VARCHAR(20),
            subject VARCHAR(200) NOT NULL,
            message TEXT NOT NULL,
            newsletter BOOLEAN DEFAULT FALSE,
            status ENUM('new', 'read', 'replied') DEFAULT 'new',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `;
    
    // Bảng newsletter subscribers
    const createNewsletterTable = `
        CREATE TABLE IF NOT EXISTS newsletter_subscribers (
            id INT AUTO_INCREMENT PRIMARY KEY,
            email VARCHAR(150) NOT NULL UNIQUE,
            name VARCHAR(100),
            status ENUM('active', 'inactive') DEFAULT 'active',
            subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `;
    
    // Bảng media files
    const createMediaTable = `
        CREATE TABLE IF NOT EXISTS media_files (
            id INT AUTO_INCREMENT PRIMARY KEY,
            filename VARCHAR(255) NOT NULL,
            original_name VARCHAR(255) NOT NULL,
            file_type ENUM('image', 'video', 'document') NOT NULL,
            file_size INT NOT NULL,
            mime_type VARCHAR(100) NOT NULL,
            article_id INT,
            uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE SET NULL
        )
    `;
    
    // Bảng user sessions (for future authentication)
    const createSessionsTable = `
        CREATE TABLE IF NOT EXISTS user_sessions (
            id INT AUTO_INCREMENT PRIMARY KEY,
            session_id VARCHAR(255) NOT NULL UNIQUE,
            user_data TEXT,
            expires_at TIMESTAMP NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    `;
    
    db.query(createCategoriesTable, (err) => {
        if (err) console.error('Lỗi tạo bảng categories:', err);
        else console.log('Bảng categories đã sẵn sàng');
    });
    
    db.query(createArticlesTable, (err) => {
        if (err) console.error('Lỗi tạo bảng articles:', err);
        else console.log('Bảng articles đã sẵn sàng');
    });
    
    db.query(createContactTable, (err) => {
        if (err) console.error('Lỗi tạo bảng contact_messages:', err);
        else console.log('Bảng contact_messages đã sẵn sàng');
    });
    
    db.query(createNewsletterTable, (err) => {
        if (err) console.error('Lỗi tạo bảng newsletter_subscribers:', err);
        else console.log('Bảng newsletter_subscribers đã sẵn sàng');
    });
    
    db.query(createMediaTable, (err) => {
        if (err) console.error('Lỗi tạo bảng media_files:', err);
        else console.log('Bảng media_files đã sẵn sàng');
    });
    
    db.query(createSessionsTable, (err) => {
        if (err) console.error('Lỗi tạo bảng user_sessions:', err);
        else console.log('Bảng user_sessions đã sẵn sàng');
        
        // Thêm dữ liệu mẫu sau khi tất cả bảng đã được tạo
        insertSampleData();
    });
}

// Thêm dữ liệu mẫu
function insertSampleData() {
    const sampleCategories = [
        ['Chính trị', 'chinh-tri', 'Tin tức chính trị trong nước và quốc tế'],
        ['Kinh tế', 'kinh-te', 'Thông tin kinh tế, tài chính, thị trường'],
        ['Thể thao', 'the-thao', 'Tin tức thể thao, bóng đá, các môn thể thao khác'],
        ['Công nghệ', 'cong-nghe', 'Công nghệ thông tin, khoa học, đổi mới'],
        ['Giải trí', 'giai-tri', 'Showbiz, điện ảnh, âm nhạc'],
        ['Sức khỏe', 'suc-khoe', 'Y tế, sức khỏe cộng đồng'],
        ['Giáo dục', 'giao-duc', 'Tin tức giáo dục, đào tạo'],
        ['Xã hội', 'xa-hoi', 'Các vấn đề xã hội, đời sống'],
        ['Pháp luật', 'phap-luat', 'Tin tức pháp luật, an ninh trật tự'],
        ['Du lịch', 'du-lich', 'Du lịch, văn hóa, ẩm thực']
    ];
    
    sampleCategories.forEach(([name, slug, description]) => {
        db.query('INSERT IGNORE INTO categories (name, slug, description) VALUES (?, ?, ?)', [name, slug, description]);
    });
    
    // Thêm bài viết mẫu
    setTimeout(() => {
        const sampleArticles = [
            {
                title: 'Chính phủ công bố chính sách mới về kinh tế số',
                content: 'Chính phủ vừa công bố chính sách mới nhằm thúc đẩy phát triển kinh tế số, tạo điều kiện thuận lợi cho các doanh nghiệp công nghệ. Chính sách này bao gồm nhiều ưu đãi về thuế, đất đai và nguồn nhân lực cho các doanh nghiệp hoạt động trong lĩnh vực công nghệ số.\n\nTheo đó, các doanh nghiệp công nghệ sẽ được hưởng mức thuế ưu đãi 10% trong 15 năm đầu hoạt động. Bên cạnh đó, chính phủ cũng cam kết đầu tư mạnh vào hạ tầng số và đào tạo nhân lực chất lượng cao.\n\nChính sách này được kỳ vọng sẽ tạo ra bước đột phá cho ngành công nghệ Việt Nam, giúp nước ta trở thành một trong những trung tâm công nghệ hàng đầu khu vực.',
                excerpt: 'Chính sách mới sẽ tạo bước đột phá cho ngành công nghệ Việt Nam',
                category: 'chinh-tri',
                author: 'Nguyễn Văn A',
                views: 1250
            },
            {
                title: 'Thị trường chứng khoán biến động mạnh trong tuần qua',
                content: 'Thị trường chứng khoán Việt Nam ghi nhận những biến động mạnh trong tuần qua với nhiều cổ phiếu tăng giảm bất thường. VN-Index đã có những phiên tăng giảm đáng kể, phản ánh tâm lý thận trọng của nhà đầu tư.\n\nCác chuyên gia phân tích cho rằng, biến động này chủ yếu do ảnh hưởng từ thị trường quốc tế và một số thông tin về chính sách kinh tế mới. Tuy nhiên, xu hướng dài hạn của thị trường vẫn được đánh giá tích cực.\n\nNhà đầu tư được khuyến cáo nên thận trọng và có chiến lược đầu tư phù hợp trong bối cảnh thị trường biến động.',
                excerpt: 'Phân tích chi tiết về tình hình thị trường chứng khoán tuần qua',
                category: 'kinh-te',
                author: 'Trần Thị B',
                views: 890
            },
            {
                title: 'Đội tuyển Việt Nam thắng đậm 3-0 trước đối thủ mạnh',
                content: 'Trong trận đấu diễn ra tối qua tại sân vận động Mỹ Đình, đội tuyển Việt Nam đã có chiến thắng ấn tượng 3-0 trước đối thủ được đánh giá cao. Đây là kết quả rất đáng khích lệ cho thầy trò HLV Park Hang-seo.\n\nBàn thắng mở tỷ số được ghi ở phút thứ 15 bởi tiền đạo Nguyễn Tiến Linh. Hai bàn thắng còn lại được thực hiện bởi Phan Văn Đức và Nguyễn Quang Hải trong hiệp hai.\n\nChiến thắng này giúp đội tuyển Việt Nam vươn lên vị trí đầu bảng và tạo lợi thế lớn cho các trận đấu tiếp theo.',
                excerpt: 'Chiến thắng quan trọng giúp Việt Nam vươn lên vị trí đầu bảng',
                category: 'the-thao',
                author: 'Lê Văn C',
                views: 2100
            }
        ];
        
        sampleArticles.forEach(article => {
            // Tạo slug từ title
            const slug = article.title.toLowerCase()
                .replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, 'a')
                .replace(/[èéẹẻẽêềếệểễ]/g, 'e')
                .replace(/[ìíịỉĩ]/g, 'i')
                .replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, 'o')
                .replace(/[ùúụủũưừứựửữ]/g, 'u')
                .replace(/[ỳýỵỷỹ]/g, 'y')
                .replace(/đ/g, 'd')
                .replace(/[^a-z0-9 -]/g, '')
                .replace(/\s+/g, '-')
                .replace(/-+/g, '-')
                .trim('-');
            
            // Lấy category_id
            db.query('SELECT id FROM categories WHERE slug = ?', [article.category], (err, results) => {
                if (!err && results.length > 0) {
                    const categoryId = results[0].id;
                    
                    const insertQuery = `
                        INSERT IGNORE INTO articles (title, slug, content, excerpt, category_id, author, status, views)
                        VALUES (?, ?, ?, ?, ?, ?, 'published', ?)
                    `;
                    
                    db.query(insertQuery, [
                        article.title, 
                        slug, 
                        article.content, 
                        article.excerpt, 
                        categoryId, 
                        article.author, 
                        article.views
                    ], (err) => {
                        if (err) console.error('Lỗi thêm bài viết mẫu:', err);
                    });
                }
            });
        });
    }, 1000);
}

// Routes

// Lấy tất cả bài viết
app.get('/api/articles', (req, res) => {
    const { category, limit = 10, page = 1 } = req.query;
    const offset = (page - 1) * limit;
    
    let query = `
        SELECT a.*, c.name as category_name, c.slug as category_slug 
        FROM articles a 
        LEFT JOIN categories c ON a.category_id = c.id 
        WHERE a.status = 'published'
    `;
    
    const params = [];
    
    if (category) {
        query += ' AND c.slug = ?';
        params.push(category);
    }
    
    query += ' ORDER BY a.created_at DESC LIMIT ? OFFSET ?';
    params.push(parseInt(limit), parseInt(offset));
    
    db.query(query, params, (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        res.json(results);
    });
});

// Lấy bài viết theo ID
app.get('/api/articles/:id', (req, res) => {
    const { id } = req.params;
    
    // Tăng lượt xem
    db.query('UPDATE articles SET views = views + 1 WHERE id = ?', [id]);
    
    const query = `
        SELECT a.*, c.name as category_name, c.slug as category_slug 
        FROM articles a 
        LEFT JOIN categories c ON a.category_id = c.id 
        WHERE a.id = ? AND a.status = 'published'
    `;
    
    db.query(query, [id], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        if (results.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy bài viết' });
        }
        
        res.json(results[0]);
    });
});

// Tạo bài viết mới
app.post('/api/articles', upload.fields([
    { name: 'featured_image', maxCount: 1 },
    { name: 'video', maxCount: 1 }
]), (req, res) => {
    const { title, content, excerpt, category_id, author, status } = req.body;
    
    if (!title || !content) {
        return res.status(400).json({ error: 'Tiêu đề và nội dung là bắt buộc' });
    }
    
    // Tạo slug từ tiêu đề
    const slug = title.toLowerCase()
        .replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, 'a')
        .replace(/[èéẹẻẽêềếệểễ]/g, 'e')
        .replace(/[ìíịỉĩ]/g, 'i')
        .replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, 'o')
        .replace(/[ùúụủũưừứựửữ]/g, 'u')
        .replace(/[ỳýỵỷỹ]/g, 'y')
        .replace(/đ/g, 'd')
        .replace(/[^a-z0-9 -]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-')
        .trim('-');
    
    const featured_image = req.files?.featured_image?.[0]?.filename || null;
    const video_url = req.files?.video?.[0]?.filename || null;
    
    const query = `
        INSERT INTO articles (title, slug, content, excerpt, featured_image, video_url, category_id, author, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;
    
    db.query(query, [title, slug, content, excerpt, featured_image, video_url, category_id, author || 'Admin', status || 'draft'], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi tạo bài viết', details: err.message });
        }
        
        res.status(201).json({
            message: 'Tạo bài viết thành công',
            id: result.insertId,
            slug: slug
        });
    });
});

// Cập nhật bài viết
app.put('/api/articles/:id', upload.fields([
    { name: 'featured_image', maxCount: 1 },
    { name: 'video', maxCount: 1 }
]), (req, res) => {
    const { id } = req.params;
    const { title, content, excerpt, category_id, author, status } = req.body;
    
    let updateFields = [];
    let values = [];
    
    if (title) {
        updateFields.push('title = ?');
        values.push(title);
        
        // Cập nhật slug nếu có title mới
        const slug = title.toLowerCase()
            .replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, 'a')
            .replace(/[èéẹẻẽêềếệểễ]/g, 'e')
            .replace(/[ìíịỉĩ]/g, 'i')
            .replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, 'o')
            .replace(/[ùúụủũưừứựửữ]/g, 'u')
            .replace(/[ỳýỵỷỹ]/g, 'y')
            .replace(/đ/g, 'd')
            .replace(/[^a-z0-9 -]/g, '')
            .replace(/\s+/g, '-')
            .replace(/-+/g, '-')
            .trim('-');
        
        updateFields.push('slug = ?');
        values.push(slug);
    }
    
    if (content) {
        updateFields.push('content = ?');
        values.push(content);
    }
    
    if (excerpt) {
        updateFields.push('excerpt = ?');
        values.push(excerpt);
    }
    
    if (category_id) {
        updateFields.push('category_id = ?');
        values.push(category_id);
    }
    
    if (author) {
        updateFields.push('author = ?');
        values.push(author);
    }
    
    if (status) {
        updateFields.push('status = ?');
        values.push(status);
    }
    
    if (req.files?.featured_image?.[0]) {
        updateFields.push('featured_image = ?');
        values.push(req.files.featured_image[0].filename);
    }
    
    if (req.files?.video?.[0]) {
        updateFields.push('video_url = ?');
        values.push(req.files.video[0].filename);
    }
    
    if (updateFields.length === 0) {
        return res.status(400).json({ error: 'Không có dữ liệu để cập nhật' });
    }
    
    values.push(id);
    const query = `UPDATE articles SET ${updateFields.join(', ')} WHERE id = ?`;
    
    db.query(query, values, (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi cập nhật bài viết', details: err.message });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Không tìm thấy bài viết' });
        }
        
        res.json({ message: 'Cập nhật bài viết thành công' });
    });
});

// Xóa bài viết
app.delete('/api/articles/:id', (req, res) => {
    const { id } = req.params;
    
    db.query('DELETE FROM articles WHERE id = ?', [id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi xóa bài viết', details: err.message });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Không tìm thấy bài viết' });
        }
        
        res.json({ message: 'Xóa bài viết thành công' });
    });
});

// Lấy danh sách categories
app.get('/api/categories', (req, res) => {
    db.query('SELECT * FROM categories ORDER BY name', (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        res.json(results);
    });
});

// Tạo category mới
app.post('/api/categories', (req, res) => {
    const { name } = req.body;
    
    if (!name) {
        return res.status(400).json({ error: 'Tên danh mục là bắt buộc' });
    }
    
    const slug = name.toLowerCase()
        .replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, 'a')
        .replace(/[èéẹẻẽêềếệểễ]/g, 'e')
        .replace(/[ìíịỉĩ]/g, 'i')
        .replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, 'o')
        .replace(/[ùúụủũưừứựửữ]/g, 'u')
        .replace(/[ỳýỵỷỹ]/g, 'y')
        .replace(/đ/g, 'd')
        .replace(/[^a-z0-9 -]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-')
        .trim('-');
    
    db.query('INSERT INTO categories (name, slug) VALUES (?, ?)', [name, slug], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi tạo danh mục', details: err.message });
        }
        
        res.status(201).json({
            message: 'Tạo danh mục thành công',
            id: result.insertId,
            slug: slug
        });
    });
});

// Serve static files
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

// Contact form endpoint
app.post('/api/contact', (req, res) => {
    const { name, email, phone, subject, message, newsletter } = req.body;
    
    if (!name || !email || !subject || !message) {
        return res.status(400).json({ error: 'Vui lòng điền đầy đủ thông tin bắt buộc' });
    }
    
    // Insert contact message into database
    const query = `
        INSERT INTO contact_messages (name, email, phone, subject, message, newsletter, created_at)
        VALUES (?, ?, ?, ?, ?, ?, NOW())
    `;
    
    db.query(query, [name, email, phone || null, subject, message, newsletter ? 1 : 0], (err, result) => {
        if (err) {
            console.error('Lỗi lưu tin nhắn:', err);
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        res.json({ 
            message: 'Tin nhắn đã được gửi thành công',
            id: result.insertId 
        });
    });
});

// Newsletter subscription endpoint
app.post('/api/newsletter', (req, res) => {
    const { email, name } = req.body;
    
    if (!email) {
        return res.status(400).json({ error: 'Email là bắt buộc' });
    }
    
    const query = `
        INSERT INTO newsletter_subscribers (email, name, subscribed_at)
        VALUES (?, ?, NOW())
        ON DUPLICATE KEY UPDATE name = VALUES(name), subscribed_at = NOW()
    `;
    
    db.query(query, [email, name || null], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        res.json({ message: 'Đăng ký newsletter thành công' });
    });
});

// Get contact messages (admin only)
app.get('/api/contact-messages', (req, res) => {
    const { limit = 50, page = 1 } = req.query;
    const offset = (page - 1) * limit;
    
    const query = `
        SELECT * FROM contact_messages 
        ORDER BY created_at DESC 
        LIMIT ? OFFSET ?
    `;
    
    db.query(query, [parseInt(limit), parseInt(offset)], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        res.json(results);
    });
});

// Update contact message status
app.put('/api/contact-messages/:id', (req, res) => {
    const { id } = req.params;
    const { status } = req.body;
    
    const validStatuses = ['new', 'read', 'replied', 'archived'];
    if (!validStatuses.includes(status)) {
        return res.status(400).json({ error: 'Trạng thái không hợp lệ' });
    }
    
    db.query('UPDATE contact_messages SET status = ? WHERE id = ?', [status, id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Không tìm thấy tin nhắn' });
        }
        
        res.json({ message: 'Cập nhật trạng thái thành công' });
    });
});

// Delete contact message
app.delete('/api/contact-messages/:id', (req, res) => {
    const { id } = req.params;
    
    db.query('DELETE FROM contact_messages WHERE id = ?', [id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Không tìm thấy tin nhắn' });
        }
        
        res.json({ message: 'Xóa tin nhắn thành công' });
    });
});

// Media management endpoints
app.get('/api/media', (req, res) => {
    const query = 'SELECT * FROM media_files ORDER BY uploaded_at DESC';
    
    db.query(query, (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        res.json(results);
    });
});

// Upload media files
app.post('/api/media/upload', upload.array('media_file', 10), (req, res) => {
    if (!req.files || req.files.length === 0) {
        return res.status(400).json({ error: 'Không có file nào được upload' });
    }
    
    const uploadedFiles = [];
    let processedFiles = 0;
    
    req.files.forEach(file => {
        const fileType = file.mimetype.startsWith('image/') ? 'image' : 
                        file.mimetype.startsWith('video/') ? 'video' : 'document';
        
        const query = `
            INSERT INTO media_files (filename, original_name, file_type, file_size, mime_type, file_path, uploaded_by)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        `;
        
        db.query(query, [
            file.filename,
            file.originalname,
            fileType,
            file.size,
            file.mimetype,
            file.path,
            'Admin'
        ], (err, result) => {
            processedFiles++;
            
            if (!err) {
                uploadedFiles.push({
                    id: result.insertId,
                    filename: file.filename,
                    original_name: file.originalname
                });
            }
            
            if (processedFiles === req.files.length) {
                res.json({
                    message: 'Upload thành công',
                    files: uploadedFiles
                });
            }
        });
    });
});

// Delete media file
app.delete('/api/media/:id', (req, res) => {
    const { id } = req.params;
    
    // Get file info first
    db.query('SELECT filename FROM media_files WHERE id = ?', [id], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        
        if (results.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy file' });
        }
        
        const filename = results[0].filename;
        
        // Delete from database
        db.query('DELETE FROM media_files WHERE id = ?', [id], (err, result) => {
            if (err) {
                return res.status(500).json({ error: 'Lỗi server', details: err.message });
            }
            
            // Delete physical file
            const filePath = path.join(__dirname, 'uploads', filename);
            fs.unlink(filePath, (err) => {
                if (err) console.error('Lỗi xóa file:', err);
            });
            
            res.json({ message: 'Xóa file thành công' });
        });
    });
});

// Search articles
app.get('/api/search', (req, res) => {
    const { q, category, limit = 20 } = req.query;
    
    if (!q) {
        return res.status(400).json({ error: 'Từ khóa tìm kiếm là bắt buộc' });
    }
    
    let query = `
        SELECT a.*, c.name as category_name, c.slug as category_slug 
        FROM articles a 
        LEFT JOIN categories c ON a.category_id = c.id 
        WHERE a.status = 'published' 
        AND (a.title LIKE ? OR a.content LIKE ? OR a.excerpt LIKE ?)
    `;
    
    const searchTerm = `%${q}%`;
    const params = [searchTerm, searchTerm, searchTerm];
    
    if (category) {
        query += ' AND c.slug = ?';
        params.push(category);
    }
    
    query += ' ORDER BY a.created_at DESC LIMIT ?';
    params.push(parseInt(limit));
    
    db.query(query, params, (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi server', details: err.message });
        }
        res.json(results);
    });
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
    console.log(`Server đang chạy tại http://localhost:${PORT}`);
});

module.exports = app;