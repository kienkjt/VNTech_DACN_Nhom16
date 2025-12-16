package com.nhom16.VNTech.dto.coupon;

import com.nhom16.VNTech.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderValue;
    private Integer maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private Integer remainingUsage; // Số lần còn lại có thể dùng
}
