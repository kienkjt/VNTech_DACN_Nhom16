package com.nhom16.VNTech.dto.review;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class ReviewRequestDto {
    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    @NotNull(message = "Đánh giá sao không được để trống")
    @Min(value = 1, message = "Đánh giá sao phải từ 1 đến 5")
    @Max(value = 5, message = "Đánh giá sao phải từ 1 đến 5")
    private Double rating;

    //@NotBlank(message = "Nội dung đánh giá không được để trống")
    @Size(min = 10, max = 1000, message = "Nội dung đánh giá phải từ 10 đến 1000 ký tự")
    private String comment;
}