package com.nhom16.VNTech.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {
    private String productName;
    private String description;
    private Long originalPrice;
    private Long salePrice;
    private int stock;
    private String brand;
    private String model;
    private String origin;
    private Long categoryId;
    private List<ProductImageDto> images;
    private List<ProductSpecificationDto> specifications;
}
