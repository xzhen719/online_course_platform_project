package com.ocp.backend.dto.request;

import lombok.Data;

@Data
public class ChapterRequest {
    private String title;
    private String summary;
    private String description;
    private Integer duration; // In minutes
    private String videoUrl;
}