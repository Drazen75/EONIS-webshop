package com.furniture.store.dto.product;

import com.furniture.store.dto.category.CategoryResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private CategoryResponse category;
    private boolean active;
    private LocalDateTime createdAt;
}
