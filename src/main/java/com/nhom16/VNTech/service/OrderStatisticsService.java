package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.order.OrderStatisticsDto;

import java.time.LocalDateTime;

public interface OrderStatisticsService {
    OrderStatisticsDto getOrderStatistics();
    OrderStatisticsDto getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate);
    Long calculateRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
