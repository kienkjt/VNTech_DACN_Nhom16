package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.ProductFilterDto;
import com.nhom16.VNTech.dto.ProductResponseDto;
import com.nhom16.VNTech.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        ProductFilterDto filter = new ProductFilterDto();
        filter.setCategoryId(categoryId);
        filter.setProductName(productName);
        filter.setBrand(brand);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        Page<ProductResponseDto> products = productService.getProducts(filter);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}