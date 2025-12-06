package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.order.*;
import com.nhom16.VNTech.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(Long userId, OrderRequestDto request);
    OrderResponseDto getOrderById(Long orderId, Long userId);
    OrderResponseDto getOrderByCode(String orderCode, Long userId);
    List<OrderSummaryDto> getOrdersByUserId(Long userId);
    Page<OrderSummaryDto> getOrdersByUserId(Long userId, Pageable pageable);
    List<OrderSummaryDto> getOrdersByUserIdAndStatus(Long userId, OrderStatus status);
    OrderResponseDto cancelOrder(Long orderId, Long userId, String reason);
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto request);
    Page<OrderResponseDto> getAllOrders(Pageable pageable);
    List<OrderResponseDto> getAllOrders();
    Page<OrderResponseDto> getOrdersByStatus(OrderStatus status, Pageable pageable);
    void updateOrderPaymentStatus(Long orderId, String paymentStatus);
}