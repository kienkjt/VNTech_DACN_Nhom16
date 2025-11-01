package com.nhom16.VNTech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String productName;
    private String description;
    private Long originalPrice;
    private Long salePrice;
    private int stock;
    private int quantitySold;
    private String brand;
    private String model;
    private int rating;
    private String origin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryResponseDto category;
    private List<ProductImageDto> images;
    private List<ProductSpecificationDto> specifications;
}
