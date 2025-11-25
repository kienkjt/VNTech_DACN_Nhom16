package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Cart;
import com.nhom16.VNTech.entity.CartItem;
import com.nhom16.VNTech.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProducts(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart = :cart")
    void deleteAllByCart(@Param("cart") Cart cart);

    @Modifying
    @Query("""
        DELETE FROM CartItem ci 
        WHERE ci.cart.id = :cartId 
        AND ci.products.id = :productId
    """)
    void deleteByCartIdAndProductId(@Param("cartId") Long cartId,
                                    @Param("productId") Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.products.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    boolean existsByCartAndProducts(Cart cart, Product product);
}