package com.ocp.backend.dto.response;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long courseId;
    private String courseName;
    private String courseImage; // 顯示圖片用
    private Integer priceAtPurchase; // 當時購買價格
}
