/**
 * Drag & Drop Image Upload Utility
 * Provides drag and drop functionality for image upload forms
 */

class DragDropUpload {
  constructor(options = {}) {
    this.dropZoneId = options.dropZoneId || 'avatarDropZone';
    this.previewWrapperId = options.previewWrapperId || 'avatarPreviewWrapper';
    this.previewImageId = options.previewImageId || 'avatarPreview';
    this.fileInputId = options.fileInputId || 'avatarFile';
    this.statusId = options.statusId || 'uploadStatus';
    this.maxSize = options.maxSize || 5 * 1024 * 1024; // 5MB default
    this.allowedTypes = options.allowedTypes || ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    this.onFileSelected = options.onFileSelected || null;

    this.init();
  }

  init() {
    const dropZone = document.getElementById(this.dropZoneId);
    const previewWrapper = document.getElementById(this.previewWrapperId);
    const preview = document.getElementById(this.previewImageId);

    if (!dropZone || !preview) {
      console.warn('Drag & Drop: Required elements not found');
      return;
    }

    // Store original image URL
    preview.setAttribute('data-original', preview.src);

    // Prevent default drag behaviors
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
      dropZone.addEventListener(eventName, this.preventDefaults, false);
      document.body.addEventListener(eventName, this.preventDefaults, false);
    });

    // Highlight drop zone when dragging over it
    ['dragenter', 'dragover'].forEach(eventName => {
      dropZone.addEventListener(eventName, () => this.highlight(dropZone, previewWrapper), false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
      dropZone.addEventListener(eventName, () => this.unhighlight(dropZone, previewWrapper), false);
    });

    // Handle dropped files
    dropZone.addEventListener('drop', (e) => this.handleDrop(e), false);
  }

  preventDefaults(e) {
    e.preventDefault();
    e.stopPropagation();
  }

  highlight(dropZone, previewWrapper) {
    dropZone.classList.add('drag-over');
    if (previewWrapper) {
      previewWrapper.classList.add('drag-over');
    }
  }

  unhighlight(dropZone, previewWrapper) {
    dropZone.classList.remove('drag-over');
    if (previewWrapper) {
      previewWrapper.classList.remove('drag-over');
    }
  }

  handleDrop(e) {
    const dt = e.dataTransfer;
    const files = dt.files;

    if (files.length > 0) {
      this.handleFile(files[0], null);
    }
  }

  handleFile(file, input) {
    const preview = document.getElementById(this.previewImageId);
    const status = document.getElementById(this.statusId);
    const fileInput = document.getElementById(this.fileInputId);

    if (!file) {
      // Reset to original if no file
      const originalSrc = preview.getAttribute('data-original') || '/static/images/default-avatar.svg';
      preview.src = originalSrc;
      if (status) status.classList.add('d-none');
      return false;
    }

    // Validate file size
    if (file.size > this.maxSize) {
      alert(`File quá lớn! Vui lòng chọn file nhỏ hơn ${this.maxSize / (1024 * 1024)}MB.`);
      if (input) input.value = '';
      return false;
    }

    // Validate file type
    if (!this.allowedTypes.includes(file.type)) {
      alert('Định dạng file không được hỗ trợ! Chỉ chấp nhận JPG, JPEG, PNG, GIF.');
      if (input) input.value = '';
      return false;
    }

    // Show preview
    const reader = new FileReader();
    reader.onload = (e) => {
      preview.src = e.target.result;
      if (status) status.classList.remove('d-none');
      
      // Call custom callback if provided
      if (this.onFileSelected) {
        this.onFileSelected(file, e.target.result);
      }
    };
    reader.readAsDataURL(file);

    // If file came from drag & drop, update the file input
    if (!input && fileInput) {
      const dataTransfer = new DataTransfer();
      dataTransfer.items.add(file);
      fileInput.files = dataTransfer.files;
    }

    return true;
  }

  // Public method to handle file from input change event
  handleFileInput(input) {
    const file = input.files[0];
    return this.handleFile(file, input);
  }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
  module.exports = DragDropUpload;
}
