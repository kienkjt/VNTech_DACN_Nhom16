package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm sản phẩm theo ID và load cả Category
    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    // Lấy product với lock để tránh race condition khi cập nhật stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    // Tìm sản phẩm theo danh mục
    @EntityGraph(attributePaths = "category")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Tìm sản phẩm theo tên
    @EntityGraph(attributePaths = "category")
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // Tìm sản phẩm theo khoảng giá
    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p WHERE p.salePrice BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") Long minPrice,
                                   @Param("maxPrice") Long maxPrice,
                                   Pageable pageable);

    // Tìm sản phẩm theo thương hiệu
    @EntityGraph(attributePaths = "category")
    Page<Product> findByBrandContainingIgnoreCase(String brand, Pageable pageable);

    // Tìm sản phẩm còn hàng
    @EntityGraph(attributePaths = "category")
    Page<Product> findByStockGreaterThan(int stock, Pageable pageable);

    // Lấy sản phẩm mới nhất
    @EntityGraph(attributePaths = "category")
    Page<Product> findByOrderByCreatedAtDesc(Pageable pageable);

    // Tìm kiếm tổng hợp với nhiều điều kiện
    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:brand IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:minPrice IS NULL OR p.salePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.salePrice <= :maxPrice)")
    Page<Product> findProductsWithFilters(@Param("categoryId") Long categoryId,
                                          @Param("productName") String productName,
                                          @Param("brand") String brand,
                                          @Param("minPrice") Long minPrice,
                                          @Param("maxPrice") Long maxPrice,
                                          Pageable pageable);
}
