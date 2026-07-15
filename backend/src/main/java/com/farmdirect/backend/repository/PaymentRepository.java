package com.farmdirect.backend.repository;

import com.farmdirect.backend.entity.Payment;
import com.farmdirect.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}