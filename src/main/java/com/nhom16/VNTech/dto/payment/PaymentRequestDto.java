package com.nhom16.VNTech.dto.payment;

import com.nhom16.VNTech.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 1000, message = "Số tiền phải lớn hơn 1,000 VND")
    private Long amount;

    private String bankCode;
    private String language = "vn";
}