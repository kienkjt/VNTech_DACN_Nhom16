package com.nhom16.VNTech.dto.coupon;

import com.nhom16.VNTech.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationResponseDto {
    private Boolean isValid;
    private Integer discountAmount;
    private String message;
    private String couponCode;
    private DiscountType discountType;
}
