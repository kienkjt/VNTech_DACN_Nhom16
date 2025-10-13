package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.UserService;
import com.nhom16.VNTech.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép React frontend gọi
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    // Đăng ký người dùng mới
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userDto) {
        User user = userService.registerNewUserAccount(userDto);
        return ResponseEntity.ok("Đăng ký thành công! Mã OTP đã được gửi đến email " + user.getEmail());
    }

    // Xác minh mã OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String otp) {
        boolean valid = verificationTokenService.validateVerificationToken(otp);
        if (valid) {
            return ResponseEntity.ok("Xác thực tài khoản thành công!");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }
    }

    // Đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        boolean success = userService.authenticateUser(loginRequest).isEnabled();
        if (success) {
            return ResponseEntity.ok("Đăng nhập thành công!");
        } else {
            return ResponseEntity.status(401).body("Email hoặc mật khẩu không chính xác!");
        }
    }
}
