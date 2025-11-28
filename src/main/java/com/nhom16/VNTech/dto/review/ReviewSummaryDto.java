package com.nhom16.VNTech.dto.review;

import lombok.Data;

@Data
public class ReviewSummaryDto {
    private Double averageRating;
    private Integer totalReviews;
    private Integer fiveStar;
    private Integer fourStar;
    private Integer threeStar;
    private Integer twoStar;
    private Integer oneStar;
    private Integer verifiedReviews;
}