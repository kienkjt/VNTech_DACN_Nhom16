package com.nhom16.VNTech.dto.order;

import lombok.Data;

@Data
public class BuyNowRequestDto {
    private Long productId;
    private int quantity;
    private Long addressId;
    private String paymentMethod;
    private String note;
    private String couponCode;
}