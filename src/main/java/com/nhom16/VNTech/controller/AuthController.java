package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.*;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.AuthService;
import com.nhom16.VNTech.service.Impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<Void>> register(@Valid @RequestBody RegistrationRequestDto dto) {
        try {
            authService.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(null, "Đăng ký thành công! OTP đã được gửi đến email của bạn."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<Map<String, Object>>> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Xác thực tài khoản
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = authService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(APIResponse.error("Tài khoản chưa được kích hoạt! Vui lòng kiểm tra email xác thực."));
            }

            // Sinh JWT và Refresh Token
            String accessToken = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getEmail());

            // Trả về thông tin phản hồi
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công!");
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 604800000); // 7 ngày
            response.put("role", user.getRole().getRoleName());

            return ResponseEntity.ok(APIResponse.success(response, "Đăng nhập thành công!"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.error("Email hoặc mật khẩu không chính xác!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Void>> logout(@RequestBody RefreshTokenRequestDto request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(APIResponse.success(null, "Đăng xuất thành công"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<APIResponse<Map<String, Object>>> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(APIResponse.error("Refresh token không hợp lệ!"));
            }

            String email = jwtUtil.getEmailFromToken(refreshToken);

            User user = authService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            if (!user.isVerified() || !user.isActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(APIResponse.error("Tài khoản không khả dụng"));
            }

            String newAccessToken = jwtUtil.generateTokenFromEmail(email, user.getRole().getRoleName());
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600);

            return ResponseEntity.ok(APIResponse.success(response, "Làm mới token thành công!"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.error("Không thể làm mới token: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<APIResponse<Void>> verifyOtp(@RequestParam String otp) {
        boolean verified = authService.verifyOtp(otp);
        if (verified) {
            return ResponseEntity.ok(APIResponse.success(null, "Xác thực OTP thành công!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("OTP không hợp lệ hoặc đã hết hạn!"));
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<Void>> forgotPassword(@RequestParam String email) {
        try {
            authService.forgotPassword(email);
            return ResponseEntity.ok(APIResponse.success(null, "Đã gửi OTP đặt lại mật khẩu đến " + email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<APIResponse<Void>> verifyResetOtp(@RequestParam String otp) {
        boolean valid = authService.validateResetOtp(otp);
        if (valid) {
            return ResponseEntity.ok(APIResponse.success(null, "Xác thực OTP thành công! Bạn có thể đặt lại mật khẩu."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Mã OTP không hợp lệ hoặc đã hết hạn!"));
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<Void>> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Optional<User> userOpt = authService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Không tìm thấy người dùng!"));
        }

        User user = userOpt.get();
        user.setPassword(authService.encodePassword(newPassword));
        authService.saveUser(user);

        return ResponseEntity.ok(APIResponse.success(null, "Đặt lại mật khẩu thành công!"));
    }
}