package com.furniture.store.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutSessionResponse {
    private String checkoutUrl;
    private Long orderId;
}
