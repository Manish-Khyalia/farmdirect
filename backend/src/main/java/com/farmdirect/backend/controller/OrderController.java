package com.farmdirect.backend.controller;

import com.farmdirect.backend.dto.request.OrderRequest;
import com.farmdirect.backend.dto.response.OrderResponse;
import com.farmdirect.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer places order
    @PostMapping("/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication auth) {
        String customerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.placeOrder(request, customerId));
    }

    // Customer views their orders
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication auth) {
        String customerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.getMyOrders(customerId));
    }

    // Customer cancels order
    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderId,
            Authentication auth) {
        String customerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, customerId));
    }

    // Farmer views orders for their products
    @GetMapping("/farmer/received")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<OrderResponse>> getFarmerOrders(
            Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.getFarmerOrders(farmerId));
    }

    // Delivery partner views assigned orders
    @GetMapping("/delivery/assigned")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<List<OrderResponse>> getAssignedDeliveries(
            Authentication auth) {
        String deliveryPartnerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.getAssignedDeliveries(deliveryPartnerId));
    }

    // Delivery partner self-assigns an order
    @PutMapping("/delivery/assign/{orderId}")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<OrderResponse> assignDelivery(
            @PathVariable String orderId,
            Authentication auth) {
        String deliveryPartnerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.assignDeliveryPartner(orderId, deliveryPartnerId));
    }

    // Delivery partner marks order as delivered
    @PutMapping("/delivery/delivered/{orderId}")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<OrderResponse> markDelivered(
            @PathVariable String orderId,
            Authentication auth) {
        String deliveryPartnerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.markAsDelivered(orderId, deliveryPartnerId));
    }

    // Get single order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable String orderId,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                orderService.getOrderById(orderId, userId));
    }
}