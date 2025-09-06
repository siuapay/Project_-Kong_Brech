// Quản nhiệm hội thánh - JavaScript functions
class ChurchManagement {
    constructor() {
        this.init();
        this.loadDashboardData();
        this.setupEventListeners();
    }

    init() {
        console.log('Khởi tạo hệ thống quản nhiệm hội thánh...');
        this.setupAutoRefresh();
    }

    // Load dashboard data
    loadDashboardData() {
        // Simulate loading data from server
        const data = {
            totalMembers: 245,
            monthlyEvents: 12,
            monthlyDonation: 85000000,
            locations: 5,
            attendanceRate: 95,
            groupParticipation: 78
        };

        this.updateDashboard(data);
    }

    updateDashboard(data) {
        // Update overview cards
        const cards = document.querySelectorAll('.overview-card');
        if (cards.length >= 4) {
            cards[0].querySelector('h3').textContent = data.totalMembers;
            cards[1].querySelector('h3').textContent = data.monthlyEvents;
            cards[2].querySelector('h3').textContent = this.formatCurrency(data.monthlyDonation);
            cards[3].querySelector('h3').textContent = data.locations;
        }
    }

    formatCurrency(amount) {
        return (amount / 1000000).toFixed(0) + 'M';
    }

    // Modal functions
    openModal(type) {
        const modalContent = this.getModalContent(type);
        this.showModal(modalContent);
    }

    getModalContent(type) {
        const contents = {
            'add-member': {
                title: 'Thêm tín hữu mới',
                body: `
                    <form id="addMemberForm">
                        <div class="form-group">
                            <label>Họ và tên:</label>
                            <input type="text" name="fullName" required>
                        </div>
                        <div class="form-group">
                            <label>Ngày sinh:</label>
                            <input type="date" name="birthDate" required>
                        </div>
                        <div class="form-group">
                            <label>Số điện thoại:</label>
                            <input type="tel" name="phone">
                        </div>
                        <div class="form-group">
                            <label>Địa chỉ:</label>
                            <textarea name="address" rows="3"></textarea>
                        </div>
                        <div class="form-group">
                            <label>Điểm nhóm:</label>
                            <select name="location">
                                <option value="">Chọn điểm nhóm</option>
                                <option value="main">Hội thánh chính</option>
                                <option value="easup">Ea Súp</option>
                                <option value="eakar">Ea Kar</option>
                            </select>
                        </div>
                    </form>
                `
            },
            'schedule-event': {
                title: 'Lên lịch sự kiện',
                body: `
                    <form id="scheduleEventForm">
                        <div class="form-group">
                            <label>Tên sự kiện:</label>
                            <input type="text" name="eventName" required>
                        </div>
                        <div class="form-group">
                            <label>Ngày:</label>
                            <input type="date" name="eventDate" required>
                        </div>
                        <div class="form-group">
                            <label>Thời gian:</label>
                            <input type="time" name="eventTime" required>
                        </div>
                        <div class="form-group">
                            <label>Địa điểm:</label>
                            <input type="text" name="eventLocation" required>
                        </div>
                        <div class="form-group">
                            <label>Loại sự kiện:</label>
                            <select name="eventType">
                                <option value="worship">Thờ phượng</option>
                                <option value="meeting">Họp</option>
                                <option value="charity">Từ thiện</option>
                                <option value="celebration">Kỷ niệm</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Mô tả:</label>
                            <textarea name="description" rows="3"></textarea>
                        </div>
                    </form>
                `
            },
            'financial-report': {
                title: 'Báo cáo tài chính',
                body: `
                    <div class="financial-summary">
                        <h4>Tháng này:</h4>
                        <div class="financial-item">
                            <span>Thu nhập:</span>
                            <span class="amount positive">85,000,000 VNĐ</span>
                        </div>
                        <div class="financial-item">
                            <span>Chi phí:</span>
                            <span class="amount negative">72,000,000 VNĐ</span>
                        </div>
                        <div class="financial-item total">
                            <span>Còn lại:</span>
                            <span class="amount positive">13,000,000 VNĐ</span>
                        </div>
                    </div>
                    <div class="report-actions">
                        <button onclick="exportFinancialReport()" class="btn-primary">Xuất báo cáo</button>
                        <button onclick="addTransaction()" class="btn-secondary">Thêm giao dịch</button>
                    </div>
                `
            },
            'send-notification': {
                title: 'Gửi thông báo',
                body: `
                    <form id="notificationForm">
                        <div class="form-group">
                            <label>Tiêu đề:</label>
                            <input type="text" name="title" required>
                        </div>
                        <div class="form-group">
                            <label>Nội dung:</label>
                            <textarea name="content" rows="4" required></textarea>
                        </div>
                        <div class="form-group">
                            <label>Gửi đến:</label>
                            <select name="recipients" multiple>
                                <option value="all">Tất cả tín hữu</option>
                                <option value="leaders">Ban chấp sự</option>
                                <option value="youth">Ban thanh niên</option>
                                <option value="women">Hội phụ nữ</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Ưu tiên:</label>
                            <select name="priority">
                                <option value="normal">Bình thường</option>
                                <option value="high">Cao</option>
                                <option value="urgent">Khẩn cấp</option>
                            </select>
                        </div>
                    </form>
                `
            }
        };

        return contents[type] || { title: 'Thông báo', body: 'Chức năng đang phát triển...' };
    }

