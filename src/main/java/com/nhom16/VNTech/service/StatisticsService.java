package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.statistics.MonthlyRevenueDto;
import com.nhom16.VNTech.dto.statistics.NewUsersDto;
import com.nhom16.VNTech.dto.statistics.OrderStatusCountDto;
import com.nhom16.VNTech.dto.statistics.TopProductDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    BigDecimal getTotalRevenue(LocalDate from, LocalDate to);
    Long getTotalOrders(LocalDate from, LocalDate to);
    Long getTotalCustomers(LocalDate from, LocalDate to);
    Long getOrdersCountByStatus(String status, LocalDate from, LocalDate to);

    List<MonthlyRevenueDto> getMonthlyRevenue(int year);
    List<OrderStatusCountDto> getOrderCountByStatus(LocalDate from, LocalDate to);
    List<TopProductDto> getTopSellingProducts(LocalDateTime from, LocalDateTime to, int limit);
    List<NewUsersDto> getMonthlyNewUsers(int year);
}

