package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.dto.address.AddressDto;
import com.nhom16.VNTech.dto.PaymentDto;
import com.nhom16.VNTech.dto.user.UserOrderDto;
import com.nhom16.VNTech.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String orderCode;
    private OrderStatus status;
    private String statusName;
    private String statusDescription;
    private int totalPrice;
    private int shippingFee;
    private int discount;
    private int finalPrice;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime processingAt;
    private LocalDateTime shippingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private UserOrderDto user;
    private AddressDto address;
    private PaymentDto payment;
    private List<OrderItemDto> orderItems;
    private boolean canBeCancelled;
    private List<OrderStatus> nextPossibleStatuses;
}
