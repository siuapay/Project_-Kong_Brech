/**
 * Searchable Dropdown Component
 * Tạo dropdown có thể tìm kiếm cho việc chọn điểm nhóm
 */
class SearchableDropdown {
    constructor(container, options = {}) {
        // Accept either element or string ID
        if (typeof container === 'string') {
            this.container = document.getElementById(container);
        } else {
            this.container = container;
        }
        
        // Check if container exists
        if (!this.container) {
            console.error('SearchableDropdown: Container not found');
            return;
        }
        
        this.options = {
            placeholder: options.placeholder || 'Tìm kiếm và chọn...',
            searchPlaceholder: options.searchPlaceholder || 'Nhập để tìm kiếm...',
            noResultsText: options.noResultsText || 'Không tìm thấy kết quả',
            apiUrl: options.apiUrl || '',
            onSelect: options.onSelect || function() {},
            displayField: options.displayField || 'name',
            valueField: options.valueField || 'id',
            data: options.data || [],
            ...options
        };
        
        this.data = this.options.data || [];
        this.filteredData = [];
        this.selectedItem = null;
        this.isOpen = false;
        
        this.init();
    }
    
    init() {
        this.createHTML();
        this.bindEvents();
        if (this.options.apiUrl) {
            this.loadData();
        }
    }
    
