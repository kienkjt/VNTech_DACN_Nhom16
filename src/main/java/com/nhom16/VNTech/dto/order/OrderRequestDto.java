package com.nhom16.VNTech.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private Long addressId;
    private String paymentMethod;
    private String note;
}
