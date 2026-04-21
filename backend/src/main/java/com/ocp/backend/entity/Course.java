package com.ocp.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    private String imageUrl;
    private String videoUrl;

    @Column(name = "instructor_name") // Tells Hibernate to map this field to the 'instructor_name' column in the DB
    private String instructorName;

    @Column(name = "instructor_desc")
    private String instructorDesc;

    private Integer price;
    private Double ratingAverage;
    private Integer totalTime, totalChapters, ratingCount, studentCount = 0;

    // --- Status ---
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    // --- Dates ---
    private LocalDateTime submittedDate;

    @Column(name = "onShelf_date")
    private LocalDateTime onShelfDate;

        

    // Many courses can belong to One teacher
    @ManyToOne(fetch = FetchType.LAZY) // Many courses can belong to One teacher
    @JoinColumn(name = "user_id", nullable = false) // This maps to the foreign key column
    private User instructor; // tho fk is 'user_id', JPA needs to link to User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // --- Timestamps ---
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        // 防止null值
        if (this.totalTime == null)
            this.totalTime = 0;
        if (this.totalChapters == null)
            this.totalChapters = 0;
        if (this.ratingAverage == null)
            this.ratingAverage = 0.0;
        if (this.ratingCount == null)
            this.ratingCount = 0;
        if (this.studentCount == null)
            this.studentCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
