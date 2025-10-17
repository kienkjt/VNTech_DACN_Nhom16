package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.ChangePasswordRequestDto;
import com.nhom16.VNTech.dto.ResetPasswordRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.UserService;
import com.nhom16.VNTech.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@RequestMapping("users")
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
        User user = userService.authenticateUser(loginRequest);
        if (user == null) {
            return ResponseEntity.status(401).body("Email hoặc mật khẩu không chính xác!");
        }
        if (!user.isVerified()) {
            return ResponseEntity.status(403).body("Tài khoản chưa được kích hoạt! Vui lòng kiểm tra email xác thực.");
        }
        return ResponseEntity.ok("Đăng nhập thành công!");
    }

    // Quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            userService.initiateForgotPassword(email);
            return ResponseEntity.ok("Đã gửi mã xác nhận đặt lại mật khẩu đến email " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    // Xác minh OTP khi quên mật khẩu
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestParam("otp") String otp) {
        boolean valid = verificationTokenService.validateVerificationToken(otp);
        if (valid) {
            return ResponseEntity.ok("Xác thực OTP thành công! Bây giờ bạn có thể đặt lại mật khẩu.");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }
    }

    // Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("newPassword") String newPassword) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng!");
        }

        User user = userOpt.get();
        user.setPassword(userService.encodePassword(newPassword));
        userService.saveUser(user);

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

    // Đổi mật khẩu (người dùng đã đăng nhập)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequestDto dto,
            @RequestParam("email") String email) {

        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        if (success)
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
    }
}
