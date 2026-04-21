package com.ocp.backend.dto.response;

import lombok.Data;

@Data
public class FavoriteDto {
    private Long id; // Favorite ID
    private Long courseId; // Course ID for navigation
    private String courseName; // Course title for display
    private String courseImage; // Course thumbnail
    private Integer coursePrice; // Price for display
    private String instructorName; // Instructor name
}
