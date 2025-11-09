package com.nhom16.VNTech.dto.product;

import lombok.Data;

@Data
public class ProductFilterDto {
    private Long categoryId;
    private String productName;
    private String brand;
    private Long minPrice;
    private Long maxPrice;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}
