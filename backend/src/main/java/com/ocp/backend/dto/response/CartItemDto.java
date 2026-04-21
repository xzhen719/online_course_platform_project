package com.ocp.backend.dto.response;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private Long courseId;
    private String courseName;
    private Integer price;
    private String imageUrl;
}
