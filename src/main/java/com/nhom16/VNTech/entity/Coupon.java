package com.nhom16.VNTech.entity;

import com.nhom16.VNTech.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Mã giảm giá (VD: WELCOME10, FREESHIP)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // Loại giảm giá

    @Column(nullable = false)
    private Integer discountValue; // Giá trị giảm (% hoặc số tiền)

    private Integer minOrderValue; // Giá trị đơn hàng tối thiểu

    private Integer maxDiscountAmount; // Số tiền giảm tối đa (cho loại %)

    @Column(nullable = false)
    private LocalDateTime startDate; // Ngày bắt đầu

    @Column(nullable = false)
    private LocalDateTime endDate; // Ngày kết thúc

    private Integer usageLimit; // Giới hạn số lần sử dụng (null = không giới hạn)

    @Column(nullable = false)
    private Integer usageCount = 0; // Số lần đã sử dụng

    @Column(nullable = false)
    private Boolean isActive = true; // Trạng thái hoạt động

    @Column(length = 500)
    private String description; // Mô tả

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<CouponUsage> usages;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive
                && now.isAfter(startDate)
                && now.isBefore(endDate)
                && (usageLimit == null || usageCount < usageLimit);
    }

    public boolean canBeUsed() {
        return isValid();
    }

    public void incrementUsageCount() {
        this.usageCount++;
    }
}
