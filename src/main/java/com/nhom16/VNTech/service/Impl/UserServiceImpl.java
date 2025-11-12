package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.FileUploadService;
import com.nhom16.VNTech.service.UserService;
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
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public UserServiceImpl(FileUploadService fileUploadService, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.fileUploadService = fileUploadService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword,String confirmPassword) {
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
        return userRepository.findByEmail(email).map(this::convertToUserProfileDto);
    }

    private UserProfileDto convertToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setGender(user.getGender());
        //dto.setAvatar(user.getAvatar());
        dto.setDateOfBirth(user.getDateOfBirth());

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
//        user.setAvatar(profileDto.getAvatar());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Cập nhật profile thành công cho: {}", email);
    }
    @Override
    @Transactional
    public UserProfileDto updateUserAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không có người dùng với id: " + userId));

        // Upload ảnh mới trước
        Map uploadResult = fileUploadService.uploadUserAvatar(file, userId);
        String avatarUrl = uploadResult.get("secure_url").toString();

        // Nếu có avatar cũ, xóa sau khi upload thành công
        if (user.getAvatar() != null) {
            // String publicId = extractPublicId(user.getAvatar());
            // fileUploadService.deleteImage(publicId);
        }

        user.setAvatar(avatarUrl);
        userRepository.save(user);

        // Trả về DTO (ẩn password, role, ... )
        return convertToUserProfileDto(user);
    }


    @Override
    @Transactional
    public void deleteUserAvatar(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không có người dùng với "));
        if (user.getAvatar() != null) {
            // Trich xuất publicId từ URL hoặc lưu trữ riêng biệt
            user.setAvatar(null);
            userRepository.save(user);
        }
    }
}