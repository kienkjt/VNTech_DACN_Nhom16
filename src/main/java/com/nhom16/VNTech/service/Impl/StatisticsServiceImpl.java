package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.statistics.MonthlyRevenueDto;
import com.nhom16.VNTech.dto.statistics.NewUsersDto;
import com.nhom16.VNTech.dto.statistics.OrderStatusCountDto;
import com.nhom16.VNTech.dto.statistics.TopProductDto;
import com.nhom16.VNTech.repository.OrderItemRepository;
import com.nhom16.VNTech.repository.OrderRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatisticsServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    private LocalDateTime toStartOfDay(LocalDate d) {
        return d == null ? null : d.atStartOfDay();
    }

    private LocalDateTime toEndOfDay(LocalDate d) {
        return d == null ? null : d.atTime(LocalTime.MAX);
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDate from, LocalDate to) {
        LocalDateTime s = toStartOfDay(from);
        LocalDateTime e = toEndOfDay(to);
        Long result = orderRepository.sumTotalRevenueBetween(s, e);
        return result == null ? BigDecimal.ZERO : BigDecimal.valueOf(result);
    }

    @Override
    public Long getTotalOrders(LocalDate from, LocalDate to) {
        LocalDateTime s = toStartOfDay(from);
        LocalDateTime e = toEndOfDay(to);
        return orderRepository.countOrdersBetween(s, e);
    }

    @Override
    public Long getTotalCustomers(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return userRepository.count();
        }
        return userRepository.count();
    }

    @Override
    public Long getOrdersCountByStatus(String status, LocalDate from, LocalDate to) {
        if (status == null) {
            return getTotalOrders(from, to);
        }
        try {
            com.nhom16.VNTech.enums.OrderStatus s = com.nhom16.VNTech.enums.OrderStatus.valueOf(status);
            return orderRepository.countByStatus(s);
        } catch (IllegalArgumentException ex) {
            return 0L;
        }
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenue(int year) {
        List<Object[]> raw = orderRepository.findMonthlyRevenueByYear(year);
        List<MonthlyRevenueDto> list = new ArrayList<>();
        for (Object[] row : raw) {
            Integer month = row[0] == null ? null : ((Number) row[0]).intValue();
            BigDecimal revenue = row[1] == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(row[1]));
            list.add(new MonthlyRevenueDto(month, revenue));
        }
        BigDecimal zero = BigDecimal.ZERO;
        List<MonthlyRevenueDto> full = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            MonthlyRevenueDto found = null;
            for (MonthlyRevenueDto dto : list) {
                if (dto.getMonth() != null && dto.getMonth() == m) {
                    found = dto;
                    break;
                }
            }
            if (found != null) full.add(found);
            else full.add(new MonthlyRevenueDto(m, zero));
        }
        return full;
    }

    @Override
    public List<OrderStatusCountDto> getOrderCountByStatus(LocalDate from, LocalDate to) {
        LocalDateTime s = toStartOfDay(from);
        LocalDateTime e = toEndOfDay(to);
        List<Object[]> raw = orderRepository.countOrdersGroupedByStatus(s, e);
        List<OrderStatusCountDto> list = new ArrayList<>();
        for (Object[] row : raw) {
            String st = row[0] == null ? null : row[0].toString();
            Long cnt = row[1] == null ? 0L : ((Number) row[1]).longValue();
            list.add(new OrderStatusCountDto(st, cnt));
        }
        return list;
    }

    @Override
    public List<TopProductDto> getTopSellingProducts(LocalDateTime from, LocalDateTime to, int limit) {
        List<Object[]> raw = orderItemRepository.findTopProducts(from, to, PageRequest.of(0, Math.max(1, limit)));
        List<TopProductDto> list = new ArrayList<>();
        for (Object[] row : raw) {
            Long productId = row[0] == null ? null : ((Number) row[0]).longValue();
            String name = row[1] == null ? null : row[1].toString();
            Long qty = row[2] == null ? 0L : ((Number) row[2]).longValue();
            BigDecimal revenue = row[3] == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(row[3]));
            list.add(new TopProductDto(productId, name, qty, revenue));
        }
        return list;
    }

    @Override
    public List<NewUsersDto> getMonthlyNewUsers(int year) {
        List<Object[]> raw = userRepository.countNewUsersPerMonth(year);
        List<NewUsersDto> list = new ArrayList<>();
        for (Object[] row : raw) {
            Integer month = row[0] == null ? null : ((Number) row[0]).intValue();
            Long cnt = row[1] == null ? 0L : ((Number) row[1]).longValue();
            list.add(new NewUsersDto(month, cnt));
        }
        List<NewUsersDto> full = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            NewUsersDto found = null;
            for (NewUsersDto dto : list) {
                if (dto.getMonth() != null && dto.getMonth() == m) {
                    found = dto; break;
                }
            }
            if (found != null) full.add(found);
            else full.add(new NewUsersDto(m, 0L));
        }
        return full;
    }
}

