package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.product.*;
import com.nhom16.VNTech.dto.category.CategoryResponseDto;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import com.nhom16.VNTech.entity.ProductSpecification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponseDto toProductResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setStock(product.getStock());
        dto.setQuantitySold(product.getQuantitySold());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setRating(product.getRating());
        dto.setOrigin(product.getOrigin());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Category
        if (product.getCategory() != null) {
            dto.setCategory(new CategoryResponseDto(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getCreatedAt(),
                    product.getCategory().getUpdatedAt()
            ));
        }

        // Images
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<ProductImageDto> imageDTOs = product.getImages().stream()
                    .map(this::toProductImageDto)
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        // Specifications
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            List<ProductSpecificationDto> specDTOs = product.getSpecifications().stream()
                    .map(this::toProductSpecificationDto)
                    .collect(Collectors.toList());
            dto.setSpecifications(specDTOs);
        }

        return dto;
    }

    public ProductImageDto toProductImageDto(ProductImage image) {
        if (image == null) {
            return null;
        }
        return new ProductImageDto(image.getImageUrl(), image.isMain());
    }

    public ProductSpecificationDto toProductSpecificationDto(ProductSpecification spec) {
        if (spec == null) {
            return null;
        }
        return new ProductSpecificationDto(spec.getKeyName(), spec.getValue());
    }

    public ProductImage toProductImageEntity(ProductImageDto dto, Product product) {
        if (dto == null) {
            return null;
        }
        ProductImage image = new ProductImage();
        image.setImageUrl(dto.getImageUrl());
        image.setMain(dto.isMain());
        image.setProducts(product);
        return image;
    }

    public ProductSpecification toProductSpecificationEntity(ProductSpecificationDto dto, Product product) {
        if (dto == null) {
            return null;
        }
        ProductSpecification spec = new ProductSpecification();
        spec.setKeyName(dto.getKeyName());
        spec.setValue(dto.getValue());
        spec.setProducts(product);
        return spec;
    }
}