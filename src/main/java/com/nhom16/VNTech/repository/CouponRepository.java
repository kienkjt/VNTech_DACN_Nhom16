package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeIgnoreCaseAndIsActiveTrue(String code);

    List<Coupon> findAllByIsActiveTrueAndEndDateAfter(LocalDateTime date);

    boolean existsByCodeIgnoreCase(String code);
}
