package com.ocp.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String merchantTradeNo; // 綠界編號
    private Integer totalAmount;
    private Integer discountAmount;
    private Integer finalAmount;
    private String status; // 轉成字串顯示 (e.g. "PENDING")
    private LocalDateTime createdAt;
    private LocalDateTime paymentDate;

    // 包含底下的商品
    private List<OrderItemDto> items;
}
