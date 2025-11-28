package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.user.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.mapper.UserMapper;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.FileUploadService;
import com.nhom16.VNTech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            logger.warn("Mật khẩu mới và xác nhận mật khẩu không khớp cho {}", email);
            return false;
        }

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
        return userRepository.findByEmail(email)
                .map(userMapper::toUserProfileDto);
    }

    @Override
    @Transactional
    public void updateProfile(String email, UserProfileDto profileDto) {
        logger.info("Cập nhật profile cho: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        userMapper.updateUserFromProfileDto(profileDto, user);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Cập nhật profile thành công cho: {}", email);
    }

    @Override
    @Transactional
    public UserProfileDto updateUserAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không có người dùng với id: " + userId));

        Map uploadResult = fileUploadService.uploadUserAvatar(file, userId);
        String avatarUrl = uploadResult.get("secure_url").toString();

        user.setAvatar(avatarUrl);
        userRepository.save(user);

        return userMapper.toUserProfileDto(user);
    }

    @Override
    @Transactional
    public void deleteUserAvatar(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không có người dùng với id: " + userId));

        if (user.getAvatar() != null) {
            user.setAvatar(null);
            userRepository.save(user);
        }
    }
}