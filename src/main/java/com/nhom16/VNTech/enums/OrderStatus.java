package com.nhom16.VNTech.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum OrderStatus {
    PENDING("Chờ xác nhận", "Đơn hàng đang chờ xác nhận"),
    CONFIRMED("Đã xác nhận", "Đơn hàng đã được xác nhận"),
    PROCESSING("Đang xử lý", "Đơn hàng đang được chuẩn bị"),
    SHIPPING("Đang giao hàng", "Đơn hàng đang được vận chuyển"),
    DELIVERED("Đã giao hàng", "Đơn hàng đã được giao thành công"),
    CANCELLED("Đã hủy", "Đơn hàng đã bị hủy"),
    RETURNED("Đã trả hàng", "Đơn hàng đã được trả lại"),
    REFUNDED("Đã hoàn tiền", "Đơn hàng đã được hoàn tiền");

    private final String name;
    private final String description;

    OrderStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static OrderStatus fromString(String status) {
        if (status == null) return null;
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ: " + status);
        }
    }

    // Kiểm tra xem có thể chuyển sang trạng thái mới không
    public boolean canTransitionTo(OrderStatus newStatus) {
        return getNextPossibleStatuses().contains(newStatus);
    }

    // Lấy danh sách trạng thái có thể chuyển tiếp
    public java.util.List<OrderStatus> getNextPossibleStatuses() {
        return switch (this) {
            case PENDING -> java.util.Arrays.asList(CONFIRMED, CANCELLED);
            case CONFIRMED -> java.util.Arrays.asList(PROCESSING, CANCELLED);
            case PROCESSING -> java.util.Arrays.asList(SHIPPING, CANCELLED);
            case SHIPPING -> java.util.Arrays.asList(DELIVERED, RETURNED);
            case DELIVERED -> java.util.Arrays.asList(RETURNED, REFUNDED);
            case CANCELLED -> List.of();
            case RETURNED -> List.of(REFUNDED);
            case REFUNDED -> List.of();
        };
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED || this == PROCESSING;
    }

    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }

}