package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.statistics.*;
import com.nhom16.VNTech.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/total-revenue")
    public TotalNumberDto totalRevenue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        BigDecimal value = statisticsService.getTotalRevenue(from, to);
        return new TotalNumberDto("totalRevenue", value);
    }

    @GetMapping("/total-orders")
    public TotalNumberDto totalOrders(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long value = statisticsService.getTotalOrders(from, to);
        return new TotalNumberDto("totalOrders", BigDecimal.valueOf(value));
    }

    @GetMapping("/total-customers")
    public TotalNumberDto totalCustomers(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long value = statisticsService.getTotalCustomers(from, to);
        return new TotalNumberDto("totalCustomers", BigDecimal.valueOf(value));
    }

    @GetMapping("/pending-orders")
    public TotalNumberDto pendingOrders(@RequestParam(required = false, defaultValue = "PENDING") String status,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long value = statisticsService.getOrdersCountByStatus(status, from, to);
        return new TotalNumberDto("pendingOrders", BigDecimal.valueOf(value));
    }

    @GetMapping("/revenue/monthly")
    public List<MonthlyRevenueDto> monthlyRevenue(@RequestParam(required = false) Integer year) {
        int y = year == null ? LocalDate.now().getYear() : year;
        return statisticsService.getMonthlyRevenue(y);
    }

    @GetMapping("/orders/by-status")
    public List<OrderStatusCountDto> ordersByStatus(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return statisticsService.getOrderCountByStatus(from, to);
    }

    @GetMapping("/products/top-selling")
    public List<TopProductDto> topSellingProducts(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                  @RequestParam(required = false, defaultValue = "5") int limit) {
        LocalDateTime s = from == null ? null : from.atStartOfDay();
        LocalDateTime e = to == null ? null : to.atTime(23,59,59);
        return statisticsService.getTopSellingProducts(s, e, limit);
    }

    @GetMapping("/users/new/monthly")
    public List<NewUsersDto> newUsersMonthly(@RequestParam(required = false) Integer year) {
        int y = year == null ? LocalDate.now().getYear() : year;
        return statisticsService.getMonthlyNewUsers(y);
    }
}