    showModal(content) {
        // Create modal if not exists
        let modal = document.getElementById('managementModal');
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'managementModal';
            modal.className = 'modal';
            modal.innerHTML = `
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 id="modalTitle"></h3>
                        <span class="close" onclick="closeModal()">&times;</span>
                    </div>
                    <div class="modal-body" id="modalBody"></div>
                    <div class="modal-footer">
                        <button onclick="closeModal()" class="btn-secondary">Hủy</button>
                        <button onclick="submitModal()" class="btn-primary">Xác nhận</button>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }

        document.getElementById('modalTitle').textContent = content.title;
        document.getElementById('modalBody').innerHTML = content.body;
        modal.style.display = 'block';
    }

    closeModal() {
        const modal = document.getElementById('managementModal');
        if (modal) {
            modal.style.display = 'none';
        }
    }

    submitModal() {
        // Handle form submission based on active form
        const forms = document.querySelectorAll('#managementModal form');
        if (forms.length > 0) {
            const form = forms[0];
            const formData = new FormData(form);
            this.processFormData(form.id, formData);
        }
        this.closeModal();
    }

    processFormData(formId, formData) {
        console.log(`Processing ${formId}:`, Object.fromEntries(formData));
        
        switch(formId) {
            case 'addMemberForm':
                this.addMember(formData);
                break;
            case 'scheduleEventForm':
                this.scheduleEvent(formData);
                break;
            case 'notificationForm':
                this.sendNotification(formData);
                break;
        }
    }

    addMember(formData) {
        const memberData = Object.fromEntries(formData);
        console.log('Thêm tín hữu mới:', memberData);
        this.showNotification('Đã thêm tín hữu mới thành công!', 'success');
        // Update member count
        this.updateMemberCount();
    }

    scheduleEvent(formData) {
        const eventData = Object.fromEntries(formData);
        console.log('Lên lịch sự kiện:', eventData);
        this.showNotification('Đã tạo sự kiện mới thành công!', 'success');
        this.addToRecentActivities('Sự kiện được tạo', `${eventData.eventName} đã được lên lịch`);
    }

    sendNotification(formData) {
        const notificationData = Object.fromEntries(formData);
        console.log('Gửi thông báo:', notificationData);
        this.showNotification('Đã gửi thông báo thành công!', 'success');
    }

    // Quick action functions
    exportMembers() {
        console.log('Xuất danh sách tín hữu...');
        this.showNotification('Đang xuất file Excel...', 'info');
        // Simulate export
        setTimeout(() => {
            this.showNotification('Đã xuất file thành công!', 'success');
        }, 2000);
    }

    openFinancialReport() {
        this.openModal('financial-report');
    }

    addTransaction() {
        const amount = prompt('Nhập số tiền:');
        const type = confirm('Thu nhập? (OK = Thu, Cancel = Chi)') ? 'income' : 'expense';
        const description = prompt('Mô tả:');
        
        if (amount && description) {
            console.log('Thêm giao dịch:', { amount, type, description });
            this.showNotification('Đã ghi nhận giao dịch!', 'success');
            this.addToRecentActivities('Giao dịch mới', `${type === 'income' ? 'Thu' : 'Chi'}: ${amount} VNĐ`);
        }
    }

    viewCalendar() {
        this.showNotification('Đang tải lịch sự kiện...', 'info');
        // Redirect to calendar view or open calendar modal
        console.log('Mở lịch sự kiện');
    }

    createEvent() {
        this.openModal('schedule-event');
    }

    addLocation() {
        const locationName = prompt('Tên điểm nhóm mới:');
        const address = prompt('Địa chỉ:');
        
        if (locationName && address) {
            console.log('Thêm điểm nhóm:', { locationName, address });
            this.showNotification('Đã thêm điểm nhóm mới!', 'success');
        }
    }

    viewReports() {
        this.showNotification('Đang tải báo cáo...', 'info');
        console.log('Xem báo cáo thống kê');
    }

    generateReport() {
        const reportType = prompt('Loại báo cáo (monthly/yearly/custom):');
        if (reportType) {
            console.log('Tạo báo cáo:', reportType);
            this.showNotification('Đang tạo báo cáo...', 'info');
        }
    }

    viewNotifications() {
        console.log('Xem danh sách thông báo');
        this.showNotification('Đang tải thông báo...', 'info');
    }

    sendNotificationQuick() {
        this.openModal('send-notification');
    }

    // Utility functions
    updateMemberCount() {
        const memberCard = document.querySelector('.overview-card h3');
        if (memberCard) {
            const currentCount = parseInt(memberCard.textContent);
            memberCard.textContent = currentCount + 1;
        }
    }

    addToRecentActivities(title, description) {
        const activitiesContainer = document.querySelector('.activities-container');
        if (activitiesContainer) {
            const newActivity = document.createElement('div');
            newActivity.className = 'activity-item';
            newActivity.innerHTML = `
                <div class="activity-icon">
                    <i class="fas fa-plus"></i>
                </div>
                <div class="activity-content">
                    <h4>${title}</h4>
                    <p>${description}</p>
                    <span class="activity-time">Vừa xong</span>
                </div>
            `;
            activitiesContainer.insertBefore(newActivity, activitiesContainer.firstChild);
        }
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'times' : 'info'}-circle"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.remove()">&times;</button>
        `;

        // Add to page
        document.body.appendChild(notification);

        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    setupEventListeners() {
        // Setup click handlers for action cards
        document.querySelectorAll('.action-card').forEach(card => {
            card.addEventListener('click', (e) => {
                const onclick = card.getAttribute('onclick');
                if (onclick) {
                    eval(onclick);
                }
            });
        });

        // Setup module button handlers
        document.querySelectorAll('.module-btn').forEach(btn => {
            if (btn.getAttribute('onclick')) {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    eval(btn.getAttribute('onclick'));
                });
            }
        });
    }

    setupAutoRefresh() {
        // Auto-refresh data every 5 minutes
        setInterval(() => {
            console.log('Làm mới dữ liệu dashboard...');
            this.loadDashboardData();
        }, 300000);
    }
}

