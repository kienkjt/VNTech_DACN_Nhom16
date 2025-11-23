package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.AddressRequestDto;
import com.nhom16.VNTech.entity.Address;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.AddressRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final org.springframework.web.client.RestTemplate restTemplate;

    public AddressServiceImpl(AddressRepository addressRepository,
                              UserRepository userRepository,
                              org.springframework.web.client.RestTemplate restTemplate) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDto addAddress(Long userId, AddressRequestDto addressDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (addressDto.isDefault()) {
            unsetDefaultAddresses(userId);
        }

        Address address = new Address();
        address.setRecipientName(addressDto.getRecipientName());
        address.setPhoneNumber(addressDto.getPhoneNumber());
        address.setProvince(addressDto.getProvince());
        address.setDistrict(addressDto.getDistrict());
        address.setWard(addressDto.getWard());
        address.setAddressDetail(addressDto.getAddressDetail());
        address.setDefault(addressDto.isDefault());
        address.setUser(user);
        address.setCreatedAt(LocalDateTime.now());

        Address savedAddress = addressRepository.save(address);
        return convertToDto(savedAddress);
    }

    @Override
    public AddressDto updateAddress(Long userId, Long addressId, AddressRequestDto addressDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (addressDto.isDefault() && !address.isDefault()) {
            unsetDefaultAddresses(userId);
        }

        address.setRecipientName(addressDto.getRecipientName());
        address.setPhoneNumber(addressDto.getPhoneNumber());
        address.setProvince(addressDto.getProvince());
        address.setDistrict(addressDto.getDistrict());
        address.setWard(addressDto.getWard());
        address.setAddressDetail(addressDto.getAddressDetail());
        address.setDefault(addressDto.isDefault());
        address.setUpdatedAt(LocalDateTime.now());

        Address updatedAddress = addressRepository.save(address);
        return convertToDto(updatedAddress);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        addressRepository.delete(address);
    }

    @Override
    public AddressDto setDefaultAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        unsetDefaultAddresses(userId);

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        address.setDefault(true);
        address.setUpdatedAt(LocalDateTime.now());

        Address updatedAddress = addressRepository.save(address);
        return convertToDto(updatedAddress);
    }

    @Override
    public Optional<AddressDto> getDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(this::convertToDto);
    }

    private void unsetDefaultAddresses(Long userId) {
        List<Address> defaultAddresses = addressRepository.findByUserId(userId).stream()
                .filter(Address::isDefault)
                .collect(Collectors.toList());

        for (Address addr : defaultAddresses) {
            addr.setDefault(false);
            addressRepository.save(addr);
        }
    }

    private AddressDto convertToDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setProvince(address.getProvince());
        dto.setDistrict(address.getDistrict());
        dto.setWard(address.getWard());
        dto.setAddressDetail(address.getAddressDetail());
        dto.setIsDefault(address.isDefault());
        return dto;
    }

    // Các method hiện có cho provinces, districts, wards (giữ nguyên)
    @Override
    public List<java.util.Map<String, Object>> getAllProvinces() {
        // implementation từ AddressServiceImpl hiện tại
        String url = "https://vn-public-apis.fpo.vn/provinces/getAll?limit=-1";
        return fetchListFromApi(url);
    }

    @Override
    public List<java.util.Map<String, Object>> getDistrictsByProvince(String provinceCode) {
        String url = String.format("https://vn-public-apis.fpo.vn/districts/getByProvince?provinceCode=%s&limit=-1", provinceCode);
        return fetchListFromApi(url);
    }

    @Override
    public List<java.util.Map<String, Object>> getWardsByDistrict(String districtCode) {
        String url = String.format("https://vn-public-apis.fpo.vn/wards/getByDistrict?districtCode=%s&limit=-1", districtCode);
        return fetchListFromApi(url);
    }

    @SuppressWarnings("unchecked")
    private List<java.util.Map<String, Object>> fetchListFromApi(String url) {
        try {
            Object resp = restTemplate.getForObject(url, Object.class);
            if (resp == null) return java.util.List.of();

            if (resp instanceof java.util.Map) {
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) resp;

                Object data = map.get("data");
                if (data instanceof java.util.Map) {
                    Object innerData = ((java.util.Map<String, Object>) data).get("data");
                    if (innerData instanceof java.util.List) {
                        return (java.util.List<java.util.Map<String, Object>>) innerData;
                    }
                }

                if (data instanceof java.util.List) {
                    return (java.util.List<java.util.Map<String, Object>>) data;
                }
            }

            return java.util.List.of();

        } catch (Exception ex) {
            return java.util.List.of();
        }
    }
}
