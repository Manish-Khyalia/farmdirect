package com.farmdirect.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double priceAtOrder;
}