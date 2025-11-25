package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.order.OrderResponseDto;
import com.nhom16.VNTech.dto.order.OrderStatisticsDto;
import com.nhom16.VNTech.dto.order.OrderStatusUpdateDto;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.service.Impl.OrderStatisticsServiceImpl;
import com.nhom16.VNTech.service.OrderService;
import com.nhom16.VNTech.service.OrderStatisticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderStatisticsService orderStatisticsService;
    public AdminOrderController(OrderService orderService, OrderStatisticsService orderStatisticsService) {
        this.orderService = orderService;
        this.orderStatisticsService = orderStatisticsService;
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<OrderResponseDto>>> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            List<OrderResponseDto> list = orderService.getAllOrders(pageable).getContent();
            return ResponseEntity.ok(APIResponse.success(list, "Lấy danh sách đơn hàng thành công (phân trang)"));
        } else {
            List<OrderResponseDto> list = orderService.getAllOrders();
            return ResponseEntity.ok(APIResponse.success(list, "Lấy tất cả đơn hàng thành công"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<APIResponse<List<OrderResponseDto>>> getOrdersByStatus(
            @RequestParam String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        OrderStatus st = OrderStatus.fromString(status);
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            List<OrderResponseDto> list = orderService.getOrdersByStatus(st, pageable).getContent();
            return ResponseEntity.ok(APIResponse.success(list, "Lấy đơn hàng theo trạng thái thành công (phân trang)"));
        } else {
            List<OrderResponseDto> list = orderService.getOrdersByStatus(st, Pageable.unpaged()).getContent();
            return ResponseEntity.ok(APIResponse.success(list, "Lấy đơn hàng theo trạng thái thành công"));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<APIResponse<OrderResponseDto>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateDto request) {
        OrderResponseDto dto = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(APIResponse.success(dto, "Cập nhật trạng thái đơn hàng thành công"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<APIResponse<OrderResponseDto>> getOrderById(@PathVariable Long orderId) {
        OrderResponseDto dto = orderService.getOrderById(orderId, null);
        return ResponseEntity.ok(APIResponse.success(dto, "Lấy đơn hàng thành công"));
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<APIResponse<OrderResponseDto>> getOrderByCode(@PathVariable String orderCode) {
        OrderResponseDto dto = orderService.getOrderByCode(orderCode, null);
        return ResponseEntity.ok(APIResponse.success(dto, "Lấy đơn hàng theo mã thành công"));
    }

    @GetMapping("/statistics")
    public ResponseEntity<APIResponse<OrderStatisticsDto>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
       OrderStatisticsDto stats;
        if (start != null && end != null) {
            stats = orderStatisticsService.getOrderStatistics(start, end);
        } else {
            stats = orderStatisticsService.getOrderStatistics();
        }
        return ResponseEntity.ok(APIResponse.success(stats, "Lấy số liệu đơn hàng thành công"));
    }
}

