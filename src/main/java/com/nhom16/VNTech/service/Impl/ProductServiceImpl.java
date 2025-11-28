package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.product.*;
import com.nhom16.VNTech.entity.Category;
import com.nhom16.VNTech.entity.Product;
import com.nhom16.VNTech.entity.ProductImage;
import com.nhom16.VNTech.entity.ProductSpecification;
import com.nhom16.VNTech.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(ProductFilterDto filter) {
        Pageable pageable = createPageable(filter);

        Page<Product> products;

        if (hasMultipleFilters(filter)) {
            products = productRepository.findProductsWithFilters(
                    filter.getCategoryId(),
                    filter.getProductName(),
                    filter.getBrand(),
                    filter.getMinPrice(),
                    filter.getMaxPrice(),
                    pageable
            );
        } else {
            products = applySimpleFilters(filter, pageable);
        }

        return products.map(productMapper::toProductResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + id));
        return productMapper.toProductResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request) {
        validateProductRequest(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không có danh mục với id: " + request.getCategoryId()));

        Product product = buildProductEntity(request, category);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImage> images = buildProductImages(request.getImages(), product);
            product.setImages(images);
        }

        if (request.getSpecifications() != null && !request.getSpecifications().isEmpty()) {
            List<ProductSpecification> specifications = buildProductSpecifications(request.getSpecifications(), product);
            product.setSpecifications(specifications);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        validateProductRequest(request);

        Product existingProduct = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Không có sản phẩm với id: " + id));

        updateProductEntity(existingProduct, request);

        if (!existingProduct.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không có danh mục với id: " + request.getCategoryId()));
            existingProduct.setCategory(category);
        }

        updateProductImages(existingProduct, request.getImages());
        updateProductSpecifications(existingProduct, request.getSpecifications());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toProductResponseDto(updatedProduct);
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
                .map(dto -> productMapper.toProductImageEntity(dto, product))
                .collect(Collectors.toList());
    }

    private List<ProductSpecification> buildProductSpecifications(
            List<ProductSpecificationDto> specDTOs, Product product) {
        return specDTOs.stream()
                .map(dto -> productMapper.toProductSpecificationEntity(dto, product))
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
}