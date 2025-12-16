package com.nhom16.VNTech.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationRequestDto {
    private String couponCode;
    private Long userId;
    private Integer orderValue;
    private Integer shippingFee;
}
