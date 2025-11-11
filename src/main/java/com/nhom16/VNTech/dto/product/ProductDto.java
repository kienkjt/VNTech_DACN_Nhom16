package com.nhom16.VNTech.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Dto cho thông tin sản phẩm cơ bản
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String productName;
    private Long salePrice;
    private String brand;
    private String model;
    private int stock;
    private String mainImage;
}
