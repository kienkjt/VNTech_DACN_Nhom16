package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.review.*;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import com.nhom16.VNTech.entity.Review;
import com.nhom16.VNTech.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDto toReviewResponseDto(Review review) {
        if (review == null) return null;

        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setVerifiedPurchase(review.isVerifiedPurchase());
        dto.setUser(toReviewUserDto(review.getUser()));
        dto.setProduct(toReviewProductDto(review.getProducts()));

        return dto;
    }

    public ReviewUserDto toReviewUserDto(User user) {
        if (user == null) return null;

        ReviewUserDto dto = new ReviewUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    public ReviewProductDto toReviewProductDto(Product product) {
        if (product == null) return null;

        ReviewProductDto dto = new ReviewProductDto();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String mainImage = product.getImages().stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl());
            dto.setMainImage(mainImage);
        }

        return dto;
    }

    public ReviewSummaryDto toReviewSummaryDto(
            Double averageRating, Integer totalReviews,
            Integer fiveStar, Integer fourStar, Integer threeStar,
            Integer twoStar, Integer oneStar, Integer verifiedReviews
    ) {
        ReviewSummaryDto dto = new ReviewSummaryDto();
        dto.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
        dto.setTotalReviews(totalReviews != null ? totalReviews : 0);
        dto.setFiveStar(fiveStar != null ? fiveStar : 0);
        dto.setFourStar(fourStar != null ? fourStar : 0);
        dto.setThreeStar(threeStar != null ? threeStar : 0);
        dto.setTwoStar(twoStar != null ? twoStar : 0);
        dto.setOneStar(oneStar != null ? oneStar : 0);
        dto.setVerifiedReviews(verifiedReviews != null ? verifiedReviews : 0);

        return dto;
    }
}
