package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.order.OrderRequestDto;
import com.nhom16.VNTech.dto.order.OrderResponseDto;
import com.nhom16.VNTech.dto.order.OrderSummaryDto;
import com.nhom16.VNTech.service.OrderService;
import com.nhom16.VNTech.service.UserService;
import com.nhom16.VNTech.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user/orders")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserOrderController(OrderService orderService, UserService userService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private String extractUserEmailFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getEmailFromToken(token);
            }
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }
        throw new RuntimeException("Không tìm thấy JWT trong header Authorization!");
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String email = extractUserEmailFromRequest(request);
        Optional<com.nhom16.VNTech.entity.User> userOpt = userService.findByEmail(email);
        return userOpt.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")).getId();
    }

    @PostMapping
    @Operation(summary = "Tạo đơn hàng mới cho user đang đăng nhập")
    public ResponseEntity<APIResponse<OrderResponseDto>> createOrder(
            @RequestBody OrderRequestDto request,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        OrderResponseDto created = orderService.createOrder(userId, request);
        return ResponseEntity.ok(APIResponse.success(created, "Tạo đơn hàng thành công"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<APIResponse<OrderResponseDto>> getOrderById(
            @PathVariable Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        OrderResponseDto dto = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(APIResponse.success(dto, "Lấy đơn hàng thành công"));
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<APIResponse<OrderResponseDto>> getOrderByCode(
            @PathVariable String orderCode,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        OrderResponseDto dto = orderService.getOrderByCode(orderCode, userId);
        return ResponseEntity.ok(APIResponse.success(dto, "Lấy đơn hàng theo mã thành công"));
    }
    @GetMapping("/my")
    public ResponseEntity<APIResponse<List<OrderSummaryDto>>> getMyOrders(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Long userId = extractUserIdFromRequest(httpRequest);
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            List<com.nhom16.VNTech.dto.order.OrderSummaryDto> list = orderService.getOrdersByUserId(userId, pageable).getContent();
            return ResponseEntity.ok(APIResponse.success(list, "Lấy đơn hàng thành công (phân trang)"));
        } else {
            List<com.nhom16.VNTech.dto.order.OrderSummaryDto> list = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(APIResponse.success(list, "Lấy danh sách đơn hàng thành công"));
        }
    }
    @GetMapping("/my/status")
    public ResponseEntity<APIResponse<List<OrderSummaryDto>>> getMyOrdersByStatus(
            HttpServletRequest httpRequest,
            @RequestParam String status) {
        Long userId = extractUserIdFromRequest(httpRequest);
        com.nhom16.VNTech.enums.OrderStatus st = com.nhom16.VNTech.enums.OrderStatus.fromString(status);
        List<com.nhom16.VNTech.dto.order.OrderSummaryDto> list = orderService.getOrdersByUserIdAndStatus(userId, st);
        return ResponseEntity.ok(APIResponse.success(list, "Lấy đơn hàng theo trạng thái thành công"));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<APIResponse<OrderResponseDto>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            HttpServletRequest httpRequest) {
        Long userId = extractUserIdFromRequest(httpRequest);
        com.nhom16.VNTech.dto.order.OrderResponseDto dto = orderService.cancelOrder(orderId, userId, reason);
        return ResponseEntity.ok(APIResponse.success(dto, "Hủy đơn hàng thành công"));
    }
}

