package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.shipping.ShippingDistanceDto;
import com.nhom16.VNTech.dto.shipping.ShippingFeeResponseDto;

import java.util.List;

public interface ShippingFeeService {


    ShippingFeeResponseDto calculateShippingFee(String province, Integer orderValue);

    ShippingFeeResponseDto calculateShippingFee(Long addressId, Integer orderValue);

    List<ShippingDistanceDto> getAllShippingDistances();

    ShippingDistanceDto getShippingDistanceByProvince(String province);
}
