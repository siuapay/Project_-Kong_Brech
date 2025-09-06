// Mobile Navigation Toggle
const hamburger = document.querySelector('.hamburger');
const navMenu = document.querySelector('.nav-menu');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');
});

// Close mobile menu when clicking on a link
document.querySelectorAll('.nav-menu a').forEach(link => {
    link.addEventListener('click', () => {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');
    });
});

// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Video play functionality
document.querySelectorAll('.video-thumbnail').forEach(thumbnail => {
    thumbnail.addEventListener('click', function() {
        // Tạo modal để phát video
        const modal = document.createElement('div');
        modal.className = 'video-modal';
        modal.innerHTML = `
            <div class="video-modal-content">
                <span class="close-modal">&times;</span>
                <video controls autoplay>
                    <source src="sample-video.mp4" type="video/mp4">
                    <p>Trình duyệt của bạn không hỗ trợ video HTML5.</p>
                </video>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Đóng modal
        modal.querySelector('.close-modal').addEventListener('click', () => {
            document.body.removeChild(modal);
        });
        
        // Đóng modal khi click bên ngoài
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                document.body.removeChild(modal);
            }
        });
    });
});

// Lazy loading cho hình ảnh
const images = document.querySelectorAll('img');
const imageObserver = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const img = entry.target;
            img.src = img.dataset.src || img.src;
            img.classList.remove('lazy');
            observer.unobserve(img);
        }
    });
});

images.forEach(img => {
    imageObserver.observe(img);
});

// Thêm hiệu ứng fade-in khi scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Áp dụng hiệu ứng cho các card
document.querySelectorAll('.news-card, .video-card').forEach(card => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(20px)';
    card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
    observer.observe(card);
});

// Tìm kiếm tin tức (có thể mở rộng)
function searchNews(query) {
    const newsCards = document.querySelectorAll('.news-card');
    const searchTerm = query.toLowerCase();
    
    newsCards.forEach(card => {
        const title = card.querySelector('h3').textContent.toLowerCase();
        const content = card.querySelector('p').textContent.toLowerCase();
        
        if (title.includes(searchTerm) || content.includes(searchTerm)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// Thêm chức năng chia sẻ
function shareArticle(title, url) {
    if (navigator.share) {
        navigator.share({
            title: title,
            url: url
        });
    } else {
        // Fallback cho trình duyệt không hỗ trợ Web Share API
        const shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`;
        window.open(shareUrl, '_blank');
    }
}

// Thêm nút back to top
const backToTopButton = document.createElement('button');
backToTopButton.innerHTML = '<i class="fas fa-arrow-up"></i>';
backToTopButton.className = 'back-to-top';
backToTopButton.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    background: #667eea;
    color: white;
    border: none;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    cursor: pointer;
    display: none;
    z-index: 1000;
    transition: all 0.3s ease;
`;

document.body.appendChild(backToTopButton);

// Hiển thị/ẩn nút back to top
window.addEventListener('scroll', () => {
    if (window.pageYOffset > 300) {
        backToTopButton.style.display = 'block';
    } else {
        backToTopButton.style.display = 'none';
    }
});

// Xử lý click nút back to top
backToTopButton.addEventListener('click', () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
});

// API Integration
const API_BASE = 'http://localhost:3000/api';

// Load articles from database
async function loadArticlesFromDB() {
    try {
        const response = await fetch(`${API_BASE}/articles?limit=20`);
        const articles = await response.json();
        
        if (articles.length > 0) {
            updateNewsGrid(articles);
        }
    } catch (error) {
        console.error('Lỗi tải bài viết:', error);
    }
}

// Update news grid with real data
function updateNewsGrid(articles) {
    const newsGrid = document.querySelector('.news-grid');
    
    // Clear existing content except featured article
    const featuredCard = newsGrid.querySelector('.news-card.featured');
    newsGrid.innerHTML = '';
    
    // Add featured article if exists
    if (featuredCard && articles.length > 0) {
        updateFeaturedArticle(articles[0]);
        newsGrid.appendChild(featuredCard);
    }
    
    // Add other articles
    articles.slice(1).forEach(article => {
        const articleCard = createArticleCard(article);
        newsGrid.appendChild(articleCard);
    });
}

// Update featured article
function updateFeaturedArticle(article) {
    const featuredCard = document.querySelector('.news-card.featured');
    if (featuredCard) {
        const img = featuredCard.querySelector('img');
        const category = featuredCard.querySelector('.category');
        const title = featuredCard.querySelector('h3');
        const excerpt = featuredCard.querySelector('p');
        const timeSpan = featuredCard.querySelector('.news-meta span:first-child');
        const authorSpan = featuredCard.querySelector('.news-meta span:last-child');
        
        if (article.featured_image) {
            img.src = `/uploads/${article.featured_image}`;
        }
        category.textContent = article.category_name || 'Tin tức';
        title.textContent = article.title;
        excerpt.textContent = article.excerpt || article.content.substring(0, 150) + '...';
        timeSpan.innerHTML = `<i class="fas fa-clock"></i> ${formatTimeAgo(article.created_at)}`;
        authorSpan.innerHTML = `<i class="fas fa-user"></i> ${article.author}`;
        
        // Add click handler
        featuredCard.style.cursor = 'pointer';
        featuredCard.onclick = () => openArticle(article.id);
    }
}

// Create article card
function createArticleCard(article) {
    const card = document.createElement('article');
    card.className = 'news-card';
    card.style.cursor = 'pointer';
    
    const imageUrl = article.featured_image ? `/uploads/${article.featured_image}` : 'https://via.placeholder.com/400x200';
    
    card.innerHTML = `
        <img src="${imageUrl}" alt="${article.title}">
        <div class="news-content">
            <span class="category">${article.category_name || 'Tin tức'}</span>
            <h3>${article.title}</h3>
            <p>${article.excerpt || article.content.substring(0, 100) + '...'}</p>
            <div class="news-meta">
                <span><i class="fas fa-clock"></i> ${formatTimeAgo(article.created_at)}</span>
                <span><i class="fas fa-eye"></i> ${article.views}</span>
            </div>
        </div>
    `;
    
    card.onclick = () => openArticle(article.id);
    
    return card;
}

// Format time ago
function formatTimeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const diffInHours = Math.floor((now - date) / (1000 * 60 * 60));
    
    if (diffInHours < 1) {
        return 'Vừa xong';
    } else if (diffInHours < 24) {
        return `${diffInHours} giờ trước`;
    } else {
        const diffInDays = Math.floor(diffInHours / 24);
        return `${diffInDays} ngày trước`;
    }
}

// Open article in modal or new page
async function openArticle(articleId) {
    try {
        const response = await fetch(`${API_BASE}/articles/${articleId}`);
        const article = await response.json();
        
        // Create article modal
        const modal = document.createElement('div');
        modal.className = 'article-modal';
        modal.innerHTML = `
            <div class="article-modal-content">
                <span class="close-modal">&times;</span>
                <div class="article-header">
                    <span class="category">${article.category_name || 'Tin tức'}</span>
                    <h1>${article.title}</h1>
                    <div class="article-meta">
                        <span><i class="fas fa-user"></i> ${article.author}</span>
                        <span><i class="fas fa-clock"></i> ${formatTimeAgo(article.created_at)}</span>
                        <span><i class="fas fa-eye"></i> ${article.views} lượt xem</span>
                    </div>
                </div>
                ${article.featured_image ? `<img src="/uploads/${article.featured_image}" alt="${article.title}" class="article-image">` : ''}
                <div class="article-content">
                    ${article.content.replace(/\n/g, '<br>')}
                </div>
                ${article.video_url ? `
                    <div class="article-video">
                        <video controls>
                            <source src="/uploads/${article.video_url}" type="video/mp4">
                            Trình duyệt không hỗ trợ video.
                        </video>
                    </div>
                ` : ''}
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Close modal handlers
        modal.querySelector('.close-modal').onclick = () => document.body.removeChild(modal);
        modal.onclick = (e) => {
            if (e.target === modal) document.body.removeChild(modal);
        };
        
    } catch (error) {
        console.error('Lỗi tải bài viết:', error);
        alert('Không thể tải bài viết');
    }
}

