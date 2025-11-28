package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.user.UserDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.mapper.UserMapper;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUserDtos() {
        logger.info("Lấy danh sách tất cả người dùng");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        logger.info("Lấy thông tin người dùng với id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        return userMapper.toUserDto(user);
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
}