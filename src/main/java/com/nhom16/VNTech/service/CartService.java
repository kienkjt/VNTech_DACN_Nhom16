package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.cart.UpdateCartItemRequestDto;

import java.util.List;

public interface CartService {
    CartResponseDto getCartByUserId(Long userId);
    CartResponseDto addToCart(Long userId, AddToCartRequestDto request);
    CartResponseDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequestDto request);
    CartResponseDto updateSelectedItems(Long userId, List<Long> itemIds, boolean selected);
    void removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
    int getCartItemCount(Long userId);
}
