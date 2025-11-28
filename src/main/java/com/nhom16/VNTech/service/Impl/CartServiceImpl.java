package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.cart.UpdateCartItemRequestDto;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.mapper.CartMapper;
import com.nhom16.VNTech.repository.CartItemRepository;
import com.nhom16.VNTech.repository.CartRepository;
import com.nhom16.VNTech.repository.ProductRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponseDto getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        Cart cartWithItems = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        return cartMapper.toCartResponseDto(cartWithItems);
    }

    @Override
    public CartResponseDto addToCart(Long userId, AddToCartRequestDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Số lượng không đủ trong kho");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartAndProducts(cart, product)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProducts(product);
            item.setQuantity(request.getQuantity());
            item.setPrice(product.getSalePrice().intValue());
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        }

        cartItemRepository.save(item);

        return getCartByUserId(userId);
    }

    @Override
    public CartResponseDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequestDto request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục trong giỏ"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Không thuộc quyền sở hữu người dùng");
        }

        if (request.getQuantity() > 0) {
            if (item.getProducts().getStock() < request.getQuantity()) {
                throw new RuntimeException("Không đủ số lượng kho");
            }
            item.setQuantity(request.getQuantity());
        }

        if (request.getSelected() != null) {
            item.setSelected(request.getSelected());
        }

        if (request.getQuantity() == 0) {
            cartItemRepository.delete(item);
        } else {
            cartItemRepository.save(item);
        }

        return getCartByUserId(userId);
    }

    @Override
    public CartResponseDto updateSelectedItems(Long userId, List<Long> itemIds, boolean selected) {
        List<CartItem> items = cartItemRepository.findAllById(itemIds);
        for (CartItem item : items) {
            if (!item.getCart().getUser().getId().equals(userId)) {
                throw new RuntimeException("Không thuộc quyền sở hữu người dùng");
            }
            item.setSelected(selected);
        }
        cartItemRepository.saveAll(items);
        return getCartByUserId(userId);
    }

    @Override
    public void removeCartItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Không hợp lệ");
        }

        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteAllByCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .map(cart -> cart.getCartItems().size())
                .orElse(0);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }
}