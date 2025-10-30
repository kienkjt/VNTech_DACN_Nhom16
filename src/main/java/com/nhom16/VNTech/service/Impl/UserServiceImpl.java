package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.UserProfileDto;
import com.nhom16.VNTech.entity.Address;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        logger.info("Đổi mật khẩu cho người dùng: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.warn("Không tìm thấy người dùng: {}", email);
            return false;
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Mật khẩu cũ không chính xác cho {}", email);
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        logger.info("Đổi mật khẩu thành công cho {}", email);
        return true;
    }

    @Override
    public Optional<UserProfileDto> getProfileByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserProfileDto);
    }

    private UserProfileDto convertToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setGender(user.getGender());
        dto.setAvatar(user.getAvatar());
        dto.setDateOfBirth(user.getDateOfBirth());

//        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
//            List<AddressDto> addressDtos = user.getAddress().stream().map(address -> {
//                AddressDto addressDto = new AddressDto();
//                addressDto.setId(address.getId());
//                addressDto.setRecipientName(address.getRecipientName());
//                addressDto.setPhoneNumber(address.getPhoneNumber());
//                addressDto.setProvince(address.getProvince());
//                addressDto.setDistrict(address.getDistrict());
//                addressDto.setWard(address.getWard());
//                addressDto.setAddressDetail(address.getAddressDetail());
//                addressDto.setDefault(address.isDefault());
//                return addressDto;
//            }).collect(Collectors.toList());
//            dto.setAddresses(addressDtos);
//        }

        return dto;
    }

    @Override
    @Transactional
    public void updateProfile(String email, UserProfileDto profileDto) {
        logger.info("Cập nhật profile cho: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setUsername(profileDto.getUsername());
        user.setFullName(profileDto.getFullName());
        user.setGender(profileDto.getGender());
        user.setDateOfBirth(profileDto.getDateOfBirth());
        user.setAvatar(profileDto.getAvatar());
        user.setUpdatedAt(LocalDateTime.now());

//        if (profileDto.getAddresses() != null && !profileDto.getAddresses().isEmpty()) {
//            // Xóa tất cả địa chỉ cũ
//            if (user.getAddress() != null) {
//                user.getAddress().clear();
//            }
//
//            // Thêm địa chỉ mới
//            List<Address> updatedAddresses = profileDto.getAddresses().stream().map(a -> {
//                Address address = new Address();
//                address.setId(a.getId());
//                address.setRecipientName(a.getRecipientName());
//                address.setPhoneNumber(a.getPhoneNumber());
//                address.setProvince(a.getProvince());
//                address.setDistrict(a.getDistrict());
//                address.setWard(a.getWard());
//                address.setAddressDetail(a.getAddressDetail());
//                address.setDefault(a.isDefault());
//                address.setUser(user);
//                address.setUpdatedAt(LocalDateTime.now());
//                return address;
//            }).collect(Collectors.toList());
//
//            user.setAddress(updatedAddresses);
//        }

        userRepository.save(user);
        logger.info("Cập nhật profile thành công cho: {}", email);
    }
}