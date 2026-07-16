package com.farmdirect.backend.dto.response;

import com.farmdirect.backend.enums.OrderStatus;
import com.farmdirect.backend.enums.PaymentMode;
import com.farmdirect.backend.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String customerName;
    private String deliveryAddress;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
}