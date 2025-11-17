package com.nhom16.VNTech.entity;

import com.nhom16.VNTech.enums.OrderStatus;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private int totalPrice;
    private int shippingFee;
    private int discount;
    private int finalPrice;
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime processingAt;
    private LocalDateTime shippingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderCode == null) {
            orderCode = "ORD" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateTimestamps();
    }

    private void updateTimestamps() {
        switch (this.status) {
            case CONFIRMED -> {
                if (confirmedAt == null) confirmedAt = LocalDateTime.now();
            }
            case PROCESSING -> {
                if (processingAt == null) processingAt = LocalDateTime.now();
            }
            case SHIPPING -> {
                if (shippingAt == null) shippingAt = LocalDateTime.now();
            }
            case DELIVERED -> {
                if (deliveredAt == null) deliveredAt = LocalDateTime.now();
            }
            case CANCELLED -> {
                if (cancelledAt == null) cancelledAt = LocalDateTime.now();
            }
            default -> {}
        }
    }

    public void calculateFinalPrice() {
        this.finalPrice = this.totalPrice + this.shippingFee - this.discount;
    }

    public boolean canBeCancelled() {
        return this.status.canBeCancelled();
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Không thể chuyển từ trạng thái %s sang %s",
                            this.status.getName(), newStatus.getName())
            );
        }
        this.status = newStatus;
        updateTimestamps();
    }
}