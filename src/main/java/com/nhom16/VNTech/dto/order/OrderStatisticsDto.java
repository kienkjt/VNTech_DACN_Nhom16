package com.nhom16.VNTech.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatisticsDto {
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long processingOrders;
    private long shippingOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long returnedOrders;
    private long refundedOrders;
    private long totalRevenue;
    private long todayOrders;
    private long thisWeekOrders;
    private long thisMonthOrders;
    private long thisYearOrders;
}
