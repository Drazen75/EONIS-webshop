package com.furniture.store.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Long orderId;
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private BigDecimal amount;
    private String currency;
    private String customerEmail;
    private String status;
    private LocalDateTime createdAt;

    // Order details for admin panel
    private String userFullName;
    private String shippingAddress;
    private Integer itemCount;
}
