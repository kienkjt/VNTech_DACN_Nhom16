package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.order.OrderStatisticsDto;
import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.repository.OrderRepository;
import com.nhom16.VNTech.service.OrderStatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderStatisticsServiceImpl implements OrderStatisticsService {
    private final OrderRepository orderRepository;

    public OrderStatisticsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderStatisticsDto getOrderStatistics() {
        List<Order> orders = orderRepository.findAll();
        OrderStatisticsDto dto = new OrderStatisticsDto();
        dto.setTotalOrders(orders.size());
        long revenue = orders.stream()
                .filter(o -> o.getFinalPrice() > 0)
                .mapToLong(Order::getFinalPrice)
                .sum();
        dto.setTotalRevenue(revenue);
        return dto;
    }

    @Override
    public OrderStatisticsDto getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        OrderStatisticsDto dto = new OrderStatisticsDto();
        dto.setTotalOrders(orders.size());
        long revenue = orders.stream()
                .filter(o -> o.getFinalPrice() > 0)
                .mapToLong(Order::getFinalPrice)
                .sum();
        dto.setTotalRevenue(revenue);
        return dto;
    }

    @Override
    public Long calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        return orders.stream().mapToLong(Order::getFinalPrice).sum();
    }
}
