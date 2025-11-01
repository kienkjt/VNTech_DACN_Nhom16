package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.ProductFilterDto;
import com.nhom16.VNTech.dto.ProductRequestDto;
import com.nhom16.VNTech.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponseDto> getProducts(ProductFilterDto filter);
    ProductResponseDto getProductById(Long id);
    ProductResponseDto createProduct(ProductRequestDto request);
    ProductResponseDto updateProduct(Long id, ProductRequestDto request);
    void deleteProduct(Long id);
    void updateStock(Long productId, int newStock);
}