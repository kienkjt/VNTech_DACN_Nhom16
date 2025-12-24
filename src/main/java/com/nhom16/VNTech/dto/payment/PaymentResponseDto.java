package com.nhom16.VNTech.dto.payment;

import com.nhom16.VNTech.enums.PaymentMethod;
import com.nhom16.VNTech.enums.PaymentStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private String code;
    private String message;
    private String paymentUrl;
    private String transactionId;

    // Thông tin chi tiết khi trả về order
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Integer amount;
    private String bankCode;
    private String bankTransactionNo;
    private String cardType;
    private String payDate;
    private LocalDateTime paidAt;
}