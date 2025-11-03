package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.UserDto;
import com.nhom16.VNTech.entity.Role;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.RoleRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<UserDto> getAllUserDtos() {
        logger.info("Lấy danh sách tất cả người dùng");
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        logger.info("Lấy thông tin người dùng với id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        return convertToUserDto(user);
    }
    @Transactional
    @Override
    public void deleteUser(Long id) {
        logger.info("Xóa người dùng với id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        userRepository.delete(user);
        logger.info("Đã xóa người dùng với id: {}", id);
    }
    @Transactional
    @Override
    public void changeUserRole(String email, String roleName) {
        logger.info("Thay đổi role cho người dùng: {} thành {}", email, roleName);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role: " + roleName));

        user.setRole(role);
        userRepository.save(user);
        logger.info("Đã cập nhật role cho {}: {}", email, roleName);
    }
    private UserDto convertToUserDto(User user) {
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
            dto.setAddresses(user.getAddress().stream().map(address -> {
                AddressDto a = new AddressDto();
                a.setId(address.getId());
                a.setRecipientName(address.getRecipientName());
                a.setPhoneNumber(address.getPhoneNumber());
                a.setProvince(address.getProvince());
                a.setDistrict(address.getDistrict());
                a.setWard(address.getWard());
                a.setAddressDetail(address.getAddressDetail());
                a.setDefault(address.isDefault());
                return a;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
