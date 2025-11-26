package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.order.*;
import com.nhom16.VNTech.dto.product.ProductDto;
import com.nhom16.VNTech.dto.user.UserOrderDto;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.repository.*;
import com.nhom16.VNTech.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            UserRepository userRepository,
                            AddressRepository addressRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setNote(request.getNote());

        List<OrderItem> orderItems;

        // 2 cách mua
        if (request.getProductId() != null && request.getQuantity() != null) {
            // c1 : mua trực tiếp tại trang sản phẩm
            orderItems = createOrderFromDirectPurchase(request, order);
        } else {
            // c2 : mua từ giỏ hàng
            orderItems = createOrderFromCart(userId, request, order);
        }

        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm nào để đặt hàng");
        }

        int total = orderItems.stream().mapToInt(oi -> oi.getPrice() * oi.getQuantity()).sum();
        order.setTotalPrice(total);

        int shippingFee = calculateShippingFee(address);
        order.setShippingFee(shippingFee);

        int discount = calculateDiscount(total, request.getCouponCode());
        order.setDiscount(discount);

        order.calculateFinalPrice();

        Order saved = orderRepository.save(order);

        orderItems.forEach(oi -> oi.setOrders(saved));
        orderItemRepository.saveAll(orderItems);

        // Xóa sản phẩm đã mua khỏi giỏ hàng
        if (request.getProductId() == null) {
            removePurchasedItemsFromCart(userId, request.getCartItemIds());
        }

        return toOrderResponseDto(saved);
    }
    // c1: mua trực tiếp tại trang sp
    private List<OrderItem> createOrderFromDirectPurchase(OrderRequestDto request, Order order) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Số lượng sản phẩm không đủ trong kho");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProducts(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getSalePrice().intValue());
        orderItem.setOrders(order);

        // Cập nhật số lượng tồn kho
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        return List.of(orderItem);
    }

    // c2 : mua từ giỏ hàng
    private List<OrderItem> createOrderFromCart(Long userId, OrderRequestDto request, Order order) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng trống"));

        List<CartItem> cartItemsToPurchase;

        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            // Mua các sản phẩm được chỉ định trong cartItemIds
            cartItemsToPurchase = cart.getCartItems().stream()
                    .filter(item -> request.getCartItemIds().contains(item.getId()))
                    .collect(Collectors.toList());
        } else {
            // Mua tất cả sản phẩm ĐÃ CHỌN trong giỏ hàng
            cartItemsToPurchase = cart.getCartItems().stream()
                    .filter(CartItem::isSelected)
                    .collect(Collectors.toList());
        }

        if (cartItemsToPurchase.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm nào được chọn để mua");
        }

        // Kiểm tra số lượng tồn kho
        for (CartItem cartItem : cartItemsToPurchase) {
            Product product = cartItem.getProducts();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        String.format("Sản phẩm %s không đủ số lượng trong kho", product.getProductName()));
            }
        }

        // Tạo order items và cập nhật tồn kho
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItemsToPurchase) {
            Product product = cartItem.getProducts();

            OrderItem orderItem = new OrderItem();
            orderItem.setProducts(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrders(order);
            orderItems.add(orderItem);

            // Cập nhật số lượng tồn kho
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        return orderItems;
    }
    // Xóa sản phẩm đã mua khỏi giỏ hàng
    private void removePurchasedItemsFromCart(Long userId, List<Long> cartItemIds) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            // Xóa các items được chỉ định
            for (Long itemId : cartItemIds) {
                cartItemRepository.deleteById(itemId);
            }
        } else {
            // Xóa tất cả items ĐÃ CHỌN
            List<CartItem> selectedItems = cart.getCartItems().stream()
                    .filter(CartItem::isSelected)
                    .collect(Collectors.toList());

            cartItemRepository.deleteAll(selectedItems);
        }
    }


    private int calculateShippingFee(Address address) {
        // Logic tính phí vận chuyển theo tỉnh/thành
        // Hiện tại fix 30,000 VND
        return 30000;
    }

    private int calculateDiscount(int totalPrice, String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return 0;
        }
        if ("DISCOUNT10".equalsIgnoreCase(couponCode)) {
            return (int) (totalPrice * 0.1);
        } else if ("DISCOUNT20".equalsIgnoreCase(couponCode)) {
            return (int) (totalPrice * 0.2);
        }

        return 0;
    }


    @Override
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }
        return toOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto getOrderByCode(String orderCode, Long userId) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }
        return toOrderResponseDto(order);
    }

    @Override
    public List<OrderSummaryDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::toOrderSummaryDto).collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryDto> getOrdersByUserId(Long userId, Pageable pageable) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        List<OrderSummaryDto> dtos = orders.subList(start, end).stream().map(this::toOrderSummaryDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }

    @Override
    public List<OrderSummaryDto> getOrdersByUserIdAndStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(o -> o.getStatus() == status)
                .map(this::toOrderSummaryDto)
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
        return toOrderResponseDto(saved);
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
        return toOrderResponseDto(saved);
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        List<OrderResponseDto> dtos = page.getContent().stream().map(this::toOrderResponseDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toOrderResponseDto).collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponseDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        List<Order> orders = orderRepository.findByStatus(status);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
        List<OrderResponseDto> dtos = orders.subList(start, end).stream().map(this::toOrderResponseDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orders.size());
    }

    private OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) return null;
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus());
        dto.setStatusName(order.getStatus().getName());
        dto.setStatusDescription(order.getStatus().getDescription());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setConfirmedAt(order.getConfirmedAt());
        dto.setProcessingAt(order.getProcessingAt());
        dto.setShippingAt(order.getShippingAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setCancelReason(order.getCancelReason());
        dto.setCanBeCancelled(order.canBeCancelled());
        dto.setUser(convertToUserOrderDto(order.getUser()));
        dto.setAddress(convertToAddressDto(order.getAddress()));
        if (order.getOrderItems() != null) {
            List<OrderItemDto> items = order.getOrderItems().stream().map(oi -> {
                OrderItemDto i = new OrderItemDto();
                i.setId(oi.getId());
                i.setPrice(oi.getPrice());
                i.setQuantity(oi.getQuantity());
                i.setTotalPrice(oi.getPrice() * oi.getQuantity());
                Product prod = oi.getProducts();
                if (prod != null) {
                    ProductDto pd = new ProductDto();
                    pd.setId(prod.getId());
                    pd.setProductName(prod.getProductName());
                    pd.setSalePrice(prod.getSalePrice());
                    pd.setBrand(prod.getBrand());
                    pd.setModel(prod.getModel());
                    pd.setStock(prod.getStock());
                    if (prod.getImages() != null && !prod.getImages().isEmpty()) {
                        String main = prod.getImages().stream().filter(ProductImage::isMain)
                                .map(ProductImage::getImageUrl).findFirst()
                                .orElse(prod.getImages().get(0).getImageUrl());
                        pd.setMainImage(main);
                    }
                    i.setProduct(pd);
                }
                return i;
            }).collect(Collectors.toList());
            dto.setOrderItems(items);
        }
        return dto;
    }

    private OrderSummaryDto toOrderSummaryDto(Order order) {
        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setOrderId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus() != null ? order.getStatus().getName() : null);
        dto.setFinalPrice(order.getFinalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        if (order.getAddress() != null) {
            dto.setRecipientName(order.getAddress().getRecipientName());
            dto.setPhoneNumber(order.getAddress().getPhoneNumber());
            String shortAddr = String.join(", ",
                    nonNullOrEmpty(order.getAddress().getAddressDetail()),
                    nonNullOrEmpty(order.getAddress().getWard()),
                    nonNullOrEmpty(order.getAddress().getDistrict()),
                    nonNullOrEmpty(order.getAddress().getProvince())
            ).replaceAll("(, )+", ", ").trim();
            dto.setShortAddress(shortAddr);
        }
        dto.setItemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);
        return dto;
    }

    private String nonNullOrEmpty(String s) {
        return s == null ? "" : s;
    }

    private UserOrderDto convertToUserOrderDto(User user) {
        if (user == null) return null;
        UserOrderDto dto = new UserOrderDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    private AddressDto convertToAddressDto(Address address) {
        if (address == null) return null;
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setProvince(address.getProvince());
        dto.setDistrict(address.getDistrict());
        dto.setWard(address.getWard());
        dto.setAddressDetail(address.getAddressDetail());
        dto.setIsDefault(address.isDefault());
        return dto;
    }
}
