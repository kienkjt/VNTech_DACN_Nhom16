package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateDto {
    private OrderStatus status;;
    private String cancelReason;
}
