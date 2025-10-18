/**
 * Toast Notification System
 * Hệ thống thông báo toast hiện đại và đẹp mắt
 */
class ToastNotification {
    constructor() {
        this.container = null;
        this.toasts = new Map();
        this.init();
    }

    init() {
        // Tạo container cho toast nếu chưa có
        if (!document.querySelector('.toast-container')) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.querySelector('.toast-container');
        }
    }

    /**
     * Hiển thị toast notification
     * @param {string} type - Loại toast: success, error, warning, info
     * @param {string} title - Tiêu đề toast
     * @param {string} message - Nội dung toast
     * @param {number} duration - Thời gian hiển thị (ms), 0 = không tự động ẩn
     */
    show(type = 'info', title = '', message = '', duration = 5000) {
        const toastId = 'toast_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        
        const toast = this.createToastElement(toastId, type, title, message, duration);
        
        // Thêm vào container
        this.container.appendChild(toast);
        this.toasts.set(toastId, toast);

        // Tự động ẩn sau duration (nếu duration > 0)
        if (duration > 0) {
            setTimeout(() => {
                this.hide(toastId);
            }, duration);
        }

        return toastId;
    }

    /**
     * Tạo element toast
     */
    createToastElement(id, type, title, message, duration) {
        const toast = document.createElement('div');
        toast.id = id;
        toast.className = `toast-notification toast-${type}`;

        const icon = this.getIcon(type);
        
        toast.innerHTML = `
            <div class="toast-icon">
                <i class="${icon}"></i>
            </div>
            <div class="toast-content">
                ${title ? `<div class="toast-title">${this.escapeHtml(title)}</div>` : ''}
                ${message ? `<div class="toast-message">${this.escapeHtml(message)}</div>` : ''}
            </div>
            <button class="toast-close" type="button" aria-label="Đóng">
                <i class="fas fa-times"></i>
            </button>
            ${duration > 0 ? '<div class="toast-progress"></div>' : ''}
        `;

        // Thêm event listener cho nút đóng
        const closeBtn = toast.querySelector('.toast-close');
        closeBtn.addEventListener('click', () => {
            this.hide(id);
        });

        // Thêm event listener cho click vào toast (tùy chọn)
        toast.addEventListener('click', (e) => {
            if (!e.target.closest('.toast-close')) {
                // Có thể thêm logic xử lý khi click vào toast
            }
        });

        return toast;
    }

    /**
     * Ẩn toast
     */
    hide(toastId) {
        const toast = this.toasts.get(toastId);
        if (toast) {
            toast.classList.add('hiding');
            
            // Xóa sau khi animation hoàn thành
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
                this.toasts.delete(toastId);
            }, 300);
        }
    }

    /**
     * Ẩn tất cả toast
     */
    hideAll() {
        this.toasts.forEach((toast, id) => {
            this.hide(id);
        });
    }

    /**
     * Lấy icon theo loại toast
     */
    getIcon(type) {
        const icons = {
            success: 'fas fa-check',
            error: 'fas fa-times',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info'
        };
        return icons[type] || icons.info;
    }

    /**
     * Escape HTML để tránh XSS
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * Các phương thức tiện ích
     */
    success(title, message, duration = 5000) {
        return this.show('success', title, message, duration);
    }

    error(title, message, duration = 7000) {
        return this.show('error', title, message, duration);
    }

    warning(title, message, duration = 6000) {
        return this.show('warning', title, message, duration);
    }

    info(title, message, duration = 5000) {
        return this.show('info', title, message, duration);
    }
}

// Tạo instance global
window.Toast = new ToastNotification();

// Tự động hiển thị toast từ server-side messages
document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra các thông báo từ server
    const alerts = document.querySelectorAll('.alert[data-toast="true"]');
    alerts.forEach(alert => {
        const type = alert.classList.contains('alert-success') ? 'success' :
                    alert.classList.contains('alert-danger') ? 'error' :
                    alert.classList.contains('alert-warning') ? 'warning' : 'info';
        
        const title = alert.getAttribute('data-title') || '';
        const message = alert.textContent.trim();
        
        // Hiển thị toast
        Toast.show(type, title, message);
        
        // Ẩn alert gốc
        alert.style.display = 'none';
    });

    // Xử lý các thông báo flash từ URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.has('success')) {
        const successMsg = urlParams.get('success');
        if (successMsg && successMsg !== 'true') {
            Toast.success('Thành công', successMsg);
        }
    }
    
    // Không xử lý error từ URL parameters nữa vì đã có template xử lý
    // if (urlParams.has('error')) {
    //     const errorMsg = urlParams.get('message') || 'Có lỗi xảy ra';
    //     if (urlParams.get('error') === 'true') {
    //         Toast.error('Lỗi', errorMsg);
    //     }
    // }
    
    if (urlParams.has('warning')) {
        const warningMsg = urlParams.get('warning');
        if (warningMsg && warningMsg !== 'true') {
            Toast.warning('Cảnh báo', warningMsg);
        }
    }
    
    if (urlParams.has('info')) {
        const infoMsg = urlParams.get('info');
        if (infoMsg && infoMsg !== 'true') {
            Toast.info('Thông tin', infoMsg);
        }
    }
});

// Hỗ trợ cho các form AJAX
document.addEventListener('submit', function(e) {
    const form = e.target;
    if (form.hasAttribute('data-toast-submit')) {
        e.preventDefault();
        
        const formData = new FormData(form);
        const url = form.action || window.location.href;
        const method = form.method || 'POST';
        
        fetch(url, {
            method: method,
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                Toast.success(data.title || 'Thành công', data.message);
                if (data.redirect) {
                    setTimeout(() => {
                        window.location.href = data.redirect;
                    }, 1500);
                }
            } else {
                Toast.error(data.title || 'Lỗi', data.message || 'Có lỗi xảy ra');
            }
        })
        .catch(error => {
            Toast.error('Lỗi', 'Không thể kết nối đến server');
        });
    }
});

// Export cho sử dụng trong modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ToastNotification;
}