// Global functions for backward compatibility
function openModal(type) {
    window.churchManagement.openModal(type);
}

function closeModal() {
    window.churchManagement.closeModal();
}

function submitModal() {
    window.churchManagement.submitModal();
}

function exportMembers() {
    window.churchManagement.exportMembers();
}

function openFinancialReport() {
    window.churchManagement.openFinancialReport();
}

function addTransaction() {
    window.churchManagement.addTransaction();
}

function viewCalendar() {
    window.churchManagement.viewCalendar();
}

function createEvent() {
    window.churchManagement.createEvent();
}

function addLocation() {
    window.churchManagement.addLocation();
}

function viewReports() {
    window.churchManagement.viewReports();
}

function generateReport() {
    window.churchManagement.generateReport();
}

function viewNotifications() {
    window.churchManagement.viewNotifications();
}

function sendNotification() {
    window.churchManagement.sendNotificationQuick();
}

function exportFinancialReport() {
    window.churchManagement.showNotification('Đang xuất báo cáo tài chính...', 'info');
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.churchManagement = new ChurchManagement();
    console.log('Hệ thống quản nhiệm hội thánh đã sẵn sàng!');
});
// 
Additional Management Features
class ReportGenerator {
    static generateMemberReport() {
        const reportData = {
            totalMembers: 245,
            newThisMonth: 5,
            activeMembers: 220,
            inactiveMembers: 25,
            ageGroups: {
                children: 45,
                youth: 68,
                adults: 98,
                elderly: 34
            },
            locations: {
                main: 180,
                easup: 35,
                eakar: 20,
                others: 10
            }
        };

        return this.formatReport('Báo cáo tín hữu', reportData);
    }

