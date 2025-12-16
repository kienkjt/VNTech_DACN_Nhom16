package com.nhom16.VNTech.config;

import com.nhom16.VNTech.entity.Coupon;
import com.nhom16.VNTech.enums.DiscountType;
import com.nhom16.VNTech.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class CouponDataInitializer implements CommandLineRunner {

    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        if (couponRepository.count() > 0) {
            return;
        }

        System.out.println("Initializing sample coupon data...");

        List<Coupon> coupons = new ArrayList<>();

        // Coupon 1: Giảm 10% cho khách hàng mới
        Coupon welcome10 = new Coupon();
        welcome10.setCode("WELCOME10");
        welcome10.setDiscountType(DiscountType.PERCENTAGE);
        welcome10.setDiscountValue(10); // 10%
        welcome10.setMinOrderValue(500000); // Đơn tối thiểu 500k
        welcome10.setMaxDiscountAmount(200000); // Giảm tối đa 200k
        welcome10.setStartDate(LocalDateTime.now());
        welcome10.setEndDate(LocalDateTime.now().plusMonths(6)); // Hiệu lực 6 tháng
        welcome10.setUsageLimit(null); // Không giới hạn
        welcome10.setDescription("Giảm 10% cho đơn hàng đầu tiên (tối đa 200,000 VND)");
        coupons.add(welcome10);

        // Coupon 2: Miễn phí ship
        Coupon freeShip = new Coupon();
        freeShip.setCode("FREESHIP");
        freeShip.setDiscountType(DiscountType.FREE_SHIPPING);
        freeShip.setDiscountValue(0); // Không cần giá trị
        freeShip.setMinOrderValue(5000000); // Đơn tối thiểu 1 triệu
        freeShip.setMaxDiscountAmount(null);
        freeShip.setStartDate(LocalDateTime.now());
        freeShip.setEndDate(LocalDateTime.now().plusMonths(3));
        freeShip.setUsageLimit(null);
        freeShip.setDescription("Miễn phí vận chuyển cho đơn hàng từ 5,000,000 VND");
        coupons.add(freeShip);

        // Coupon 3: Giảm 50k cố định
        Coupon save50k = new Coupon();
        save50k.setCode("SAVE50K");
        save50k.setDiscountType(DiscountType.FIXED_AMOUNT);
        save50k.setDiscountValue(50000); // Giảm 50k
        save50k.setMinOrderValue(500000); // Đơn tối thiểu 500k
        save50k.setMaxDiscountAmount(null);
        save50k.setStartDate(LocalDateTime.now());
        save50k.setEndDate(LocalDateTime.now().plusMonths(3));
        save50k.setUsageLimit(1000); // Giới hạn 1000 lượt
        save50k.setDescription("Giảm 50,000 VND cho đơn hàng từ 500,000 VND");
        coupons.add(save50k);

        // Coupon 4: Giảm 15% cho đơn lớn
        Coupon bigOrder15 = new Coupon();
        bigOrder15.setCode("BIGORDER15");
        bigOrder15.setDiscountType(DiscountType.PERCENTAGE);
        bigOrder15.setDiscountValue(15); // 15%
        bigOrder15.setMinOrderValue(5000000); // Đơn tối thiểu 5 triệu
        bigOrder15.setMaxDiscountAmount(1000000); // Giảm tối đa 1 triệu
        bigOrder15.setStartDate(LocalDateTime.now());
        bigOrder15.setEndDate(LocalDateTime.now().plusMonths(6));
        bigOrder15.setUsageLimit(null);
        bigOrder15.setDescription("Giảm 15% cho đơn hàng từ 5,000,000 VND (tối đa 1,000,000 VND)");
        coupons.add(bigOrder15);

        // Coupon 5: Flash sale - Giảm 100k
        Coupon flash100k = new Coupon();
        flash100k.setCode("FLASH100K");
        flash100k.setDiscountType(DiscountType.FIXED_AMOUNT);
        flash100k.setDiscountValue(100000); // Giảm 100k
        flash100k.setMinOrderValue(1000000); // Đơn tối thiểu 1 triệu
        flash100k.setMaxDiscountAmount(null);
        flash100k.setStartDate(LocalDateTime.now());
        flash100k.setEndDate(LocalDateTime.now().plusDays(7)); // Chỉ 7 ngày
        flash100k.setUsageLimit(500); // Giới hạn 500 lượt
        flash100k.setDescription("Flash Sale - Giảm 100,000 VND (chỉ 7 ngày, giới hạn 500 lượt)");
        coupons.add(flash100k);

        couponRepository.saveAll(coupons);
        System.out.println("Sample coupon data initialized successfully! Total: " + coupons.size() + " coupons");
    }
}
