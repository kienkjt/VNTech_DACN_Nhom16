package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.product.ProductImageResponseDto;
import com.nhom16.VNTech.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("")
    public ResponseEntity<ProductImageResponseDto> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain) throws IOException {

        ProductImageResponseDto response = productImageService.uploadProductImage(productId, file, isMain);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<ProductImageResponseDto>> uploadMultipleProductImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        List<ProductImageResponseDto> responses = productImageService.uploadMultipleProductImages(productId, files);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(@PathVariable Long productId) {
        List<ProductImageResponseDto> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @PutMapping("/{imageId}/main")
    public ResponseEntity<ProductImageResponseDto> setMainImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        ProductImageResponseDto response = productImageService.setMainImage(productId, imageId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) throws IOException {

        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}