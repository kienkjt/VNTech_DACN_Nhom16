package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        try {
            authService.register(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công! OTP đã được gửi đến email của bạn."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Xác thực tài khoản
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin người dùng
            User user = authService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tài khoản chưa được kích hoạt! Vui lòng kiểm tra email xác thực."));
            }

            // Sinh JWT
            String token = jwtUtil.generateToken(authentication);

            // Trả về thông tin phản hồi
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công!");
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().getRoleName());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email hoặc mật khẩu không chính xác!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp) {
        boolean verified = authService.verifyOtp(otp);
        if (verified) {
            return ResponseEntity.ok(Map.of("message", "Xác thực OTP thành công!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "OTP không hợp lệ hoặc đã hết hạn!"));
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            authService.forgotPassword(email);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã gửi OTP đặt lại mật khẩu đến " + email
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestParam String otp) {
        boolean valid = authService.validateResetOtp(otp);
        if (valid) {
            return ResponseEntity.ok(Map.of("message", "Xác thực OTP thành công! Bây giờ bạn có thể đặt lại mật khẩu."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Mã OTP không hợp lệ hoặc đã hết hạn!"));
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String newPassword) {
        Optional<User> userOpt = authService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng!"));
        }

        User user = userOpt.get();
        user.setPassword(authService.encodePassword(newPassword));
        authService.saveUser(user);

        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công!"));
    }
}
