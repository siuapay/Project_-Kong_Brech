// package com.branch.demo.domain;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "test_entity")
// public class TestEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(name = "name", length = 255)
//     private String name;
    
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;
    
//     @PrePersist
//     protected void onCreate() {
//         createdAt = LocalDateTime.now();
//     }
    
//     // Constructors
//     public TestEntity() {}
    
//     public TestEntity(String name) {
//         this.name = name;
//     }
    
//     // Getters and Setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
    
//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }
    
//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
// }