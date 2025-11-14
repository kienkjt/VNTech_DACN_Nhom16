package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.product.ProductImageResponseDto;
import com.nhom16.VNTech.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/products/{productId}/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("")
    public ResponseEntity<APIResponse<ProductImageResponseDto>> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain) {
        try {
            ProductImageResponseDto response = productImageService.uploadProductImage(productId, file, isMain);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(response, "Tải ảnh sản phẩm thành công"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Không thể tải ảnh lên: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/multiple")
    public ResponseEntity<APIResponse<List<ProductImageResponseDto>>> uploadMultipleProductImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<ProductImageResponseDto> responses =
                    productImageService.uploadMultipleProductImages(productId, files);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(responses, "Tải nhiều ảnh sản phẩm thành công"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Không thể tải nhiều ảnh lên: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<List<ProductImageResponseDto>>> getProductImages(@PathVariable Long productId) {
        try {
            List<ProductImageResponseDto> images = productImageService.getProductImages(productId);
            return ResponseEntity.ok(APIResponse.success(images, "Lấy danh sách ảnh sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("Không thể lấy danh sách ảnh: " + e.getMessage()));
        }
    }

    @PutMapping("/{imageId}/main")
    public ResponseEntity<APIResponse<ProductImageResponseDto>> setMainImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            ProductImageResponseDto response = productImageService.setMainImage(productId, imageId);
            return ResponseEntity.ok(APIResponse.success(response, "Cập nhật ảnh chính thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<APIResponse<Void>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            productImageService.deleteProductImage(productId, imageId);
            return ResponseEntity.ok(APIResponse.success(null, "Xóa ảnh thành công"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Không thể xóa ảnh: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }
}
