package com.ocp.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDto {
    private Long id;
    private Long courseId; // For ownership verification
    private Integer index;
    private String title;
    private String description; // "descript" in DB
    private String summary;
    private String videoUrl; // Null if not authorized
}
