package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.coupon.CouponDto;
import com.nhom16.VNTech.dto.coupon.CouponValidationResponseDto;
import com.nhom16.VNTech.dto.coupon.CreateCouponRequestDto;

import java.util.List;

public interface CouponService {

    /**
     * Validate coupon và tính số tiền giảm
     */
    CouponValidationResponseDto validateAndCalculateDiscount(
            String couponCode,
            Long userId,
            Integer orderValue,
            Integer shippingFee);

    /**
     * Áp dụng coupon và ghi nhận việc sử dụng
     */
    void applyCoupon(String couponCode, Long userId, Long orderId, Integer discountAmount);

    /**
     * Lấy tất cả coupon đang hoạt động
     */
    List<CouponDto> getAllActiveCoupons();

    /**
     * Lấy thông tin coupon theo code
     */
    CouponDto getCouponByCode(String code);

    /**
     * Tạo coupon mới (Admin)
     */
    CouponDto createCoupon(CreateCouponRequestDto request);

    /**
     * Cập nhật coupon (Admin)
     */
    CouponDto updateCoupon(Long id, CreateCouponRequestDto request);

    /**
     * Vô hiệu hóa coupon (Admin)
     */
    void deactivateCoupon(Long id);

    /**
     * Lấy tất cả coupon (Admin)
     */
    List<CouponDto> getAllCoupons();
}
