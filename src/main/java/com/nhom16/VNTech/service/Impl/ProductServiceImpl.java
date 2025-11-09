package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.product.*;
import com.nhom16.VNTech.dto.category.CategoryResponseDto;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.repository.CategoryRepository;
import com.nhom16.VNTech.repository.ProductRepository;
import com.nhom16.VNTech.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(ProductFilterDto filter) {
        Pageable pageable = createPageable(filter);

        Page<Product> products;

        if (hasMultipleFilters(filter)) {
            // Sử dụng filter tổng hợp
            products = productRepository.findProductsWithFilters(
                    filter.getCategoryId(),
                    filter.getProductName(),
                    filter.getBrand(),
                    filter.getMinPrice(),
                    filter.getMaxPrice(),
                    pageable
            );
        } else {
            // Sử dụng filter đơn giản
            products = applySimpleFilters(filter, pageable);
        }

        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + id));
        return convertToDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request) {
        validateProductRequest(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không có danh mục với id: " + request.getCategoryId()));

        Product product = buildProductEntity(request, category);

        // Thêm images nếu có
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImage> images = buildProductImages(request.getImages(), product);
            product.setImages(images);
        }

        // Thêm specifications nếu có
        if (request.getSpecifications() != null && !request.getSpecifications().isEmpty()) {
            List<ProductSpecification> specifications = buildProductSpecifications(request.getSpecifications(), product);
            product.setSpecifications(specifications);
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        validateProductRequest(request);

        Product existingProduct = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + id));

        updateProductEntity(existingProduct, request);

        // Cập nhật category nếu có thay đổi
        if (!existingProduct.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không có danh mục với id: " + request.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // Cập nhật images (xóa cũ, thêm mới)
        updateProductImages(existingProduct, request.getImages());

        // Cập nhật specifications (xóa cũ, thêm mới)
        updateProductSpecifications(existingProduct, request.getSpecifications());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, int newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + productId));

        if (newStock < 0) {
            throw new RuntimeException("Hàng tồn kho không thể âm");
        }

        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    // ========== PRIVATE HELPER METHODS ==========
    // Tạo Pageable từ ProductFilterDto
    private Pageable createPageable(ProductFilterDto filter) {
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, filter.getSortBy());
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    private boolean hasMultipleFilters(ProductFilterDto filter) {
        return filter.getCategoryId() != null ||
                filter.getProductName() != null ||
                filter.getBrand() != null ||
                filter.getMinPrice() != null ||
                filter.getMaxPrice() != null;
    }

    private Page<Product> applySimpleFilters(ProductFilterDto filter, Pageable pageable) {
        if (filter.getCategoryId() != null) {
            return productRepository.findByCategoryId(filter.getCategoryId(), pageable);
        } else if (filter.getProductName() != null) {
            return productRepository.findByProductNameContainingIgnoreCase(filter.getProductName(), pageable);
        } else if (filter.getBrand() != null) {
            return productRepository.findByBrandContainingIgnoreCase(filter.getBrand(), pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    private void validateProductRequest(ProductRequestDto request) {
        if (request.getSalePrice() > request.getOriginalPrice()) {
            throw new RuntimeException("Giá sale không thể lớn hơn giá gốc");
        }

        if (request.getStock() < 0) {
            throw new RuntimeException("Hàng tồn kho không thể âm");
        }
    }

    private Product buildProductEntity(ProductRequestDto request, Category category) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSalePrice(request.getSalePrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setOrigin(request.getOrigin());
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setRating(0);
        product.setQuantitySold(0);
        return product;
    }

    private void updateProductEntity(Product product, ProductRequestDto request) {
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSalePrice(request.getSalePrice());
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setOrigin(request.getOrigin());
        product.setUpdatedAt(LocalDateTime.now());
    }

    private List<ProductImage> buildProductImages(List<ProductImageDto> imageDTOs, Product product) {
        return imageDTOs.stream()
                .map(imageDTO -> {
                    ProductImage image = new ProductImage();
                    image.setImageUrl(imageDTO.getImageUrl());
                    image.setMain(imageDTO.isMain());
                    image.setProducts(product);
                    return image;
                })
                .collect(Collectors.toList());
    }

    private List<ProductSpecification> buildProductSpecifications(
            List<ProductSpecificationDto> specDTOs, Product product) {
        return specDTOs.stream()
                .map(specDTO -> {
                    ProductSpecification spec = new ProductSpecification();
                    spec.setKeyName(specDTO.getKeyName());
                    spec.setValue(specDTO.getValue());
                    spec.setProducts(product);
                    return spec;
                })
                .collect(Collectors.toList());
    }

    private void updateProductImages(Product product, List<ProductImageDto> imageDTOs) {
        if (imageDTOs != null) {
            product.getImages().clear();
            if (!imageDTOs.isEmpty()) {
                List<ProductImage> newImages = buildProductImages(imageDTOs, product);
                product.getImages().addAll(newImages);
            }
        }
    }

    private void updateProductSpecifications(Product product, List<ProductSpecificationDto> specDTOs) {
        if (specDTOs != null) {
            product.getSpecifications().clear();
            if (!specDTOs.isEmpty()) {
                List<ProductSpecification> newSpecs = buildProductSpecifications(specDTOs, product);
                product.getSpecifications().addAll(newSpecs);
            }
        }
    }

    private ProductResponseDto convertToDTO(Product product) {
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
                    .map(image -> new ProductImageDto(
                            image.getImageUrl(),
                            image.isMain()
                    ))
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        // Specifications
        if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
            List<ProductSpecificationDto> specDTOs = product.getSpecifications().stream()
                    .map(spec -> new ProductSpecificationDto(
                            spec.getKeyName(),
                            spec.getValue()
                    ))
                    .collect(Collectors.toList());
            dto.setSpecifications(specDTOs);
        }

        return dto;
    }
}