// Load categories for navigation
async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE}/categories`);
        const categories = await response.json();
        
        // Update navigation menu with categories
        const navMenu = document.querySelector('.nav-menu');
        categories.forEach(category => {
            const li = document.createElement('li');
            li.innerHTML = `<a href="#" onclick="filterByCategory('${category.slug}')">${category.name}</a>`;
            navMenu.appendChild(li);
        });
    } catch (error) {
        console.error('Lỗi tải danh mục:', error);
    }
}

// Filter articles by category
async function filterByCategory(categorySlug) {
    try {
        const response = await fetch(`${API_BASE}/articles?category=${categorySlug}&limit=20`);
        const articles = await response.json();
        updateNewsGrid(articles);
    } catch (error) {
        console.error('Lỗi lọc bài viết:', error);
    }
}

// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const categoryFilter = document.getElementById('category-filter');
    
    // Load initial data
    loadArticlesFromDB();
    loadCategoriesForFilter();
    
    // Search event listeners
    if (searchBtn) {
        searchBtn.addEventListener('click', performSearch);
    }
    
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
    
    if (categoryFilter) {
        categoryFilter.addEventListener('change', function() {
            if (this.value) {
                filterByCategory(this.value);
            } else {
                loadArticlesFromDB();
            }
        });
    }
});

// Load categories for filter dropdown
async function loadCategoriesForFilter() {
    try {
        const response = await fetch(`${API_BASE}/categories`);
        const categories = await response.json();
        
        const categoryFilter = document.getElementById('category-filter');
        if (categoryFilter) {
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.slug;
                option.textContent = category.name;
                categoryFilter.appendChild(option);
            });
        }
        
        // Also update navigation menu
        const navMenu = document.querySelector('.nav-menu');
        const existingCategoryLinks = navMenu.querySelectorAll('.category-link');
        existingCategoryLinks.forEach(link => link.remove());
        
        categories.forEach(category => {
            const li = document.createElement('li');
            li.innerHTML = `<a href="#" class="category-link" onclick="filterByCategory('${category.slug}')">${category.name}</a>`;
            // Insert before admin link
            const adminLink = navMenu.querySelector('.admin-link').parentElement;
            navMenu.insertBefore(li, adminLink);
        });
    } catch (error) {
        console.error('Lỗi tải danh mục:', error);
    }
}

// Perform search
async function performSearch() {
    const searchInput = document.getElementById('search-input');
    const categoryFilter = document.getElementById('category-filter');
    
    const query = searchInput ? searchInput.value.trim() : '';
    const category = categoryFilter ? categoryFilter.value : '';
    
    if (!query) {
        alert('Vui lòng nhập từ khóa tìm kiếm');
        return;
    }
    
    try {
        let url = `${API_BASE}/search?q=${encodeURIComponent(query)}`;
        if (category) {
            url += `&category=${category}`;
        }
        
        const response = await fetch(url);
        const articles = await response.json();
        
        if (articles.length > 0) {
            updateNewsGrid(articles);
            
            // Show search results message
            const mainContent = document.querySelector('.main-content');
            let searchMessage = mainContent.querySelector('.search-results-message');
            if (!searchMessage) {
                searchMessage = document.createElement('div');
                searchMessage.className = 'search-results-message';
                mainContent.insertBefore(searchMessage, mainContent.firstChild);
            }
            searchMessage.innerHTML = `
                <div style="background: #d4edda; color: #155724; padding: 1rem; border-radius: 8px; margin-bottom: 2rem;">
                    <i class="fas fa-search"></i> Tìm thấy ${articles.length} kết quả cho "${query}"
                    ${category ? ` trong danh mục "${categoryFilter.options[categoryFilter.selectedIndex].text}"` : ''}
                    <button onclick="clearSearch()" style="float: right; background: none; border: none; color: #155724; cursor: pointer;">
                        <i class="fas fa-times"></i> Xóa bộ lọc
                    </button>
                </div>
            `;
        } else {
            // Show no results message
            const newsGrid = document.querySelector('.news-grid');
            newsGrid.innerHTML = `
                <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: white; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1);">
                    <i class="fas fa-search" style="font-size: 3rem; color: #ccc; margin-bottom: 1rem;"></i>
                    <h3>Không tìm thấy kết quả</h3>
                    <p>Không có bài viết nào phù hợp với từ khóa "${query}"</p>
                    <button onclick="clearSearch()" class="btn btn-primary" style="margin-top: 1rem;">
                        <i class="fas fa-arrow-left"></i> Quay lại trang chủ
                    </button>
                </div>
            `;
        }
    } catch (error) {
        console.error('Lỗi tìm kiếm:', error);
        alert('Có lỗi xảy ra khi tìm kiếm');
    }
}

// Clear search and reload all articles
function clearSearch() {
    const searchInput = document.getElementById('search-input');
    const categoryFilter = document.getElementById('category-filter');
    
    if (searchInput) searchInput.value = '';
    if (categoryFilter) categoryFilter.value = '';
    
    // Remove search results message
    const searchMessage = document.querySelector('.search-results-message');
    if (searchMessage) {
        searchMessage.remove();
    }
    
    loadArticlesFromDB();
}

console.log('Website tin tức đã được tải thành công!');