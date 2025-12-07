package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.dto.address.AddressDto;
import com.nhom16.VNTech.dto.payment.PaymentResponseDto;
import com.nhom16.VNTech.dto.user.UserOrderDto;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.enums.PaymentMethod;
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
    private LocalDateTime confirmedAt; // thời gian xác nhận đơn hàng
    private LocalDateTime processingAt; // thời gian đơn hàng đang được xử lý
    private LocalDateTime shippingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private UserOrderDto user;
    private AddressDto address;
    private PaymentResponseDto payment; // trả về thông tin thanh toán
    private PaymentMethod paymentMethod; // phương thức thanh toán
    private List<OrderItemDto> orderItems;
    private boolean canBeCancelled;
    private List<OrderStatus> nextPossibleStatuses;
}