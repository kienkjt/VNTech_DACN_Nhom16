package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        authService.register(dto);
        return ResponseEntity.ok("Đăng ký thành công! OTP đã được gửi đến email của bạn.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            User user = authService.authenticateUser(loginRequest);

            if (!user.isVerified()) {
                return ResponseEntity.status(403).body("Tài khoản chưa được kích hoạt! Vui lòng kiểm tra email xác thực.");
            }

            return ResponseEntity.ok("Đăng nhập thành công!");

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp) {
        return authService.verifyOtp(otp)
                ? ResponseEntity.ok("Xác thực OTP thành công!")
                : ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Đã gửi OTP đặt lại mật khẩu đến " + email);
    }
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestParam("otp") String otp) {
        boolean valid = authService.validateResetOtp(otp);
        if (valid) {
            return ResponseEntity.ok("Xác thực OTP thành công! Bây giờ bạn có thể đặt lại mật khẩu.");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("newPassword") String newPassword) {
        Optional<User> userOpt = authService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng!");
        }
        User user = userOpt.get();
        user.setPassword(authService.encodePassword(newPassword));
        authService.saveUser(user);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }
}
