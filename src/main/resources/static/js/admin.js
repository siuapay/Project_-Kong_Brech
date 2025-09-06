// Admin Dashboard JavaScript
class AdminDashboard {
    constructor() {
        this.currentTab = 'dashboard';
        this.articles = [];
        this.media = [];
        this.users = [];
        this.contacts = [];
        this.quillEditor = null;
        
        this.init();
    }

    init() {
        console.log('Initializing Admin Dashboard...');
        this.loadData();
        this.setupEventListeners();
        this.initializeEditor();
        this.loadDashboard();
    }

    // Tab Management
    showTab(tabName) {
        // Hide all tabs
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.classList.remove('active');
        });
        
        // Remove active class from all buttons
        document.querySelectorAll('.tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // Show selected tab
        const selectedTab = document.getElementById(`${tabName}-tab`);
        if (selectedTab) {
            selectedTab.classList.add('active');
        }
        
        // Add active class to clicked button
        event.target.classList.add('active');
        
        this.currentTab = tabName;
        
        // Load tab-specific content
        switch(tabName) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'articles':
                this.loadArticles();
                break;
            case 'pages':
                this.loadPages();
                break;
            case 'media':
                this.loadMedia();
                break;
            case 'users':
                this.loadUsers();
                break;
            case 'contacts':
                this.loadContacts();
                break;
            case 'settings':
                this.loadSettings();
                break;
        }
    }

    // Data Loading
    loadData() {
        // Load sample data (in real app, this would be from API)
        this.articles = [
            {
                id: 1,
                title: 'Chương trình Giáng sinh 2024',
                category: 'su-kien',
                author: 'Admin',
                status: 'published',
                created_at: '2024-12-15',
                views: 156,
                excerpt: 'Chương trình Giáng sinh đặc biệt với nhiều hoạt động ý nghĩa...'
            },
            {
                id: 2,
                title: 'Thông báo họp Ban Chấp sự',
                category: 'thong-bao',
                author: 'Admin',
                status: 'published',
                created_at: '2024-12-10',
                views: 89,
                excerpt: 'Cuộc họp định kỳ tháng 12 của Ban Chấp sự...'
            },
            {
                id: 3,
                title: 'Chia sẻ về lòng biết ơn',
                category: 'chia-se',
                author: 'Mục sư',
                status: 'draft',
                created_at: '2024-12-08',
                views: 45,
                excerpt: 'Bài chia sẻ về tầm quan trọng của lòng biết ơn...'
            }
        ];

        this.contacts = [
            {
                id: 1,
                name: 'Nguyễn Văn A',
                email: 'nguyenvana@email.com',
                subject: 'Hỏi về lịch thờ phượng',
                message: 'Xin chào, tôi muốn biết lịch thờ phượng Chúa nhật...',
                created_at: '2024-12-15 10:30',
                status: 'unread'
            },
            {
                id: 2,
                name: 'Trần Thị B',
                email: 'tranthib@email.com',
                subject: 'Đăng ký tham gia ban ca đoàn',
                message: 'Em muốn tham gia ban ca đoàn của hội thánh...',
                created_at: '2024-12-14 15:45',
                status: 'read'
            }
        ];

        this.users = [
            {
                id: 1,
                name: 'Admin',
                email: 'admin@httlkongbrech.org',
                role: 'administrator',
                created_at: '2024-01-01',
                last_login: '2024-12-15 09:00'
            }
        ];

        this.media = [
            {
                id: 1,
                name: 'church-banner.jpg',
                type: 'image',
                size: '2.5 MB',
                url: '/media/church-banner.jpg',
                created_at: '2024-12-10'
            },
            {
                id: 2,
                name: 'christmas-video.mp4',
                type: 'video',
                size: '15.8 MB',
                url: '/media/christmas-video.mp4',
                created_at: '2024-12-08'
            }
        ];
    }

    // Dashboard
    loadDashboard() {
        this.updateStats();
        this.loadRecentArticles();
        this.loadRecentMessages();
        this.initAnalyticsChart();
    }

    updateStats() {
        document.getElementById('total-articles').textContent = this.articles.length;
        document.getElementById('total-views').textContent = this.articles.reduce((sum, article) => sum + article.views, 0).toLocaleString();
        document.getElementById('total-messages').textContent = this.contacts.filter(c => c.status === 'unread').length;
        document.getElementById('total-media').textContent = this.media.length;
    }

    loadRecentArticles() {
        const container = document.getElementById('recent-articles');
        const recentArticles = this.articles.slice(0, 5);
        
        container.innerHTML = recentArticles.map(article => `
            <div class="recent-item">
                <div class="item-info">
                    <h5>${article.title}</h5>
                    <p>${article.created_at} • ${article.views} lượt xem</p>
                </div>
                <span class="status-badge ${article.status}">${article.status === 'published' ? 'Đã xuất bản' : 'Bản nháp'}</span>
            </div>
        `).join('');
    }

    loadRecentMessages() {
        const container = document.getElementById('recent-messages');
        const recentMessages = this.contacts.slice(0, 5);
        
        container.innerHTML = recentMessages.map(message => `
            <div class="recent-item">
                <div class="item-info">
                    <h5>${message.name}</h5>
                    <p>${message.subject}</p>
                </div>
                <span class="status-badge ${message.status}">${message.status === 'unread' ? 'Chưa đọc' : 'Đã đọc'}</span>
            </div>
        `).join('');
    }

    initAnalyticsChart() {
        const ctx = document.getElementById('analytics-chart');
        if (ctx) {
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
                    datasets: [{
                        label: 'Lượt xem',
                        data: [65, 59, 80, 81, 56, 55, 40],
                        borderColor: 'rgb(102, 126, 234)',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }
    }

    // Articles Management
    loadArticles() {
        const container = document.getElementById('articles-container');
        
        container.innerHTML = this.articles.map(article => `
            <div class="content-item" data-id="${article.id}">
                <div class="item-info">
                    <h4>${article.title}</h4>
                    <p>${article.excerpt}</p>
                    <div class="item-meta">
                        <span><i class="fas fa-user"></i> ${article.author}</span>
                        <span><i class="fas fa-calendar"></i> ${article.created_at}</span>
                        <span><i class="fas fa-eye"></i> ${article.views} lượt xem</span>
                        <span class="status-badge ${article.status}">${article.status === 'published' ? 'Đã xuất bản' : 'Bản nháp'}</span>
                    </div>
                </div>
                <div class="item-actions">
                    <button class="btn btn-sm btn-primary" onclick="adminDashboard.editArticle(${article.id})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteArticle(${article.id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        `).join('');
    }

    openArticleModal(articleId = null) {
        const modal = document.getElementById('article-modal');
        const title = document.getElementById('article-modal-title');
        
        if (articleId) {
            const article = this.articles.find(a => a.id === articleId);
            title.textContent = 'Chỉnh sửa bài viết';
            this.populateArticleForm(article);
        } else {
            title.textContent = 'Thêm bài viết mới';
            this.clearArticleForm();
        }
        
        modal.style.display = 'block';
    }

    populateArticleForm(article) {
        const form = document.getElementById('article-form');
        form.querySelector('[name="id"]').value = article.id;
        form.querySelector('[name="title"]').value = article.title;
        form.querySelector('[name="category"]').value = article.category;
        form.querySelector('[name="excerpt"]').value = article.excerpt;
        form.querySelector('[name="author"]').value = article.author;
        form.querySelector('[name="status"]').value = article.status;
        
        if (this.quillEditor) {
            this.quillEditor.setText(article.content || '');
        }
    }

    clearArticleForm() {
        const form = document.getElementById('article-form');
        form.reset();
        if (this.quillEditor) {
            this.quillEditor.setText('');
        }
    }

    saveArticle() {
        const form = document.getElementById('article-form');
        const formData = new FormData(form);
        const articleData = Object.fromEntries(formData);
        
        // Get content from Quill editor
        if (this.quillEditor) {
            articleData.content = this.quillEditor.root.innerHTML;
        }
        
        const articleId = articleData.id;
        
        if (articleId) {
            // Update existing article
            const index = this.articles.findIndex(a => a.id == articleId);
            if (index !== -1) {
                this.articles[index] = { ...this.articles[index], ...articleData };
            }
        } else {
            // Create new article
            articleData.id = Date.now();
            articleData.created_at = new Date().toISOString().split('T')[0];
            articleData.views = 0;
            this.articles.unshift(articleData);
        }
        
        this.closeModal('article-modal');
        this.loadArticles();
        this.showNotification('Bài viết đã được lưu thành công!', 'success');
    }

    editArticle(id) {
        this.openArticleModal(id);
    }

    deleteArticle(id) {
        if (confirm('Bạn có chắc chắn muốn xóa bài viết này?')) {
            this.articles = this.articles.filter(a => a.id !== id);
            this.loadArticles();
            this.showNotification('Bài viết đã được xóa!', 'success');
        }
    }

    // Pages Management
    loadPages() {
        // Pages are already displayed in HTML
        console.log('Pages loaded');
    }

    editPage(filename) {
        const modal = document.getElementById('page-modal');
        const title = document.getElementById('page-modal-title');
        const content = document.getElementById('page-content');
        
        title.textContent = `Chỉnh sửa ${filename}`;
        
        // In a real application, you would load the actual file content
        content.value = `<!-- Nội dung của ${filename} -->\n<h1>Đang chỉnh sửa ${filename}</h1>\n<p>Nội dung trang sẽ được tải ở đây...</p>`;
        
        modal.style.display = 'block';
    }

    savePage() {
        const content = document.getElementById('page-content').value;
        // In a real application, you would save the content to the actual file
        console.log('Saving page content:', content);
        
        this.closeModal('page-modal');
        this.showNotification('Trang đã được lưu thành công!', 'success');
    }

    previewPage() {
        const content = document.getElementById('page-content').value;
        const previewWindow = window.open('', '_blank');
        previewWindow.document.write(content);
        previewWindow.document.close();
    }

    // Media Management
    loadMedia() {
        const container = document.getElementById('media-container');
        
        container.innerHTML = this.media.map(item => `
            <div class="media-item" data-id="${item.id}">
                <div class="media-preview">
                    ${item.type === 'image' ? 
                        `<img src="${item.url}" alt="${item.name}" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                         <div style="display:none;"><i class="fas fa-image"></i></div>` :
                        `<i class="fas fa-${item.type === 'video' ? 'video' : 'file'}"></i>`
                    }
                </div>
                <div class="media-info">
                    <h5>${item.name}</h5>
                    <p>${item.size} • ${item.created_at}</p>
                    <div class="media-actions">
                        <button class="btn btn-sm btn-primary" onclick="adminDashboard.copyMediaUrl('${item.url}')">
                            <i class="fas fa-copy"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteMedia(${item.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    openMediaUpload() {
        const modal = document.getElementById('media-modal');
        modal.style.display = 'block';
    }

    uploadMedia() {
        const fileInput = document.getElementById('media-files');
        const files = fileInput.files;
        
        if (files.length === 0) {
            this.showNotification('Vui lòng chọn file để upload!', 'error');
            return;
        }
        
        // Simulate upload process
        const progressContainer = document.getElementById('upload-progress');
        const progressFill = progressContainer.querySelector('.progress-fill');
        const progressText = progressContainer.querySelector('.progress-text');
        
        progressContainer.style.display = 'block';
        
        let progress = 0;
        const interval = setInterval(() => {
            progress += 10;
            progressFill.style.width = progress + '%';
            progressText.textContent = `Đang upload... ${progress}%`;
            
            if (progress >= 100) {
                clearInterval(interval);
                
                // Add uploaded files to media array
                Array.from(files).forEach(file => {
                    this.media.unshift({
                        id: Date.now() + Math.random(),
                        name: file.name,
                        type: file.type.startsWith('image/') ? 'image' : file.type.startsWith('video/') ? 'video' : 'document',
                        size: this.formatFileSize(file.size),
                        url: URL.createObjectURL(file),
                        created_at: new Date().toISOString().split('T')[0]
                    });
                });
                
                setTimeout(() => {
                    this.closeModal('media-modal');
                    this.loadMedia();
                    this.showNotification('Upload thành công!', 'success');
                    progressContainer.style.display = 'none';
                    progressFill.style.width = '0%';
                }, 500);
            }
        }, 200);
    }

    copyMediaUrl(url) {
        navigator.clipboard.writeText(url).then(() => {
            this.showNotification('URL đã được copy!', 'success');
        });
    }

    deleteMedia(id) {
        if (confirm('Bạn có chắc chắn muốn xóa file này?')) {
            this.media = this.media.filter(m => m.id !== id);
            this.loadMedia();
            this.showNotification('File đã được xóa!', 'success');
        }
    }

    // Users Management
    loadUsers() {
        const container = document.getElementById('users-container');
        
        container.innerHTML = this.users.map(user => `
            <div class="user-item" data-id="${user.id}">
                <div class="user-avatar-large">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-details">
                    <h4>${user.name}</h4>
                    <p>${user.email} • ${user.role}</p>
                    <small>Đăng nhập lần cuối: ${user.last_login}</small>
                </div>
                <div class="item-actions">
                    <button class="btn btn-sm btn-primary" onclick="adminDashboard.editUser(${user.id})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteUser(${user.id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        `).join('');
    }

    // Contacts Management
    loadContacts() {
        const container = document.getElementById('contacts-container');
        
        container.innerHTML = this.contacts.map(contact => `
            <div class="contact-item ${contact.status}" data-id="${contact.id}">
                <div class="contact-header">
                    <span class="contact-sender">${contact.name} (${contact.email})</span>
                    <span class="contact-date">${contact.created_at}</span>
                </div>
                <div class="contact-subject">${contact.subject}</div>
                <div class="contact-message">${contact.message}</div>
                <div class="item-actions" style="margin-top: 1rem;">
                    <button class="btn btn-sm btn-primary" onclick="adminDashboard.markAsRead(${contact.id})">
                        <i class="fas fa-check"></i> Đánh dấu đã đọc
                    </button>
                    <button class="btn btn-sm btn-success" onclick="adminDashboard.replyContact(${contact.id})">
                        <i class="fas fa-reply"></i> Trả lời
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteContact(${contact.id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        `).join('');
    }

    markAsRead(id) {
        const contact = this.contacts.find(c => c.id === id);
        if (contact) {
            contact.status = 'read';
            this.loadContacts();
            this.updateStats();
        }
    }

    markAllAsRead() {
        this.contacts.forEach(contact => contact.status = 'read');
        this.loadContacts();
        this.updateStats();
        this.showNotification('Đã đánh dấu tất cả tin nhắn là đã đọc!', 'success');
    }

    deleteContact(id) {
        if (confirm('Bạn có chắc chắn muốn xóa tin nhắn này?')) {
            this.contacts = this.contacts.filter(c => c.id !== id);
            this.loadContacts();
            this.updateStats();
            this.showNotification('Tin nhắn đã được xóa!', 'success');
        }
    }

    deleteAllMessages() {
        if (confirm('Bạn có chắc chắn muốn xóa tất cả tin nhắn?')) {
            this.contacts = [];
            this.loadContacts();
            this.updateStats();
            this.showNotification('Đã xóa tất cả tin nhắn!', 'success');
        }
    }

    replyContact(id) {
        const contact = this.contacts.find(c => c.id === id);
        if (contact) {
            const subject = `Re: ${contact.subject}`;
            const mailtoLink = `mailto:${contact.email}?subject=${encodeURIComponent(subject)}`;
            window.open(mailtoLink);
        }
    }

    // Settings Management
    loadSettings() {
        console.log('Settings loaded');
    }

    // Utility Functions
    setupEventListeners() {
        // File upload preview
        document.addEventListener('change', (e) => {
            if (e.target.id === 'article-image') {
                this.previewImage(e.target, 'article-image-preview');
            }
        });

        // Drag and drop for media upload
        const uploadArea = document.getElementById('upload-area');
        if (uploadArea) {
            uploadArea.addEventListener('dragover', (e) => {
                e.preventDefault();
                uploadArea.classList.add('dragover');
            });

            uploadArea.addEventListener('dragleave', () => {
                uploadArea.classList.remove('dragover');
            });

            uploadArea.addEventListener('drop', (e) => {
                e.preventDefault();
                uploadArea.classList.remove('dragover');
                
                const files = e.dataTransfer.files;
                document.getElementById('media-files').files = files;
            });

            uploadArea.addEventListener('click', () => {
                document.getElementById('media-files').click();
            });
        }

        // Form submissions
        document.addEventListener('submit', (e) => {
            e.preventDefault();
            
            if (e.target.id === 'general-settings-form') {
                this.saveGeneralSettings(e.target);
            } else if (e.target.id === 'security-settings-form') {
                this.changePassword(e.target);
            }
        });
    }

    initializeEditor() {
        const editorElement = document.getElementById('article-editor');
        if (editorElement) {
            this.quillEditor = new Quill('#article-editor', {
                theme: 'snow',
                modules: {
                    toolbar: [
                        [{ 'header': [1, 2, 3, false] }],
                        ['bold', 'italic', 'underline'],
                        ['link', 'image'],
                        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                        ['clean']
                    ]
                }
            });
        }
    }

    previewImage(input, previewId) {
        const preview = document.getElementById(previewId);
        const file = input.files[0];
        
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                preview.src = e.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    }

    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
        }
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'times' : 'info'}-circle"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.remove()">&times;</button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    // Settings Functions
    saveGeneralSettings(form) {
        const formData = new FormData(form);
        const settings = Object.fromEntries(formData);
        
        console.log('Saving general settings:', settings);
        this.showNotification('Cài đặt đã được lưu!', 'success');
    }

    changePassword(form) {
        const formData = new FormData(form);
        const passwords = Object.fromEntries(formData);
        
        if (passwords.new_password !== passwords.confirm_password) {
            this.showNotification('Mật khẩu xác nhận không khớp!', 'error');
            return;
        }
        
        console.log('Changing password...');
        this.showNotification('Mật khẩu đã được thay đổi!', 'success');
        form.reset();
    }

    createBackup() {
        this.showNotification('Đang tạo bản sao lưu...', 'info');
        
        setTimeout(() => {
            const backup = {
                articles: this.articles,
                contacts: this.contacts,
                users: this.users,
                media: this.media,
                timestamp: new Date().toISOString()
            };
            
            const dataStr = JSON.stringify(backup, null, 2);
            const dataBlob = new Blob([dataStr], {type: 'application/json'});
            
            const link = document.createElement('a');
            link.href = URL.createObjectURL(dataBlob);
            link.download = `backup-${new Date().toISOString().split('T')[0]}.json`;
            link.click();
            
            this.showNotification('Bản sao lưu đã được tạo!', 'success');
        }, 2000);
    }

    restoreBackup() {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = '.json';
        
        input.onchange = (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    try {
                        const backup = JSON.parse(e.target.result);
                        
                        if (confirm('Bạn có chắc chắn muốn khôi phục từ bản sao lưu này? Dữ liệu hiện tại sẽ bị ghi đè.')) {
                            this.articles = backup.articles || [];
                            this.contacts = backup.contacts || [];
                            this.users = backup.users || [];
                            this.media = backup.media || [];
                            
                            this.loadData();
                            this.showNotification('Khôi phục dữ liệu thành công!', 'success');
                        }
                    } catch (error) {
                        this.showNotification('File sao lưu không hợp lệ!', 'error');
                    }
                };
                reader.readAsText(file);
            }
        };
        
        input.click();
    }

    // Search and Filter Functions
    filterArticles() {
        const categoryFilter = document.getElementById('article-category-filter').value;
        const statusFilter = document.getElementById('article-status-filter').value;
        
        let filteredArticles = this.articles;
        
        if (categoryFilter) {
            filteredArticles = filteredArticles.filter(a => a.category === categoryFilter);
        }
        
        if (statusFilter) {
            filteredArticles = filteredArticles.filter(a => a.status === statusFilter);
        }
        
        this.displayFilteredArticles(filteredArticles);
    }

    searchArticles() {
        const searchTerm = document.getElementById('article-search').value.toLowerCase();
        
        const filteredArticles = this.articles.filter(article => 
            article.title.toLowerCase().includes(searchTerm) ||
            article.excerpt.toLowerCase().includes(searchTerm) ||
            article.author.toLowerCase().includes(searchTerm)
        );
        
        this.displayFilteredArticles(filteredArticles);
    }

    displayFilteredArticles(articles) {
        const container = document.getElementById('articles-container');
        
        container.innerHTML = articles.map(article => `
            <div class="content-item" data-id="${article.id}">
                <div class="item-info">
                    <h4>${article.title}</h4>
                    <p>${article.excerpt}</p>
                    <div class="item-meta">
                        <span><i class="fas fa-user"></i> ${article.author}</span>
                        <span><i class="fas fa-calendar"></i> ${article.created_at}</span>
                        <span><i class="fas fa-eye"></i> ${article.views} lượt xem</span>
                        <span class="status-badge ${article.status}">${article.status === 'published' ? 'Đã xuất bản' : 'Bản nháp'}</span>
                    </div>
                </div>
                <div class="item-actions">
                    <button class="btn btn-sm btn-primary" onclick="adminDashboard.editArticle(${article.id})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteArticle(${article.id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        `).join('');
    }

    filterMedia() {
        const typeFilter = document.getElementById('media-type-filter').value;
        
        let filteredMedia = this.media;
        
        if (typeFilter) {
            filteredMedia = filteredMedia.filter(m => m.type === typeFilter);
        }
        
        this.displayFilteredMedia(filteredMedia);
    }

    searchMedia() {
        const searchTerm = document.getElementById('media-search').value.toLowerCase();
        
        const filteredMedia = this.media.filter(item => 
            item.name.toLowerCase().includes(searchTerm)
        );
        
        this.displayFilteredMedia(filteredMedia);
    }

    displayFilteredMedia(media) {
        const container = document.getElementById('media-container');
        
        container.innerHTML = media.map(item => `
            <div class="media-item" data-id="${item.id}">
                <div class="media-preview">
                    ${item.type === 'image' ? 
                        `<img src="${item.url}" alt="${item.name}" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                         <div style="display:none;"><i class="fas fa-image"></i></div>` :
                        `<i class="fas fa-${item.type === 'video' ? 'video' : 'file'}"></i>`
                    }
                </div>
                <div class="media-info">
                    <h5>${item.name}</h5>
                    <p>${item.size} • ${item.created_at}</p>
                    <div class="media-actions">
                        <button class="btn btn-sm btn-primary" onclick="adminDashboard.copyMediaUrl('${item.url}')">
                            <i class="fas fa-copy"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="adminDashboard.deleteMedia(${item.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }
}

// Global functions for HTML onclick events
function showTab(tabName) {
    window.adminDashboard.showTab(tabName);
}

function openArticleModal(articleId = null) {
    window.adminDashboard.openArticleModal(articleId);
}

function openPageModal() {
    window.adminDashboard.openPageModal();
}

function openMediaUpload() {
    window.adminDashboard.openMediaUpload();
}

function openUserModal() {
    // Implementation for user modal
    console.log('Open user modal');
}

function editPage(filename) {
    window.adminDashboard.editPage(filename);
}

function saveArticle() {
    window.adminDashboard.saveArticle();
}

function savePage() {
    window.adminDashboard.savePage();
}

function previewPage() {
    window.adminDashboard.previewPage();
}

function uploadMedia() {
    window.adminDashboard.uploadMedia();
}

function closeModal(modalId) {
    window.adminDashboard.closeModal(modalId);
}

function filterArticles() {
    window.adminDashboard.filterArticles();
}

function searchArticles() {
    window.adminDashboard.searchArticles();
}

function filterMedia() {
    window.adminDashboard.filterMedia();
}

function searchMedia() {
    window.adminDashboard.searchMedia();
}

function markAllAsRead() {
    window.adminDashboard.markAllAsRead();
}

function deleteAllMessages() {
    window.adminDashboard.deleteAllMessages();
}

function createBackup() {
    window.adminDashboard.createBackup();
}

function restoreBackup() {
    window.adminDashboard.restoreBackup();
}

function insertHTML(type) {
    const textarea = document.getElementById('page-content');
    const templates = {
        header: '<header class="page-header">\n    <h1>Tiêu đề trang</h1>\n    <p>Mô tả trang</p>\n</header>\n\n',
        section: '<section class="content-section">\n    <h2>Tiêu đề section</h2>\n    <p>Nội dung section</p>\n</section>\n\n',
        image: '<div class="image-container">\n    <img src="/path/to/image.jpg" alt="Mô tả hình ảnh">\n    <p class="image-caption">Chú thích hình ảnh</p>\n</div>\n\n',
        button: '<button class="btn btn-primary" onclick="handleClick()">\n    <i class="fas fa-click"></i> Nút bấm\n</button>\n\n'
    };
    
    const template = templates[type] || '';
    const cursorPos = textarea.selectionStart;
    const textBefore = textarea.value.substring(0, cursorPos);
    const textAfter = textarea.value.substring(cursorPos);
    
    textarea.value = textBefore + template + textAfter;
    textarea.focus();
    textarea.setSelectionRange(cursorPos + template.length, cursorPos + template.length);
}

// Initialize admin dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.adminDashboard = new AdminDashboard();
    console.log('Admin Dashboard initialized successfully!');
});