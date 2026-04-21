package com.ocp.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * CourseDto - 課程資料傳輸物件
 * 用於API回傳，避免直接回傳Entity導致的Hibernate proxy序列化問題
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String instructorName;
    private String instructorDesc;
    private Long instructorId;

    private Integer price;
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer studentCount;

    private Integer totalChapters;
    private Integer totalTime;

    private String categoryName;
    private Long categoryId;

    private String status;

    /**
     * 簡化版建構子 - 用於EnrollmentService
     * 保持向後相容
     */
    public CourseDto(Long id, String name, String description, String imageUrl,
            String instructorName, Integer price, Double ratingAverage,
            Integer studentCount, Integer totalChapters, Integer totalTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.instructorName = instructorName;
        this.price = price;
        this.ratingAverage = ratingAverage;
        this.studentCount = studentCount;
        this.totalChapters = totalChapters;
        this.totalTime = totalTime;
    }
}
