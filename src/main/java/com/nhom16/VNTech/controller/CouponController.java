package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.coupon.CouponDto;
import com.nhom16.VNTech.dto.coupon.CouponValidationRequestDto;
import com.nhom16.VNTech.dto.coupon.CouponValidationResponseDto;
import com.nhom16.VNTech.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    @Operation(summary = "Validate mã giảm giá", description = "Kiểm tra mã giảm giá và tính số tiền giảm")
    public ResponseEntity<CouponValidationResponseDto> validateCoupon(
            @RequestBody CouponValidationRequestDto request) {
        CouponValidationResponseDto response = couponService.validateAndCalculateDiscount(
                request.getCouponCode(),
                request.getUserId(),
                request.getOrderValue(),
                request.getShippingFee());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách mã giảm giá", description = "Lấy tất cả mã giảm giá đang hoạt động")
    public ResponseEntity<List<CouponDto>> getActiveCoupons() {
        List<CouponDto> coupons = couponService.getAllActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Lấy thông tin mã giảm giá", description = "Lấy chi tiết một mã giảm giá theo code")
    public ResponseEntity<CouponDto> getCouponByCode(@PathVariable String code) {
        CouponDto coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }
}
