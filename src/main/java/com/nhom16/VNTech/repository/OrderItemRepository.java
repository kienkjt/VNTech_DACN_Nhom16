package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrders(Order order);

    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "LEFT JOIN FETCH oi.products " +
            "WHERE oi.orders.id = :orderId")
    List<OrderItem> findByOrderIdWithProducts(@Param("orderId") Long orderId);

    void deleteByOrders(Order order);

    @Query("SELECT SUM(oi.quantity * oi.price) " +
            "FROM OrderItem oi " +
            "WHERE oi.orders.id = :orderId")
    Integer calculateTotalPriceByOrderId(@Param("orderId") Long orderId);
}
