package com.nhom16.VNTech.enums;

public enum PaymentMethod {
    COD("COD", "Thanh toán khi nhận hàng"),
    VNPAY("VNPAY", "Thanh toán qua VNPay");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentMethod fromString(String text) {
        if (text == null) {
            return COD;
        }
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.code.equalsIgnoreCase(text) || method.name().equalsIgnoreCase(text)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + text);
    }
}