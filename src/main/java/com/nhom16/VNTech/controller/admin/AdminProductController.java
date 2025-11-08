package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.product.ProductRequestDto;
import com.nhom16.VNTech.dto.product.ProductResponseDto;
import com.nhom16.VNTech.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<APIResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto createdProduct = productService.createProduct(request);
        return ResponseEntity.ok(APIResponse.success(createdProduct, "Tạo sản phẩm thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(APIResponse.success(updatedProduct, "Cập nhật sản phẩm thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(APIResponse.success(null, "Xóa sản phẩm thành công"));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<APIResponse<Void>> updateStock(
            @PathVariable Long id,
            @RequestParam int stock) {
        productService.updateStock(id, stock);
        return ResponseEntity.ok(APIResponse.success(null, "Cập nhật tồn kho thành công"));
    }
}
