package com.nhom16.VNTech.dto.payment;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String code;
    private String message;
    private String paymentUrl;
    private String transactionId;
}