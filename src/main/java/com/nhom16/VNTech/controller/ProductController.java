package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.product.ProductFilterDto;
import com.nhom16.VNTech.dto.product.ProductResponseDto;
import com.nhom16.VNTech.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<APIResponse<Page<ProductResponseDto>>> getProducts(
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
        return ResponseEntity.ok(APIResponse.success(products, "Lấy danh sách sản phẩm thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(APIResponse.success(product, "Lấy chi tiết sản phẩm thành công"));
    }
}
