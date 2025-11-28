package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.user.UserDto;
import com.nhom16.VNTech.dto.user.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired private AddressMapper addressMapper;

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setGender(user.getGender());
        dto.setAvatar(user.getAvatar());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setActive(user.isActive());
        dto.setVerified(user.isVerified());
        dto.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);

        if (user.getAddress() != null) {
            dto.setAddresses(user.getAddress().stream()
                    .map(addressMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) {
            return null;
        }

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setGender(user.getGender());
        dto.setAvatar(user.getAvatar());
        dto.setDateOfBirth(user.getDateOfBirth());
        return dto;
    }

    public void updateUserFromProfileDto(UserProfileDto profileDto, User user) {
        if (profileDto == null || user == null) {
            return;
        }

        user.setUsername(profileDto.getUsername());
        user.setFullName(profileDto.getFullName());
        user.setGender(profileDto.getGender());
        user.setDateOfBirth(profileDto.getDateOfBirth());
        user.setAvatar(profileDto.getAvatar());
    }
}