package com.ocp.backend.entity;

import jakarta.persistence.*; // JPA 的標準註解
import lombok.Data; // Lombok 自動生成 Getter/Setter
import lombok.NoArgsConstructor; // 自動生成無參數建構子
import lombok.AllArgsConstructor; // 自動生成全參數建構子

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // Lombok
@NoArgsConstructor // JPA必須要有無參建構子
@AllArgsConstructor
public class User {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // 將Enum轉成字串存入DB
    @Column(nullable = false)
    private UserRole role;

    private String avatarUrl;

    private String sex;

    private LocalDate birthdate;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String resetToken;
    private LocalDateTime resetTokenExpiryDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 新增前獲取現在時間
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    // 更新前獲取現在時間
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
