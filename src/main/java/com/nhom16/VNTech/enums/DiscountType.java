package com.nhom16.VNTech.enums;

import lombok.Getter;

@Getter
public enum DiscountType {
    PERCENTAGE("Giảm theo phần trăm"),
    FIXED_AMOUNT("Giảm số tiền cố định"),
    FREE_SHIPPING("Miễn phí vận chuyển");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

    public static DiscountType fromString(String value) {
        for (DiscountType type : DiscountType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid discount type: " + value);
    }
}
