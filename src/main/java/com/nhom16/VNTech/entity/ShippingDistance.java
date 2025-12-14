package com.nhom16.VNTech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "shipping_distances")
public class ShippingDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String province;

    @Column(nullable = false)
    private Integer distanceKm; // Khoảng cách từ Hà Nội (km)

    @Column(nullable = false)
    private Integer baseFee; // Phí ship cơ bản

    @Column(nullable = false)
    private Integer estimatedDays; // Số ngày giao hàng dự kiến

    @Column(nullable = false)
    private Boolean isActive = true; // Trạng thái hoạt động

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
