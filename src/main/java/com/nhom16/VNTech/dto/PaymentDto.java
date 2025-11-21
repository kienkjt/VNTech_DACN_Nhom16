package com.nhom16.VNTech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private String paymentMethod;
    private int amount;
    private String status;
    private LocalDateTime paidAt;
}
