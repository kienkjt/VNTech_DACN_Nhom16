package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.order.*;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.mapper.OrderMapper;
import com.nhom16.VNTech.repository.*;
import com.nhom16.VNTech.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng trống"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setNote(request.getNote());

        List<OrderItem> orderItems = cart.getCartItems().stream().map(ci -> {
            Product p = ci.getProducts();
            OrderItem oi = new OrderItem();
            oi.setProducts(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getPrice());
            oi.setOrders(order);
            return oi;
        }).collect(Collectors.toList());

        int total = orderItems.stream().mapToInt(oi -> oi.getPrice() * oi.getQuantity()).sum();
        order.setTotalPrice(total);

        int shippingFee = 30000;
        order.setShippingFee(shippingFee);

        int discount = 0;
        if (request.getCouponCode() != null && request.getCouponCode().equalsIgnoreCase("DISCOUNT10")) {
            discount = (int) (total * 0.1);
        }
        order.setDiscount(discount);

        order.calculateFinalPrice();

        Order saved = orderRepository.save(order);

        orderItems.forEach(oi -> oi.setOrders(saved));
        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAllByCart(cart);

        return orderMapper.toOrderResponseDto(saved);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }
        return orderMapper.toOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto getOrderByCode(String orderCode, Long userId) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }
        return orderMapper.toOrderResponseDto(order);
    }

    @Override
    public List<OrderSummaryDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(orderMapper::toOrderSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryDto> getOrdersByUserId(Long userId, Pageable pageable) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        List<OrderSummaryDto> dtos = orders.subList(start, end).stream()
                .map(orderMapper::toOrderSummaryDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }

    @Override
    public List<OrderSummaryDto> getOrdersByUserIdAndStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(o -> o.getStatus() == status)
                .map(orderMapper::toOrderSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền hủy đơn hàng này");
        }
        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Đơn hàng không thể hủy ở trạng thái hiện tại");
        }
        order.changeStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderResponseDto(saved);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        OrderStatus newStatus = OrderStatus.fromString(String.valueOf(request.getStatus()));
        order.changeStatus(newStatus);
        if (request.getCancelReason() != null) {
            order.setCancelReason(request.getCancelReason());
        }
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderResponseDto(saved);
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        List<OrderResponseDto> dtos = page.getContent().stream()
                .map(orderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponseDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        List<Order> orders = orderRepository.findByStatus(status);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        List<OrderResponseDto> dtos = orders.subList(start, end).stream()
                .map(orderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }
}