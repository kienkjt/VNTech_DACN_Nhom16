package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.product.ProductImageResponseDto;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import com.nhom16.VNTech.repository.ProductImageRepository;
import com.nhom16.VNTech.repository.ProductRepository;
import com.nhom16.VNTech.service.FileUploadService;
import com.nhom16.VNTech.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public ProductImageResponseDto uploadProductImage(Long productId, MultipartFile file, boolean isMain) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm sản phẩm với id: " + productId));

        // Tải ảnh lên Cloudinary
        Map uploadResult = fileUploadService.uploadProductImage(file, productId);

        // Tạo ProductImage entity
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(uploadResult.get("secure_url").toString());
        productImage.setPublicId(uploadResult.get("public_id").toString());
        productImage.setMain(isMain);
        productImage.setProducts(product);

        // Nếu là ảnh chính, bỏ chọn ảnh chính trước đó
        if (isMain) {
            unsetPreviousMainImage(productId);
        }

        ProductImage savedImage = productImageRepository.save(productImage);
        return convertToDTO(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImages(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductsIdOrderByIsMainDesc(productId);
        return images.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductImageResponseDto setMainImage(Long productId, Long imageId) {
        ProductImage productImage = productImageRepository.findByIdAndProductsId(imageId, productId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Bỏ chọn ảnh chính trước đó
        unsetPreviousMainImage(productId);

        // Đặt ảnh hiện tại làm ảnh chính
        productImage.setMain(true);
        ProductImage updatedImage = productImageRepository.save(productImage);

        return convertToDTO(updatedImage);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId, Long imageId) throws IOException {
        ProductImage productImage = productImageRepository.findByIdAndProductsId(imageId, productId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Xóa khỏi Cloudinary
        fileUploadService.deleteImage(productImage.getPublicId());

        // Xóa khỏi database
        productImageRepository.delete(productImage);

        // Nếu ảnh xóa là ảnh chính, đặt lại ảnh chính
        if (productImage.isMain()) {
            setFirstImageAsMain(productId);
        }
    }

    @Override
    @Transactional
    public List<ProductImageResponseDto> uploadMultipleProductImages(Long productId, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean isFirstImage = productImageRepository.countByProductsId(productId) == 0;

        return files.stream()
                .map(file -> {
                    try {
                        boolean isMain = isFirstImage && files.indexOf(file) == 0;
                        return uploadProductImage(productId, file, isMain);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename());
                    }
                })
                .collect(Collectors.toList());
    }

    private void unsetPreviousMainImage(Long productId) {
        productImageRepository.findByProductsIdAndIsMainTrue(productId)
                .forEach(image -> {
                    image.setMain(false);
                    productImageRepository.save(image);
                });
    }

    private void setFirstImageAsMain(Long productId) {
        productImageRepository.findByProductsIdOrderByIsMainDesc(productId)
                .stream()
                .findFirst()
                .ifPresent(image -> {
                    image.setMain(true);
                    productImageRepository.save(image);
                });
    }

    private ProductImageResponseDto convertToDTO(ProductImage productImage) {
        return new ProductImageResponseDto(
                productImage.getId(),
                productImage.getImageUrl(),
                productImage.getPublicId(),
                productImage.isMain(),
                productImage.getProducts().getId()
        );
    }
}