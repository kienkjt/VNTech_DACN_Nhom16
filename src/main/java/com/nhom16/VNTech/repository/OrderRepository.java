package com.nhom16.VNTech.repository;


import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.enums.OrderStatus;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUser(User user);

//    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    Optional<Order> findByIdAndUser(Long id, User user);

    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // Lấy đơn hàng kèm chi tiết user, địa chỉ và các mục trong đơn hàng
    @Query("SELECT o " +
            "FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.address " +
            "LEFT JOIN FETCH o.orderItems " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    // Lấy đơn hàng kèm chi tiết user, địa chỉ và các mục trong đơn hàng theo mã đơn hàng
    @Query("SELECT o " +
            "FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.address " +
            "LEFT JOIN FETCH o.orderItems " +
            "WHERE o.orderCode = :orderCode")
    Optional<Order> findByOrderCodeWithDetails(@Param("orderCode") String orderCode);

    // Đếm số lượng đơn hàng theo trạng thái
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    // Lấy danh sách đơn hàng của người dùng theo thứ tự thời gian tạo giảm dần
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // Lấy danh sách đơn hàng trong khoảng thời gian cụ thể
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Lấy danh sách đơn hàng của người dùng trong khoảng thời gian cụ thể
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

}
