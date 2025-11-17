package com.nhom16.VNTech.dto.order;

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
    private String status;
    private int finalPrice;
    private LocalDateTime createdAt;
    private String recipientName;
    private String phoneNumber;
    private String shortAddress;
    private int itemCount;
}
