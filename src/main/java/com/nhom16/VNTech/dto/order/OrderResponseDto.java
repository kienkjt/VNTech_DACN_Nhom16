package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.user.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private String orderCode;
    private UserProfileDto user;
    private int totalPrice;
    private int shippingFee;
    private int discount;
    private int finalPrice;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private AddressDto address;
    private List<OrderItemDto> orderItems;
//    private PaymentDto payment;
}
