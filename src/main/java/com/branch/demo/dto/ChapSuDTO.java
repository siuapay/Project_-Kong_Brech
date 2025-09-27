package com.branch.demo.dto;

public class ChapSuDTO {
      private Long id;
        private String hoTen;
        private String chucVu;
        private String dienThoai;
        private String email;
        private String avatarUrl;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        
        public String getChucVu() { return chucVu; }
        public void setChucVu(String chucVu) { this.chucVu = chucVu; }
        
        public String getDienThoai() { return dienThoai; }
        public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
