package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.shipping.ShippingDistanceDto;
import com.nhom16.VNTech.dto.shipping.ShippingFeeResponseDto;
import com.nhom16.VNTech.entity.Address;
import com.nhom16.VNTech.entity.ShippingDistance;
import com.nhom16.VNTech.repository.AddressRepository;
import com.nhom16.VNTech.repository.ShippingDistanceRepository;
import com.nhom16.VNTech.service.ShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingFeeServiceImpl implements ShippingFeeService {

    private final ShippingDistanceRepository shippingDistanceRepository;
    private final AddressRepository addressRepository;

    private static final int FREE_SHIPPING_THRESHOLD = 5000000;
    private static final int DEFAULT_SHIPPING_FEE = 30000;

    @Override
    public ShippingFeeResponseDto calculateShippingFee(String province, Integer orderValue) {
        // Kiểm tra miễn phí ship
        if (orderValue >= FREE_SHIPPING_THRESHOLD) {
            return new ShippingFeeResponseDto(
                    0,
                    province,
                    0,
                    0,
                    true,
                    0);
        }

        // Tìm thông tin shipp của tỉnh
        ShippingDistance shippingDistance = shippingDistanceRepository
                .findByProvinceIgnoreCaseAndIsActiveTrue(province)
                .orElse(null);

        if (shippingDistance == null) {
            return new ShippingFeeResponseDto(
                    DEFAULT_SHIPPING_FEE,
                    province,
                    0,
                    DEFAULT_SHIPPING_FEE,
                    false,
                    3);
        }

        return new ShippingFeeResponseDto(
                shippingDistance.getBaseFee(),
                shippingDistance.getProvince(),
                shippingDistance.getDistanceKm(),
                shippingDistance.getBaseFee(),
                false,
                shippingDistance.getEstimatedDays());
    }

    @Override
    public ShippingFeeResponseDto calculateShippingFee(Long addressId, Integer orderValue) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại"));

        return calculateShippingFee(address.getProvince(), orderValue);
    }

    @Override
    public List<ShippingDistanceDto> getAllShippingDistances() {
        return shippingDistanceRepository.findAllByIsActiveTrueOrderByDistanceKm()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShippingDistanceDto getShippingDistanceByProvince(String province) {
        ShippingDistance shippingDistance = shippingDistanceRepository
                .findByProvinceIgnoreCaseAndIsActiveTrue(province)
                .orElseThrow(
                        () -> new IllegalArgumentException("Không tìm thấy thông tin shipping cho tỉnh: " + province));

        return toDto(shippingDistance);
    }

    private ShippingDistanceDto toDto(ShippingDistance entity) {
        return new ShippingDistanceDto(
                entity.getId(),
                entity.getProvince(),
                entity.getDistanceKm(),
                entity.getBaseFee(),
                entity.getEstimatedDays());
    }
}
