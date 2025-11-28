package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.cart.CartItemDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.product.ProductDto;
import com.nhom16.VNTech.entity.Cart;
import com.nhom16.VNTech.entity.CartItem;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartResponseDto toCartResponseDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getId());
        dto.setUserId(cart.getUser().getId());

        List<CartItemDto> items = cart.getCartItems().stream()
                .map(this::toCartItemDto)
                .toList();

        List<CartItemDto> selectedItems = items.stream()
                .filter(CartItemDto::isSelected)
                .toList();

        dto.setCartItems(items);
        dto.setTotalItems(items.size());
        dto.setSelectedItems(selectedItems.size());

        dto.setTotalPrice(
                items.stream()
                        .mapToLong(i -> (long) i.getPrice() * i.getQuantity())
                        .sum()
        );

        dto.setSelectedItemsPrice(
                selectedItems.stream()
                        .mapToLong(i -> (long) i.getPrice() * i.getQuantity())
                        .sum()
        );

        return dto;
    }

    public CartItemDto toCartItemDto(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSelected(item.isSelected());
        dto.setProduct(toProductDto(item.getProducts()));
        return dto;
    }

    private ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setSalePrice(product.getSalePrice());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setStock(product.getStock());

        if (!product.getImages().isEmpty()) {
            String mainImg = product.getImages().stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl());
            dto.setMainImage(mainImg);
        }

        return dto;
    }
}