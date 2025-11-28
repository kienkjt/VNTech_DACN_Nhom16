package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.review.*;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.Review;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.mapper.ReviewMapper;
import com.nhom16.VNTech.repository.ProductRepository;
import com.nhom16.VNTech.repository.ReviewRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponseDto createReview(String userEmail, ReviewRequestDto reviewDto) {

        User user = getUserByEmail(userEmail);
        Product product = getProductById(reviewDto.getProductId());

        validateRating(reviewDto.getRating());
        validateUserCanReview(user, product);

        Review review = new Review();
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);
        review.setProducts(product);
        review.setVerifiedPurchase(true);

        Review saved = reviewRepository.save(review);

        updateProductRating(product.getId());

        return reviewMapper.toReviewResponseDto(saved);
    }

    @Override
    public ReviewResponseDto updateReview(String userEmail, Long reviewId, ReviewRequestDto reviewDto) {

        User user = getUserByEmail(userEmail);
        Review review = getReviewById(reviewId);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa đánh giá này");
        }

        validateRating(reviewDto.getRating());

        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());

        Review updated = reviewRepository.save(review);

        updateProductRating(review.getProducts().getId());

        return reviewMapper.toReviewResponseDto(updated);
    }

    @Override
    public void deleteReview(String userEmail, Long reviewId) {

        User user = getUserByEmail(userEmail);
        Review review = getReviewById(reviewId);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá này");
        }

        Long productId = review.getProducts().getId();

        reviewRepository.delete(review);

        updateProductRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getUserReviews(String userEmail, Pageable pageable) {

        User user = getUserByEmail(userEmail);

        return reviewRepository
                .findByUserId(user.getId(), pageable)
                .map(reviewMapper::toReviewResponseDto);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean canUserReviewProduct(String userEmail, Long productId) {
        User user = getUserByEmail(userEmail);

        boolean hasReviewed = reviewRepository
                .findByUserIdAndProductsId(user.getId(), productId)
                .isPresent();

        if (hasReviewed) return false;

        return reviewRepository.hasUserPurchasedProduct(user.getId(), productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getProductReviews(Long productId, Pageable pageable) {

        ensureProductExists(productId);

        return reviewRepository
                .findByProductsId(productId, pageable)
                .map(reviewMapper::toReviewResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getProductReviewsByRating(Long productId, Double rating, Pageable pageable) {

        ensureProductExists(productId);
        validateRating(rating);

        return reviewRepository
                .findByProductsIdAndRating(productId, rating, pageable)
                .map(reviewMapper::toReviewResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryDto getProductReviewSummary(Long productId) {

        ensureProductExists(productId);

        return reviewMapper.toReviewSummaryDto(
                reviewRepository.findAverageRatingByProductId(productId),
                reviewRepository.countByProductId(productId),
                reviewRepository.countByProductIdAndRating(productId, 5.0),
                reviewRepository.countByProductIdAndRating(productId, 4.0),
                reviewRepository.countByProductIdAndRating(productId, 3.0),
                reviewRepository.countByProductIdAndRating(productId, 2.0),
                reviewRepository.countByProductIdAndRating(productId, 1.0),
                reviewRepository.countVerifiedReviewsByProductId(productId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateProductAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public void updateProductRating(Long productId) {
        Product product = getProductById(productId);
        product.setRating(calculateProductAverageRating(productId));
        productRepository.save(product);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }

    private Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với id: " + id));
    }

    private void validateRating(Double rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Đánh giá phải từ 1 đến 5 sao");
        }
    }

    private void ensureProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với id: " + productId);
        }
    }

    private void validateUserCanReview(User user, Product product) {

        boolean hasReviewed = reviewRepository
                .findByUserIdAndProductsId(user.getId(), product.getId())
                .isPresent();

        if (hasReviewed) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }

        boolean purchased = reviewRepository.hasUserPurchasedProduct(user.getId(), product.getId());
        if (!purchased) {
            throw new RuntimeException("Bạn cần mua sản phẩm này trước khi đánh giá");
        }
    }
}
