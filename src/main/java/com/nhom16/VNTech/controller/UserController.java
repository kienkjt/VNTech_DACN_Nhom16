package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.config.JwtTokenProvider;
import com.nhom16.VNTech.dto.ChangePasswordRequestDto;
import com.nhom16.VNTech.dto.UpdateProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String extractEmailFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getEmailFromToken(token);
        }
        throw new RuntimeException("Token không hợp lệ hoặc thiếu header Authorization");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        String email = extractEmailFromHeader(authHeader);
        Optional<User> userOpt = userService.getProfileByEmail(email);
        return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Không tìm thấy người dùng!"));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileDto profileDto) {
        String email = extractEmailFromHeader(authHeader);
        try {
            userService.updateProfile(email, profileDto);
            return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequestDto dto) {

        String email = extractEmailFromHeader(authHeader);
        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        if (success)
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
    }
}
