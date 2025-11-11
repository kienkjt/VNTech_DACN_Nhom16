package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.*;
import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartItemDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.cart.UpdateCartItemRequestDto;
import com.nhom16.VNTech.dto.product.ProductDto;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.repository.CartItemRepository;
import com.nhom16.VNTech.repository.CartRepository;
import com.nhom16.VNTech.repository.ProductRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    //@Transactional(readOnly = true)
    @Transactional
    public CartResponseDto getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToCartResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addToCart(Long userId, AddToCartRequestDto request) {
        // Kiểm tra xem sản phẩm có tồn tại hay không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        // Kiểm tra số lượng tồn kho
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ. Hiện có: " + product.getStock());
        }

        Cart cart = getOrCreateCart(userId);

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng hay chưa
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProducts(cart, product);

        if (existingItem.isPresent()) {
            // Nếu đã có trong giỏ hàng thì cập nhật số lượng
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            // Nếu chưa có thì thêm mới vào giỏ hàng
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProducts(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getSalePrice().intValue());

            cartItemRepository.save(cartItem);
        }

        // Lấy lại giỏ hàng sau khi đã được cập nhật
        Cart updatedCart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        return convertToCartResponseDto(updatedCart);
    }

    @Override
    @Transactional
    public CartResponseDto updateCartItem(Long userId, Long itemId, UpdateCartItemRequestDto request) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ với ID: " + itemId));

        // Kiểm tra xem mục giỏ hàng có thuộc về người dùng hiện tại không
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Sản phẩm này không thuộc giỏ hàng của người dùng");
        }

        // Kiểm tra tồn kho
        if (cartItem.getProducts().getStock() < request.getQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ. Hiện có: " + cartItem.getProducts().getStock());
        }

        // Nếu người dùng nhập số lượng <= 0 thì xóa khỏi giỏ hàng
        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        Cart updatedCart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        return convertToCartResponseDto(updatedCart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ với ID: " + itemId));

        // Kiểm tra xem sản phẩm trong giỏ có thuộc về người dùng không
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Sản phẩm trong giỏ không thuộc về người dùng");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        // Xóa toàn bộ sản phẩm trong giỏ hàng của người dùng
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        // Trả về tổng số sản phẩm trong giỏ hàng
        Cart cart = getOrCreateCart(userId);
        return cart.getCartItems() != null ? cart.getCartItems().size() : 0;
    }

    // Lấy giỏ hàng của người dùng, nếu chưa có thì tự tạo mới
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // Chuyển đổi thực thể giỏ hàng sang DTO trả về cho người dùng
    private CartResponseDto convertToCartResponseDto(Cart cart) {
        CartResponseDto response = new CartResponseDto();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser().getId());

        if (cart.getCartItems() != null) {
            List<CartItemDto> CartItemDtos = cart.getCartItems().stream()
                    .map(this::convertToCartItemDto)
                    .collect(Collectors.toList());
            response.setCartItems(CartItemDtos);

            // Tính tổng số lượng và tổng giá trị đơn hàng
            int totalItems = CartItemDtos.stream().mapToInt(CartItemDto::getQuantity).sum();
            Long totalPrice = CartItemDtos.stream()
                    .mapToLong(item -> (long) item.getPrice() * item.getQuantity())
                    .sum();

            response.setTotalItems(totalItems);
            response.setTotalPrice(totalPrice);
        } else {
            response.setTotalItems(0);
            response.setTotalPrice(0L);
        }

        return response;
    }
    private CartItemDto convertToCartItemDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());

        Product product = cartItem.getProducts();
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductName(product.getProductName());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setBrand(product.getBrand());
        productDto.setModel(product.getModel());
        productDto.setStock(product.getStock());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String mainImage = product.getImages().stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl()); // Nếu không có hình chính, lấy hình đầu tiên
            productDto.setMainImage(mainImage);
        }

        dto.setProduct(productDto);
        return dto;
    }
}
