package com.nhom16.VNTech.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingDistanceDto {
    private Long id;
    private String province;
    private Integer distanceKm;
    private Integer baseFee;
    private Integer estimatedDays;
}
