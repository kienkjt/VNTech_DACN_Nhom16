package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Tìm review theo productId
    Page<Review> findByProductsId(Long productId, Pageable pageable);

    // Tìm review theo productId và rating
    Page<Review> findByProductsIdAndRating(Long productId, Double rating, Pageable pageable);

    // Tìm review của user cho product
    Optional<Review> findByUserIdAndProductsId(Long userId, Long productId);

    // Tìm tất cả review của user
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Tìm review đã xác minh mua hàng
    Page<Review> findByVerifiedPurchaseTrue(Pageable pageable);

    // Thống kê rating theo product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.products.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    // Đếm số lượng review theo rating
    @Query("SELECT COUNT(r) FROM Review r WHERE r.products.id = :productId AND r.rating = :rating")
    Integer countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Double rating);

    // Đếm tổng số review
    @Query("SELECT COUNT(r) FROM Review r WHERE r.products.id = :productId")
    Integer countByProductId(@Param("productId") Long productId);

    // Đếm số review đã xác minh mua hàng
    @Query("SELECT COUNT(r) FROM Review r WHERE r.products.id = :productId AND r.verifiedPurchase = true")
    Integer countVerifiedReviewsByProductId(@Param("productId") Long productId);

    // Lấy review với user và product information
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.products WHERE r.id = :id")
    Optional<Review> findByIdWithUserAndProduct(@Param("id") Long id);

    // Kiểm tra user đã mua sản phẩm chưa
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
            "JOIN oi.orders o " +
            "WHERE o.user.id = :userId AND oi.products.id = :productId AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}