package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrdersId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
}