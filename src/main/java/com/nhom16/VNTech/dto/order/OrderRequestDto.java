package com.nhom16.VNTech.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private Long addressId;
    private String paymentMethod;
    private String note;
    private String couponCode;

    // Cho mua trực tiếp
    private Long productId;
    private Integer quantity;

    // Cho mua từ giỏ hàng
    private List<Long> cartItemIds; // Nếu null thì mua tất cả sản phẩm ĐÃ CHỌN trong giỏ
}