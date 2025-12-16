package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.coupon.CouponDto;
import com.nhom16.VNTech.dto.coupon.CouponValidationResponseDto;
import com.nhom16.VNTech.dto.coupon.CreateCouponRequestDto;
import com.nhom16.VNTech.entity.Coupon;
import com.nhom16.VNTech.entity.CouponUsage;
import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.enums.DiscountType;
import com.nhom16.VNTech.repository.CouponRepository;
import com.nhom16.VNTech.repository.CouponUsageRepository;
import com.nhom16.VNTech.repository.OrderRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public CouponValidationResponseDto validateAndCalculateDiscount(
            String couponCode,
            Long userId,
            Integer orderValue,
            Integer shippingFee) {

        // Tìm coupon
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(couponCode)
                .orElse(null);

        if (coupon == null) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    "Mã giảm giá không tồn tại",
                    couponCode,
                    null);
        }

        // Kiểm tra thời hạn
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate())) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    "Mã giảm giá chưa có hiệu lực",
                    couponCode,
                    coupon.getDiscountType());
        }

        if (now.isAfter(coupon.getEndDate())) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    "Mã giảm giá đã hết hạn",
                    couponCode,
                    coupon.getDiscountType());
        }

        // Kiểm tra giới hạn sử dụng tổng
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    "Mã giảm giá đã hết lượt sử dụng",
                    couponCode,
                    coupon.getDiscountType());
        }

        // Kiểm tra user đã dùng chưa
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (couponUsageRepository.existsByCouponAndUser(coupon, user)) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    "Bạn đã sử dụng mã giảm giá này rồi",
                    couponCode,
                    coupon.getDiscountType());
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (coupon.getMinOrderValue() != null && orderValue < coupon.getMinOrderValue()) {
            return new CouponValidationResponseDto(
                    false,
                    0,
                    String.format("Đơn hàng phải từ %,d VND trở lên", coupon.getMinOrderValue()),
                    couponCode,
                    coupon.getDiscountType());
        }

        // Tính số tiền giảm
        int discountAmount = calculateDiscount(coupon, orderValue, shippingFee);

        return new CouponValidationResponseDto(
                true,
                discountAmount,
                "Mã giảm giá hợp lệ",
                couponCode,
                coupon.getDiscountType());
    }

    private int calculateDiscount(Coupon coupon, Integer orderValue, Integer shippingFee) {
        int discount = 0;

        switch (coupon.getDiscountType()) {
            case PERCENTAGE:
                // Giảm theo %
                discount = (int) (orderValue * coupon.getDiscountValue() / 100.0);

                // Áp dụng giới hạn tối đa nếu có
                if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                    discount = coupon.getMaxDiscountAmount();
                }
                break;

            case FIXED_AMOUNT:
                // Giảm số tiền cố định
                discount = coupon.getDiscountValue();

                // Không được giảm quá tổng giá trị đơn hàng
                if (discount > orderValue) {
                    discount = orderValue;
                }
                break;

            case FREE_SHIPPING:
                // Miễn phí ship
                discount = shippingFee;
                break;
        }

        return discount;
    }

    @Override
    @Transactional
    public void applyCoupon(String couponCode, Long userId, Long orderId, Integer discountAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        // Tạo bản ghi sử dụng
        CouponUsage usage = new CouponUsage();
        usage.setCoupon(coupon);
        usage.setUser(user);
        usage.setOrder(order);
        usage.setDiscountAmount(discountAmount);
        couponUsageRepository.save(usage);

        // Tăng số lần sử dụng
        coupon.incrementUsageCount();
        couponRepository.save(coupon);
    }

    @Override
    public List<CouponDto> getAllActiveCoupons() {
        return couponRepository.findAllByIsActiveTrueAndEndDateAfter(LocalDateTime.now())
                .stream()
                .filter(Coupon::isValid)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponDto getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại"));

        return toDto(coupon);
    }

    @Override
    @Transactional
    public CouponDto createCoupon(CreateCouponRequestDto request) {
        // Kiểm tra code đã tồn tại chưa
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setDescription(request.getDescription());

        Coupon saved = couponRepository.save(coupon);
        return toDto(saved);
    }

    @Override
    @Transactional
    public CouponDto updateCoupon(Long id, CreateCouponRequestDto request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon không tồn tại"));

        // Kiểm tra nếu đổi code thì code mới chưa tồn tại
        if (!coupon.getCode().equalsIgnoreCase(request.getCode())) {
            if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
                throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
            }
            coupon.setCode(request.getCode().toUpperCase());
        }

        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setDescription(request.getDescription());

        Coupon updated = couponRepository.save(coupon);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon không tồn tại"));

        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CouponDto toDto(Coupon coupon) {
        Integer remainingUsage = null;
        if (coupon.getUsageLimit() != null) {
            remainingUsage = coupon.getUsageLimit() - coupon.getUsageCount();
            if (remainingUsage < 0)
                remainingUsage = 0;
        }

        return new CouponDto(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getDescription(),
                remainingUsage);
    }
}
