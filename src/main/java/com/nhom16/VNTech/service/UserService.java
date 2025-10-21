package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.UpdateProfileDto;
import com.nhom16.VNTech.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean changePassword(String email, String oldPassword, String newPassword);
    Optional<User> getProfileByEmail(String email);
    void updateProfile(String email, UpdateProfileDto profileDto);
}
