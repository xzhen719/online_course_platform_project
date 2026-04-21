package com.ocp.backend.dto.request;

import lombok.Data;

@Data
public class CourseRequest {
    private String name;
    private String description;
    private String instructorName;
    private Integer price;
    private Long categoryId;
    private String imageUrl;
    private String videoUrl;
}
