package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.order.*;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.enums.PaymentMethod;
import com.nhom16.VNTech.enums.PaymentStatus;
import com.nhom16.VNTech.mapper.OrderMapper;
import com.nhom16.VNTech.repository.*;
import com.nhom16.VNTech.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

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

        // Convert payment method String -> Enum
        PaymentMethod paymentMethod = PaymentMethod.fromString(request.getPaymentMethod());

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(paymentMethod);
        order.setNote(request.getNote());

        // Convert CartItem -> OrderItem
        List<OrderItem> orderItems = cart.getCartItems().stream().map(ci -> {
            OrderItem item = new OrderItem();
            item.setProducts(ci.getProducts());
            item.setQuantity(ci.getQuantity());
            item.setPrice(ci.getPrice());
            item.setOrders(order);
            return item;
        }).collect(Collectors.toList());

        // Tính tổng tiền sản phẩm
        int total = orderItems.stream()
                .mapToInt(oi -> oi.getPrice() * oi.getQuantity())
                .sum();
        order.setTotalPrice(total);

        int shippingFee = 30000;
        order.setShippingFee(shippingFee);

        int discount = 0;
        if ("DISCOUNT10".equalsIgnoreCase(request.getCouponCode())) {
            discount = (int) (total * 0.1);
        }
        order.setDiscount(discount);

        // Tính finalPrice
        order.calculateFinalPrice();

        // Lưu Order
        Order savedOrder = orderRepository.save(order);

        // Lưu OrderItems
        orderItems.forEach(i -> i.setOrders(savedOrder));
        orderItemRepository.saveAll(orderItems);

        // Tạo Payment
        Payment payment = new Payment();
        payment.setOrders(savedOrder);
        payment.setAmount(savedOrder.getFinalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId("TEMP_" + System.currentTimeMillis());

        if (paymentMethod == PaymentMethod.COD) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setNote("Thanh toán khi nhận hàng");

            // Xử lý đơn COD: tự động xác nhận
            savedOrder.setStatus(OrderStatus.CONFIRMED);
            savedOrder.setConfirmedAt(LocalDateTime.now());
            savedOrder.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(savedOrder);

        } else if (paymentMethod == PaymentMethod.VNPAY) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setNote("Chờ thanh toán qua VNPay");
        }

        paymentRepository.save(payment);

        // Xóa giỏ hàng
        cartItemRepository.deleteAllByCart(cart);

        return orderMapper.toOrderResponseDto(savedOrder);
    }
    @Override
    @Transactional
    public OrderResponseDto buyNow(Long userId, BuyNowRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    String.format("Số lượng không đủ trong kho. Chỉ còn %d sản phẩm", product.getStock())
            );
        }

        // Convert payment method
        PaymentMethod paymentMethod = PaymentMethod.fromString(request.getPaymentMethod());

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(paymentMethod);
        order.setNote(request.getNote());

        // Tạo OrderItem từ sản phẩm
        OrderItem orderItem = new OrderItem();
        orderItem.setProducts(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getSalePrice().intValue());
        orderItem.setOrders(order);

        List<OrderItem> orderItems = Collections.singletonList(orderItem);

        // Tính tổng tiền
        int total = orderItem.getPrice() * orderItem.getQuantity();
        order.setTotalPrice(total);

        int shippingFee = 30000;
        order.setShippingFee(shippingFee);

        int discount = 0;
        if ("DISCOUNT10".equalsIgnoreCase(request.getCouponCode())) {
            discount = (int) (total * 0.1);
        }
        order.setDiscount(discount);

        // Tính finalPrice
        order.calculateFinalPrice();

        // Lưu Order
        Order savedOrder = orderRepository.save(order);

        // Lưu OrderItem
        orderItem.setOrders(savedOrder);
        orderItemRepository.save(orderItem);

        // Tạo Payment
        Payment payment = new Payment();
        payment.setOrders(savedOrder);
        payment.setAmount(savedOrder.getFinalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId("TEMP_" + System.currentTimeMillis());

        if (paymentMethod == PaymentMethod.COD) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setNote("Thanh toán khi nhận hàng");
            payment.setPaidAt(null);

            // Xử lý đơn COD: tự động xác nhận
            savedOrder.setStatus(OrderStatus.CONFIRMED);
            savedOrder.setConfirmedAt(LocalDateTime.now());
            savedOrder.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(savedOrder);

        } else if (paymentMethod == PaymentMethod.VNPAY) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setNote("Chờ thanh toán qua VNPay");
            payment.setPaidAt(null);
        }

        paymentRepository.save(payment);

        // Giảm số lượng tồn kho
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        return orderMapper.toOrderResponseDto(savedOrder);
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

        // Cập nhật trạng thái thanh toán nếu có
        paymentRepository.findByOrdersId(orderId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
        });

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

    @Override
    @Transactional
    public void updateOrderPaymentStatus(Long orderId, String paymentStatus) {
        try {
            PaymentStatus status = PaymentStatus.fromString(paymentStatus);
            Payment payment = paymentRepository.findByOrdersId(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin thanh toán"));

            payment.setStatus(status);
            if (status == PaymentStatus.PAID) {
                payment.setPaidAt(LocalDateTime.now());
            }
            paymentRepository.save(payment);

            if (status == PaymentStatus.PAID) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
                if (order.getStatus() == OrderStatus.PENDING) {
                    order.changeStatus(OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái thanh toán: " + e.getMessage());
        }
    }
}