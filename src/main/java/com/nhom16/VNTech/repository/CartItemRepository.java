package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Cart;
import com.nhom16.VNTech.entity.CartItem;
import com.nhom16.VNTech.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProducts(Cart cart, Product product);
    Optional<CartItem> findByCart(Cart cart);
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.products.id = :productId")
    Optional<CartItem> findCartItemByCartIdAndProductId(@Param("cartId") Long cartId,@Param("productId") Long productId);

    void deleteByCart(Cart cart); // Xóa tất cả CartItem dựa trên Cart

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.products.id = :productId")
    void deleteByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId); // Xóa CartItem dựa trên cartId và productId

    boolean existsByCartAndProducts(Cart cart, Product product);
}
