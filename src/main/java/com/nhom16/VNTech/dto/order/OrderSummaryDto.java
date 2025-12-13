package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDto {
    private Long orderId;
    private String orderCode;
    private OrderStatus status;
    private int finalPrice;
    private LocalDateTime createdAt;
    private String recipientName;
    private String phoneNumber;
    private String shortAddress;
    private int itemCount;
}
