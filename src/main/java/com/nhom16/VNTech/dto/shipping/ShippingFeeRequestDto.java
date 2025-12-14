package com.nhom16.VNTech.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingFeeRequestDto {
    private String province;
    private Integer orderValue;
}
