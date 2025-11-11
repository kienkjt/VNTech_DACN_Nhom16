package com.nhom16.VNTech.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequestDto {
    private Long productId;
    private int quantity;
}
