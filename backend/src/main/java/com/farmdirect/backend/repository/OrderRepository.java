package com.farmdirect.backend.repository;

import com.farmdirect.backend.entity.Order;
import com.farmdirect.backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByDeliveryPartnerId(String deliveryPartnerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderItems_Product_FarmerId(String farmerId);
}