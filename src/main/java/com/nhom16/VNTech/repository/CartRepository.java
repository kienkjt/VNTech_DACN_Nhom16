package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Cart;
import com.nhom16.VNTech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.products WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId); // Tìm giỏ hàng theo userId và lấy kèm các cartItems và products

    boolean existsByUserId(Long userId); // Kiểm tra giỏ hàng có tồn tại cho userId không
}
