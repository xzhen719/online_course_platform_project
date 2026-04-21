package com.ocp.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "chapter_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Enumerated(EnumType.STRING)
    @Column
    private LearningStatus status;

    // 0-100 (Progress within the chapter/video)
    @Column(name = "progress_percent")
    private Integer progressPercent;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null)
            this.status = LearningStatus.NOT_STARTED;
        if (this.progressPercent == null)
            this.progressPercent = 0;
        this.startedAt = LocalDateTime.now();
    }
}
