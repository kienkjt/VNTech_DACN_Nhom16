package com.nhom16.VNTech.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponseDto {
    private Long id;
    private String imageUrl;
    private String publicId;
    private boolean isMain;
    private Long productId;
}