package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.cart.UpdateCartItemRequestDto;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.CartService;
import com.nhom16.VNTech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                return userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"))
                        .getId();
            }
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }
        throw new RuntimeException("Không tìm thấy JWT trong header Authorization!");
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<CartResponseDto>> getCart(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        CartResponseDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(APIResponse.success(cart, "Lấy giỏ hàng thành công"));
    }

    @PostMapping("/items")
    public ResponseEntity<APIResponse<CartResponseDto>> addToCart(
            HttpServletRequest request,
            @RequestBody AddToCartRequestDto requestDto) {

        Long userId = extractUserIdFromRequest(request);
        CartResponseDto cart = cartService.addToCart(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(cart, "Đã thêm sản phẩm vào giỏ hàng"));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<APIResponse<CartResponseDto>> updateCartItem(
            HttpServletRequest request,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequestDto requestDto) {

        Long userId = extractUserIdFromRequest(request);
        CartResponseDto cart = cartService.updateCartItem(userId, itemId, requestDto);
        return ResponseEntity.ok(APIResponse.success(cart, "Cập nhật sản phẩm trong giỏ hàng thành công"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<APIResponse<Void>> removeCartItem(
            HttpServletRequest request,
            @PathVariable Long itemId) {

        Long userId = extractUserIdFromRequest(request);
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa sản phẩm khỏi giỏ hàng"));
    }

    @DeleteMapping("")
    public ResponseEntity<APIResponse<Void>> clearCart(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        cartService.clearCart(userId);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa toàn bộ giỏ hàng"));
    }

    @GetMapping("/count")
    public ResponseEntity<APIResponse<Integer>> getCartItemCount(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        int count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(APIResponse.success(count, "Lấy số lượng sản phẩm trong giỏ hàng thành công"));
    }
}
