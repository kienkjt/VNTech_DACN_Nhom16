package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean changePassword(String email, String oldPassword, String newPassword);
    Optional<UserProfileDto> getProfileByEmail(String email);
    void updateProfile(String email, UserProfileDto profileDto);
    User updateUserAvatar(Long userId, MultipartFile file) throws IOException;
    void deleteUserAvatar(Long userId) throws IOException;
}
