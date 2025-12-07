package com.nhom16.VNTech.dto.payment;

import lombok.Data;

@Data
public class PaymentResultDto {
    private String rspCode;
    private String message;
    private String transactionId;
    private String transactionNo;
    private Long amount;
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    private String payDate;
    private String orderInfo;
    private Long orderId;
}