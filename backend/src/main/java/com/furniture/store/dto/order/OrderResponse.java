package com.furniture.store.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String stripeSessionId;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
