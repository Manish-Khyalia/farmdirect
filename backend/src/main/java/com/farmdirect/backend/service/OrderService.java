package com.farmdirect.backend.service;

import com.farmdirect.backend.dto.request.OrderRequest;
import com.farmdirect.backend.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, String customerId);
    OrderResponse getOrderById(String orderId, String userId);
    List<OrderResponse> getMyOrders(String customerId);
    List<OrderResponse> getFarmerOrders(String farmerId);
    List<OrderResponse> getAssignedDeliveries(String deliveryPartnerId);
    OrderResponse assignDeliveryPartner(String orderId, String deliveryPartnerId);
    OrderResponse markAsDelivered(String orderId, String deliveryPartnerId);
    OrderResponse cancelOrder(String orderId, String customerId);
}