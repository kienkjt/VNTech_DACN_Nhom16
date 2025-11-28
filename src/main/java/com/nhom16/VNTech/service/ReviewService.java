package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.review.ReviewRequestDto;
import com.nhom16.VNTech.dto.review.ReviewResponseDto;
import com.nhom16.VNTech.dto.review.ReviewSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponseDto createReview(String userEmail, ReviewRequestDto reviewDto);
    ReviewResponseDto updateReview(String userEmail, Long reviewId, ReviewRequestDto reviewDto);
    void deleteReview(String userEmail, Long reviewId);
    Page<ReviewResponseDto> getUserReviews(String userEmail, Pageable pageable);
    boolean canUserReviewProduct(String userEmail, Long productId);
    Page<ReviewResponseDto> getProductReviews(Long productId, Pageable pageable);
    Page<ReviewResponseDto> getProductReviewsByRating(Long productId, Double rating, Pageable pageable);
    ReviewSummaryDto getProductReviewSummary(Long productId);
    Double calculateProductAverageRating(Long productId);
    void updateProductRating(Long productId);
}