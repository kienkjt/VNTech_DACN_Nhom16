package com.nhom16.VNTech.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("PENDING", "Chờ thanh toán"),
    PAID("PAID", "Đã thanh toán"),
    FAILED("FAILED", "Thanh toán thất bại"),
    REFUNDED("REFUNDED", "Đã hoàn tiền"),
    CANCELLED("CANCELLED", "Đã hủy");

    private final String code;
    private final String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentStatus fromString(String text) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.code.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ: " + text);
    }
}