    createHTML() {
        if (!this.container) {
            console.error('SearchableDropdown: Cannot create HTML - container is null');
            return;
        }
        
        this.container.innerHTML = `
            <div class="searchable-dropdown">
                <div class="dropdown-input-container">
                    <input type="text" 
                           class="dropdown-input form-control" 
                           placeholder="${this.options.placeholder}"
                           readonly>
                    <i class="dropdown-arrow fas fa-chevron-down"></i>
                </div>
                <div class="dropdown-menu-custom">
                    <div class="dropdown-search-container">
                        <input type="text" 
                               class="dropdown-search form-control form-control-sm" 
                               placeholder="${this.options.searchPlaceholder}">
                    </div>
                    <div class="dropdown-results">
                        <div class="dropdown-loading">
                            <i class="fas fa-spinner fa-spin"></i> Đang tải...
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Get elements
        this.inputElement = this.container.querySelector('.dropdown-input');
        this.searchElement = this.container.querySelector('.dropdown-search');
        this.menuElement = this.container.querySelector('.dropdown-menu-custom');
        this.resultsElement = this.container.querySelector('.dropdown-results');
        this.arrowElement = this.container.querySelector('.dropdown-arrow');
    }
    
    bindEvents() {
        // Toggle dropdown
        this.inputElement.addEventListener('click', () => {
            this.toggle();
        });
        
        // Search functionality
        this.searchElement.addEventListener('input', (e) => {
            this.search(e.target.value);
        });
        
        // Close on outside click
        document.addEventListener('click', (e) => {
            if (!this.container.contains(e.target)) {
                this.close();
            }
        });
        
        // Prevent menu close when clicking inside
        this.menuElement.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }
    
    async loadData() {
        try {
            const response = await fetch(this.options.apiUrl);
            this.data = await response.json();
            this.filteredData = [...this.data];
            this.renderResults();
        } catch (error) {
            console.error('Error loading data:', error);
            this.showError('Lỗi khi tải dữ liệu');
        }
    }
    
    setData(data) {
        this.data = data;
        this.hiddenIds = []; // Reset hidden items
        this.filteredData = [...data];
        this.renderResults();
    }
    
    search(query) {
        let baseData = this.data;
        
        // Filter out hidden items first
        if (this.hiddenIds && this.hiddenIds.length > 0) {
            baseData = this.data.filter(item => !this.hiddenIds.includes(item[this.options.valueField]));
        }
        
        if (!query.trim()) {
            this.filteredData = [...baseData];
        } else {
            const searchTerm = query.toLowerCase();
            this.filteredData = baseData.filter(item => {
                const displayValue = item[this.options.displayField];
                return displayValue && displayValue.toLowerCase().includes(searchTerm);
            });
        }
        this.renderResults();
    }
    
    renderResults() {
        if (this.filteredData.length === 0) {
            this.resultsElement.innerHTML = `
                <div class="dropdown-no-results">
                    <i class="fas fa-search"></i>
                    ${this.options.noResultsText}
                </div>
            `;
            return;
        }
        
        const html = this.filteredData.map(item => `
            <div class="dropdown-item" data-value="${item[this.options.valueField]}">
                <div class="item-main">
                    <strong>${item[this.options.displayField]}</strong>
                </div>
                ${item.banNganh ? `
                    <div class="item-sub">
                        <i class="fas fa-building text-muted"></i>
                        ${item.banNganh.tenBan}
                    </div>
                ` : ''}
                ${item.diaChi ? `
                    <div class="item-sub">
                        <i class="fas fa-map-marker-alt text-muted"></i>
                        ${item.diaChi}
                    </div>
                ` : ''}
            </div>
        `).join('');
        
        this.resultsElement.innerHTML = html;
        
        // Bind click events to items
        this.resultsElement.querySelectorAll('.dropdown-item').forEach(item => {
            item.addEventListener('click', () => {
                const value = item.dataset.value;
                const selectedData = this.filteredData.find(d => d[this.options.valueField] == value);
                this.select(selectedData);
            });
        });
    }
    
    select(item) {
        this.selectedItem = item;
        this.inputElement.value = item[this.options.displayField];
        this.close();
        this.options.onSelect(item);
    }
    
    toggle() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    }
    
    open() {
        this.isOpen = true;
        this.menuElement.style.display = 'block';
        this.arrowElement.classList.add('rotated');
        this.searchElement.focus();
        
        // Reset search but respect hidden items
        this.searchElement.value = '';
        
        // Apply filter to respect hidden items
        let baseData = this.data;
        if (this.hiddenIds && this.hiddenIds.length > 0) {
            baseData = this.data.filter(item => !this.hiddenIds.includes(item[this.options.valueField]));
        }
        this.filteredData = [...baseData];
        
        this.renderResults();
    }
    
    close() {
        this.isOpen = false;
        this.menuElement.style.display = 'none';
        this.arrowElement.classList.remove('rotated');
    }
    
    showError(message) {
        this.resultsElement.innerHTML = `
            <div class="dropdown-error">
                <i class="fas fa-exclamation-triangle text-danger"></i>
                ${message}
            </div>
        `;
    }
    
    getValue() {
        return this.selectedItem ? this.selectedItem[this.options.valueField] : null;
    }
    
    setValue(value) {
        const item = this.data.find(d => d[this.options.valueField] == value);
        if (item) {
            this.select(item);
        }
    }
    
    setData(data) {
        this.data = data || [];
        this.filteredData = [...this.data];
        if (this.isOpen) {
            this.renderResults();
        }
    }
    
    clear() {
        this.selectedItem = null;
        this.inputElement.value = '';
    }
    
    getData() {
        return this.data;
    }
    
    // Filter items by hiding selected ones
    filterItems(hiddenIds = []) {
        this.hiddenIds = hiddenIds;
        this.filteredData = this.data.filter(item => !hiddenIds.includes(item[this.options.valueField]));
        this.renderResults();
    }
    
    // Show/hide specific item
    hideItem(id) {
        if (!this.hiddenIds) this.hiddenIds = [];
        if (!this.hiddenIds.includes(id)) {
            this.hiddenIds.push(id);
            this.filterItems(this.hiddenIds);
        }
    }
    
    showItem(id) {
        if (!this.hiddenIds) this.hiddenIds = [];
        this.hiddenIds = this.hiddenIds.filter(hiddenId => hiddenId !== id);
        this.filterItems(this.hiddenIds);
    }
    
    // Get item by ID
    getItemById(id) {
        return this.data.find(item => item[this.options.valueField] == id);
    }
}