    static generateFinancialReport() {
        const reportData = {
            income: {
                donations: 75000000,
                offerings: 8000000,
                events: 2000000
            },
            expenses: {
                utilities: 15000000,
                maintenance: 12000000,
                programs: 25000000,
                charity: 20000000
            },
            balance: 13000000
        };

        return this.formatReport('Báo cáo tài chính', reportData);
    }

    static formatReport(title, data) {
        return {
            title,
            data,
            generatedAt: new Date().toLocaleString('vi-VN'),
            generatedBy: 'Hệ thống quản lý'
        };
    }
}

// Event Calendar System
class EventCalendar {
    constructor() {
        this.events = [
            {
                id: 1,
                title: 'Lễ thờ phượng Chúa nhật',
                date: '2025-01-15',
                time: '08:00',
                location: 'Hội thánh chính',
                type: 'worship',
                recurring: 'weekly'
            },
            {
                id: 2,
                title: 'Họp Ban Thanh niên',
                date: '2025-01-18',
                time: '19:00',
                location: 'Phòng họp',
                type: 'meeting',
                recurring: 'monthly'
            },
            {
                id: 3,
                title: 'Chương trình từ thiện',
                date: '2025-01-22',
                time: '14:00',
                location: 'Thôn Ea Súp',
                type: 'charity',
                recurring: 'none'
            }
        ];
    }

    addEvent(eventData) {
        const newEvent = {
            id: this.events.length + 1,
            ...eventData,
            createdAt: new Date().toISOString()
        };
        this.events.push(newEvent);
        return newEvent;
    }

    getUpcomingEvents(days = 30) {
        const now = new Date();
        const futureDate = new Date(now.getTime() + (days * 24 * 60 * 60 * 1000));
        
        return this.events.filter(event => {
            const eventDate = new Date(event.date);
            return eventDate >= now && eventDate <= futureDate;
        }).sort((a, b) => new Date(a.date) - new Date(b.date));
    }

    getEventsByType(type) {
        return this.events.filter(event => event.type === type);
    }
}

// Notification System
class NotificationSystem {
    constructor() {
        this.notifications = [];
        this.subscribers = {
            all: [],
            leaders: [],
            youth: [],
            women: []
        };
    }

    addSubscriber(group, contact) {
        if (this.subscribers[group]) {
            this.subscribers[group].push(contact);
        }
    }

    sendNotification(title, content, recipients, priority = 'normal') {
        const notification = {
            id: Date.now(),
            title,
            content,
            recipients,
            priority,
            sentAt: new Date().toISOString(),
            readBy: []
        };

        this.notifications.push(notification);
        
        // Simulate sending notification
        console.log('Gửi thông báo:', notification);
        
        return notification;
    }

    getNotifications(limit = 10) {
        return this.notifications
            .sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt))
            .slice(0, limit);
    }
}

// Member Management System
class MemberManagement {
    constructor() {
        this.members = [];
        this.families = [];
    }

    addMember(memberData) {
        const newMember = {
            id: this.members.length + 1,
            ...memberData,
            joinDate: new Date().toISOString(),
            status: 'active'
        };
        
        this.members.push(newMember);
        return newMember;
    }

    searchMembers(query) {
        return this.members.filter(member => 
            member.fullName.toLowerCase().includes(query.toLowerCase()) ||
            member.phone?.includes(query) ||
            member.address?.toLowerCase().includes(query.toLowerCase())
        );
    }

    getMembersByLocation(location) {
        return this.members.filter(member => member.location === location);
    }

    getStatistics() {
        return {
            total: this.members.length,
            active: this.members.filter(m => m.status === 'active').length,
            inactive: this.members.filter(m => m.status === 'inactive').length,
            newThisMonth: this.members.filter(m => {
                const joinDate = new Date(m.joinDate);
                const now = new Date();
                return joinDate.getMonth() === now.getMonth() && 
                       joinDate.getFullYear() === now.getFullYear();
            }).length
        };
    }
}

// Initialize additional systems
window.addEventListener('DOMContentLoaded', () => {
    window.eventCalendar = new EventCalendar();
    window.notificationSystem = new NotificationSystem();
    window.memberManagement = new MemberManagement();
    
    console.log('Các hệ thống bổ sung đã được khởi tạo!');
});

