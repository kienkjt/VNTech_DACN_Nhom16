package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.cart.UpdateCartItemRequestDto;
import com.nhom16.VNTech.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<APIResponse<CartResponseDto>> getCart(@RequestHeader("X-User-Id") Long userId) {
        CartResponseDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(APIResponse.success(cart, "Lấy giỏ hàng thành công"));
    }

    @PostMapping("/items")
    public ResponseEntity<APIResponse<CartResponseDto>> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddToCartRequestDto request) {

        CartResponseDto cart = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(cart, "Đã thêm sản phẩm vào giỏ hàng"));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<APIResponse<CartResponseDto>> updateCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequestDto request) {

        CartResponseDto cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(APIResponse.success(cart, "Cập nhật sản phẩm trong giỏ hàng thành công"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<APIResponse<Void>> removeCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {

        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa sản phẩm khỏi giỏ hàng"));
    }

    @DeleteMapping("")
    public ResponseEntity<APIResponse<Void>> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa toàn bộ giỏ hàng"));
    }

    @GetMapping("/count")
    public ResponseEntity<APIResponse<Integer>> getCartItemCount(@RequestHeader("X-User-Id") Long userId) {
        int count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(APIResponse.success(count, "Lấy số lượng sản phẩm trong giỏ hàng thành công"));
    }
}
