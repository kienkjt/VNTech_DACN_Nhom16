package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.order.*;
import com.nhom16.VNTech.dto.payment.PaymentResponseDto;
import com.nhom16.VNTech.dto.product.ProductDto;
import com.nhom16.VNTech.dto.user.UserOrderDto;
import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.OrderItem;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final AddressMapper addressMapper;

    public OrderResponseDto toOrderResponseDto(Order order) {
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
        dto.setUser(toUserOrderDto(order.getUser()));
        dto.setAddress(addressMapper.toDto(order.getAddress()));

        // Map payment details
        if (order.getPayment() != null) {
            PaymentResponseDto payDto = new com.nhom16.VNTech.dto.payment.PaymentResponseDto();
            payDto.setTransactionId(order.getPayment().getTransactionId());
            payDto.setPaymentUrl(order.getPayment().getPaymentUrl());
            payDto.setPaymentMethod(order.getPayment().getPaymentMethod());
            payDto.setStatus(order.getPayment().getStatus());
            payDto.setAmount(order.getPayment().getAmount());
            payDto.setBankCode(order.getPayment().getBankCode());
            payDto.setBankTransactionNo(order.getPayment().getBankTransactionNo());
            payDto.setCardType(order.getPayment().getCardType());
            payDto.setPayDate(order.getPayment().getPayDate());
            payDto.setPaidAt(order.getPayment().getPaidAt());
            dto.setPayment(payDto);
        }

        dto.setPaymentMethod(order.getPaymentMethod());

        if (order.getOrderItems() != null) {
            List<OrderItemDto> items = order.getOrderItems().stream()
                    .map(this::toOrderItemDto)
                    .collect(Collectors.toList());
            dto.setOrderItems(items);
        }

        return dto;
    }

    public OrderSummaryDto toOrderSummaryDto(Order order) {
        if (order == null) return null;

        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setOrderId(order.getId());
        dto.setOrderCode(order.getOrderCode());
//        dto.setStatus(order.getStatus() != null ? order.getStatus().getName() : null);
        dto.setStatus(order.getStatus());
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

    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) return null;

        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setPrice(orderItem.getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());

        Product prod = orderItem.getProducts();
        if (prod != null) {
            dto.setProduct(toProductDto(prod));
        }

        return dto;
    }

    private ProductDto toProductDto(Product product) {
        if (product == null) return null;

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setSalePrice(product.getSalePrice());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setStock(product.getStock());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String main = product.getImages().stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl());
            dto.setMainImage(main);
        }

        return dto;
    }

    private UserOrderDto toUserOrderDto(User user) {
        if (user == null) return null;

        UserOrderDto dto = new UserOrderDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    private String nonNullOrEmpty(String s) {
        return s == null ? "" : s;
    }
}