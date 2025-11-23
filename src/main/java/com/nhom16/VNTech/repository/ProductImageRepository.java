package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductsId(Long productId); // Tìm theo productId

    List<ProductImage> findByProductsIdOrderByIsMainDesc(Long productId); // Tìm theo productId và sắp xếp isMain giảm dần

    List<ProductImage> findByProductsIdAndIsMainTrue(Long productId); // Tìm ảnh chính theo productId

    Optional<ProductImage> findByIdAndProductsId(Long id, Long productId);

    int countByProductsId(Long productId); // Đếm số ảnh theo productId

    void deleteByProductsId(Long productId); // Xoá ảnh theo productId
}