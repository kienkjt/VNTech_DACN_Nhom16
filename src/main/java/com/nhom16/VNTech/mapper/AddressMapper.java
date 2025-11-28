package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }

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

    public Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setId(dto.getId());
        address.setRecipientName(dto.getRecipientName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setAddressDetail(dto.getAddressDetail());
        address.setDefault(dto.getIsDefault());
        return address;
    }
}