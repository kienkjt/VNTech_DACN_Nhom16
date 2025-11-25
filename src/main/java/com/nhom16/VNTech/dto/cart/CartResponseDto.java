package com.nhom16.VNTech.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long cartId;
    private Long userId;
    List<CartItemDto> cartItems;
    private int selectedItems;
    private int totalItems;
    private Long selectedItemsPrice;
    private Long totalPrice;
    //private  int quantity;
}
