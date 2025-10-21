package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;

import java.util.Optional;

public interface AuthService {
    User register(UserRegistrationDto dto);
    User authenticateUser(LoginRequestDto loginRequest);
    boolean verifyOtp(String otp);
    void forgotPassword(String email);
    boolean resetPassword(String otp, String newPassword);

    boolean validateResetOtp(String otp);

    Optional<User> findByEmail(String email);
    String encodePassword(String newPassword);
    void saveUser(User user);
}
