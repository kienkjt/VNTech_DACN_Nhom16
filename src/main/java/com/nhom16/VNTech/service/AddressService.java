package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.address.AddressDto;
import com.nhom16.VNTech.dto.address.AddressRequestDto;
import com.nhom16.VNTech.dto.address.AddressUpdateRequestDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    // Quản lý địa chỉ user
    List<AddressDto> getAddressesByUserId(Long userId);
    AddressDto addAddress(Long userId, AddressRequestDto addressDto);
    AddressDto updateAddress(Long userId, Long addressId, @Valid AddressUpdateRequestDto addressDto);
    void deleteAddress(Long userId, Long addressId);
    AddressDto setDefaultAddress(Long userId, Long addressId);
    Optional<AddressDto> getDefaultAddress(Long userId);

    // API public cho địa chỉ Việt Nam
    List<java.util.Map<String, Object>> getAllProvinces();
    List<java.util.Map<String, Object>> getDistrictsByProvince(String provinceCode);
    List<java.util.Map<String, Object>> getWardsByDistrict(String districtCode);
}