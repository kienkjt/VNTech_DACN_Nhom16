package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Coupon;
import com.nhom16.VNTech.entity.CouponUsage;
import com.nhom16.VNTech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    boolean existsByCouponAndUser(Coupon coupon, User user);

    long countByCoupon(Coupon coupon);

    List<CouponUsage> findByCouponOrderByUsedAtDesc(Coupon coupon);

    List<CouponUsage> findByUserOrderByUsedAtDesc(User user);
}
