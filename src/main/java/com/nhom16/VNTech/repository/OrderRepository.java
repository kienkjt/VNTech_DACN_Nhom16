package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUser(User user);

    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUser(Long id, User user);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.address LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.address LEFT JOIN FETCH o.orderItems WHERE o.orderCode = :orderCode")
    Optional<Order> findByOrderCodeWithDetails(@Param("orderCode") String orderCode);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Long sumDeliveredRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    long countDeliveredOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Tổng doanh thu (tùy chọn khoảng thời gian), loại trừ trạng thái CANCELLED
    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE (:startDate IS NULL OR o.createdAt >= :startDate) AND (:endDate IS NULL OR o.createdAt <= :endDate) AND o.status <> com.nhom16.VNTech.enums.OrderStatus.CANCELLED")
    Long sumTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Tổng số đơn hàng trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE (:startDate IS NULL OR o.createdAt >= :startDate) AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Long countOrdersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Thống kê số đơn theo trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE (:startDate IS NULL OR o.createdAt >= :startDate) AND (:endDate IS NULL OR o.createdAt <= :endDate) GROUP BY o.status")
    List<Object[]> countOrdersGroupedByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Doanh thu theo tháng trong năm
    @Query("SELECT FUNCTION('MONTH', o.createdAt), COALESCE(SUM(o.finalPrice),0) FROM Order o WHERE FUNCTION('YEAR', o.createdAt) = :year AND o.status <> com.nhom16.VNTech.enums.OrderStatus.CANCELLED GROUP BY FUNCTION('MONTH', o.createdAt) ORDER BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> findMonthlyRevenueByYear(@Param("year") int year);
}