// Export functions for Excel reports
function exportToExcel(data, filename) {
    // Simple CSV export (can be enhanced with proper Excel library)
    let csvContent = "data:text/csv;charset=utf-8,";
    
    if (Array.isArray(data) && data.length > 0) {
        // Add headers
        const headers = Object.keys(data[0]);
        csvContent += headers.join(",") + "\n";
        
        // Add data rows
        data.forEach(row => {
            const values = headers.map(header => row[header] || '');
            csvContent += values.join(",") + "\n";
        });
    }
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", filename + ".csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// Enhanced search functionality
function searchDashboard(query) {
    const searchResults = [];
    
    // Search in members
    if (window.memberManagement) {
        const memberResults = window.memberManagement.searchMembers(query);
        searchResults.push(...memberResults.map(m => ({
            type: 'member',
            title: m.fullName,
            description: m.phone || m.address,
            data: m
        })));
    }
    
    // Search in events
    if (window.eventCalendar) {
        const eventResults = window.eventCalendar.events.filter(e => 
            e.title.toLowerCase().includes(query.toLowerCase()) ||
            e.location.toLowerCase().includes(query.toLowerCase())
        );
        searchResults.push(...eventResults.map(e => ({
            type: 'event',
            title: e.title,
            description: `${e.date} - ${e.location}`,
            data: e
        })));
    }
    
    return searchResults;
}

// Print functionality
function printReport(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <html>
                <head>
                    <title>Báo cáo - HTTL Kông Brech</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .content { margin: 20px 0; }
                        table { width: 100%; border-collapse: collapse; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #f2f2f2; }
                        @media print { .no-print { display: none; } }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>HỘI THÁNH TIN LÀNH KÔNG BRECH</h1>
                        <p>Báo cáo được tạo ngày: ${new Date().toLocaleDateString('vi-VN')}</p>
                    </div>
                    <div class="content">
                        ${element.innerHTML}
                    </div>
                </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }
}// S
earch functionality
function performSearch() {
    const query = document.getElementById('dashboardSearch').value.trim();
    if (query.length < 2) {
        hideSearchResults();
        return;
    }
    
    const results = searchDashboard(query);
    displaySearchResults(results);
}

function displaySearchResults(results) {
    let resultsContainer = document.getElementById('searchResults');
    
    if (!resultsContainer) {
        resultsContainer = document.createElement('div');
        resultsContainer.id = 'searchResults';
        resultsContainer.className = 'search-results';
        document.querySelector('.search-container').appendChild(resultsContainer);
    }
    
    if (results.length === 0) {
        resultsContainer.innerHTML = '<div class="search-result-item">Không tìm thấy kết quả nào</div>';
    } else {
        resultsContainer.innerHTML = results.map(result => `
            <div class="search-result-item" onclick="selectSearchResult('${result.type}', ${JSON.stringify(result.data).replace(/"/g, '&quot;')})">
                <div class="search-result-title">${result.title}</div>
                <div class="search-result-description">${result.description}</div>
                <span class="search-result-type ${result.type}">${result.type === 'member' ? 'Tín hữu' : 'Sự kiện'}</span>
            </div>
        `).join('');
    }
    
    resultsContainer.style.display = 'block';
}

function hideSearchResults() {
    const resultsContainer = document.getElementById('searchResults');
    if (resultsContainer) {
        resultsContainer.style.display = 'none';
    }
}

function selectSearchResult(type, data) {
    console.log('Selected:', type, data);
    hideSearchResults();
    
    if (type === 'member') {
        window.churchManagement.showNotification(`Đã chọn tín hữu: ${data.fullName}`, 'info');
    } else if (type === 'event') {
        window.churchManagement.showNotification(`Đã chọn sự kiện: ${data.title}`, 'info');
    }
}

// Quick tools functions
function exportAllData() {
    window.churchManagement.showNotification('Đang xuất tất cả dữ liệu...', 'info');
    
    setTimeout(() => {
        // Simulate data export
        const allData = {
            members: window.memberManagement ? window.memberManagement.members : [],
            events: window.eventCalendar ? window.eventCalendar.events : [],
            notifications: window.notificationSystem ? window.notificationSystem.notifications : []
        };
        
        exportToExcel([
            { type: 'Tín hữu', count: allData.members.length },
            { type: 'Sự kiện', count: allData.events.length },
            { type: 'Thông báo', count: allData.notifications.length }
        ], 'bao-cao-tong-hop-' + new Date().toISOString().split('T')[0]);
        
        window.churchManagement.showNotification('Đã xuất dữ liệu thành công!', 'success');
    }, 2000);
}

function printDashboard() {
    window.print();
}

function refreshDashboard() {
    const indicator = document.querySelector('.refresh-indicator');
    if (!indicator) {
        const newIndicator = document.createElement('div');
        newIndicator.className = 'refresh-indicator';
        newIndicator.innerHTML = '<i class="fas fa-sync-alt"></i>';
        document.querySelector('.dashboard-overview h2').appendChild(newIndicator);
    }
    
    document.querySelector('.refresh-indicator').classList.add('active');
    
    window.churchManagement.showNotification('Đang làm mới dữ liệu...', 'info');
    
    setTimeout(() => {
        window.churchManagement.loadDashboardData();
        document.querySelector('.refresh-indicator').classList.remove('active');
        window.churchManagement.showNotification('Đã cập nhật dữ liệu!', 'success');
    }, 2000);
}

function openSettings() {
    const settingsContent = {
        title: 'Cài đặt hệ thống',
        body: `
            <div class="settings-container">
                <div class="setting-group">
                    <h4>Cài đặt chung</h4>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" checked> Tự động làm mới dữ liệu
                        </label>
                    </div>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" checked> Hiển thị thông báo
                        </label>
                    </div>
                    <div class="form-group">
                        <label>Thời gian làm mới (phút):</label>
                        <select>
                            <option value="1">1 phút</option>
                            <option value="5" selected>5 phút</option>
                            <option value="10">10 phút</option>
                            <option value="30">30 phút</option>
                        </select>
                    </div>
                </div>
                
                <div class="setting-group">
                    <h4>Cài đặt báo cáo</h4>
                    <div class="form-group">
                        <label>Định dạng xuất file:</label>
                        <select>
                            <option value="csv">CSV</option>
                            <option value="excel">Excel</option>
                            <option value="pdf">PDF</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" checked> Bao gồm biểu đồ trong báo cáo
                        </label>
                    </div>
                </div>
                
                <div class="setting-group">
                    <h4>Cài đặt thông báo</h4>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" checked> Email thông báo
                        </label>
                    </div>
                    <div class="form-group">
                        <label>
                            <input type="checkbox"> SMS thông báo
                        </label>
                    </div>
                </div>
            </div>
        `
    };
    
    window.churchManagement.showModal(settingsContent);
}

// Enhanced search with real-time results
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('dashboardSearch');
    if (searchInput) {
        let searchTimeout;
        
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                performSearch();
            }, 300);
        });
        
        searchInput.addEventListener('blur', () => {
            setTimeout(hideSearchResults, 200);
        });
        
        searchInput.addEventListener('focus', () => {
            if (searchInput.value.trim().length >= 2) {
                performSearch();
            }
        });
    }
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    // Ctrl + F for search
    if (e.ctrlKey && e.key === 'f') {
        e.preventDefault();
        const searchInput = document.getElementById('dashboardSearch');
        if (searchInput) {
            searchInput.focus();
        }
    }
    
    // Ctrl + R for refresh
    if (e.ctrlKey && e.key === 'r') {
        e.preventDefault();
        refreshDashboard();
    }
    
    // Ctrl + P for print
    if (e.ctrlKey && e.key === 'p') {
        e.preventDefault();
        printDashboard();
    }
    
    // Escape to close modal
    if (e.key === 'Escape') {
        window.churchManagement.closeModal();
        hideSearchResults();
    }
});

// Auto-save functionality for forms
function setupAutoSave() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('change', () => {
                const formData = new FormData(form);
                localStorage.setItem(`form_${form.id}`, JSON.stringify(Object.fromEntries(formData)));
            });
        });
    });
}

// Load saved form data
function loadSavedFormData(formId) {
    const savedData = localStorage.getItem(`form_${formId}`);
    if (savedData) {
        const data = JSON.parse(savedData);
        const form = document.getElementById(formId);
        if (form) {
            Object.keys(data).forEach(key => {
                const input = form.querySelector(`[name="${key}"]`);
                if (input) {
                    input.value = data[key];
                }
            });
        }
    }
}

// Clear saved form data
function clearSavedFormData(formId) {
    localStorage.removeItem(`form_${formId}`);
}

console.log('Các tính năng bổ sung đã được tải!');