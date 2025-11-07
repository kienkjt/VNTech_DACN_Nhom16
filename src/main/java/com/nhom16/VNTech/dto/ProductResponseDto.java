package com.nhom16.VNTech.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jshell.Snippet;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updatedAt;
    private CategoryResponseDto category;
    private List<ProductImageDto> images;
    private List<ProductSpecificationDto> specifications;
}