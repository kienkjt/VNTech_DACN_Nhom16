// ProductImageService.java
package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.ProductImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {
    ProductImageResponseDto uploadProductImage(Long productId, MultipartFile file, boolean isMain) throws IOException;
    List<ProductImageResponseDto> getProductImages(Long productId);
    ProductImageResponseDto setMainImage(Long productId, Long imageId);
    void deleteProductImage(Long productId, Long imageId) throws IOException;
    List<ProductImageResponseDto> uploadMultipleProductImages(Long productId, List<MultipartFile> files) throws IOException;
}