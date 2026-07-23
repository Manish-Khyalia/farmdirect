package com.farmdirect.backend.service.impl;

import com.farmdirect.backend.dto.request.OrderRequest;
import com.farmdirect.backend.dto.response.OrderItemResponse;
import com.farmdirect.backend.dto.response.OrderResponse;
import com.farmdirect.backend.entity.*;
import com.farmdirect.backend.enums.OrderStatus;
import com.farmdirect.backend.enums.PaymentStatus;
import com.farmdirect.backend.exception.ResourceNotFoundException;
import com.farmdirect.backend.exception.UnauthorizedException;
import com.farmdirect.backend.repository.*;
import com.farmdirect.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    // Convert Order entity to OrderResponse DTO
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtOrder(item.getPriceAtOrder())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomer().getName())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .items(items)
                .paymentMode(order.getPayment() != null ?
                        order.getPayment().getPaymentMode() : null)
                .paymentStatus(order.getPayment() != null ?
                        order.getPayment().getPaymentStatus() : null)
                .build();
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        // Validate products and calculate total
        for (var itemRequest : request.getItems()) {
            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + itemRequest.getProductId()));

            if (product.getStockQty() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: "
                        + product.getName());
            }

            // Reduce stock
            product.setStockQty(
                    product.getStockQty() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .priceAtOrder(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * itemRequest.getQuantity();
        }

        // Create order
        Order order = Order.builder()
                .customer(customer)
                .totalAmount(totalAmount)
                .status(OrderStatus.PLACED)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Link order items to order
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        // Create payment record
        Payment payment = Payment.builder()
                .order(savedOrder)
                .amount(totalAmount)
                .paymentMode(request.getPaymentMode())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        return toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found"));
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getMyOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getFarmerOrders(String farmerId) {
        return orderRepository.findByOrderItems_Product_FarmerId(farmerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getAssignedDeliveries(String deliveryPartnerId) {
        return orderRepository.findByDeliveryPartnerId(deliveryPartnerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse assignDeliveryPartner(String orderId,
                                               String deliveryPartnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found"));

        User deliveryPartner = userRepository.findById(deliveryPartnerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found"));

        order.setDeliveryPartner(deliveryPartner);
        order.setStatus(OrderStatus.ASSIGNED);

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse markAsDelivered(String orderId,
                                         String deliveryPartnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found"));

        if (!order.getDeliveryPartner().getId().equals(deliveryPartnerId)) {
            throw new UnauthorizedException(
                    "You are not assigned to this order");
        }

        order.setStatus(OrderStatus.DELIVERED);

        // Update payment status to completed
        Payment payment = order.getPayment();
        if (payment != null) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
        }

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId, String customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException(
                    "You are not authorized to cancel this order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel a delivered order");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQty(
                    product.getStockQty() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }
}