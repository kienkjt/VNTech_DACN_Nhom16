package com.nhom16.VNTech.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MonthlyRevenueDto {
    private Integer month; // tháng (1-12)
    private BigDecimal revenue; // doanh thu

    public MonthlyRevenueDto(Integer month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

}

