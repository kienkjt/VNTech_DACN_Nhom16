package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.coupon.CouponDto;
import com.nhom16.VNTech.dto.coupon.CreateCouponRequestDto;
import com.nhom16.VNTech.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Lấy tất cả coupon", description = "Lấy danh sách tất cả mã giảm giá (bao gồm cả inactive)")
    public ResponseEntity<List<CouponDto>> getAllCoupons() {
        List<CouponDto> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping
    @Operation(summary = "Tạo coupon mới", description = "Tạo mã giảm giá mới")
    public ResponseEntity<CouponDto> createCoupon(@RequestBody CreateCouponRequestDto request) {
        CouponDto created = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật coupon", description = "Cập nhật thông tin mã giảm giá")
    public ResponseEntity<CouponDto> updateCoupon(
            @PathVariable Long id,
            @RequestBody CreateCouponRequestDto request) {
        CouponDto updated = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Vô hiệu hóa coupon", description = "Vô hiệu hóa mã giảm giá (soft delete)")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable Long id) {
        couponService.deactivateCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Lấy thông tin coupon", description = "Lấy chi tiết mã giảm giá theo code")
    public ResponseEntity<CouponDto> getCouponByCode(@PathVariable String code) {
        CouponDto coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }
}
