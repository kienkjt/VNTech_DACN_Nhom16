package com.nhom16.VNTech.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingFeeResponseDto {
    private Integer shippingFee;
    private String province;
    private Integer distanceKm;
    private Integer baseFee;
    private Boolean isFreeShipping;
    private Integer estimatedDays;
}
