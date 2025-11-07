package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.ChangePasswordRequestDto;
import com.nhom16.VNTech.dto.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private String extractUserEmailFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getEmailFromToken(token);
            }
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }
        throw new RuntimeException("Không tìm thấy JWT trong header Authorization!");
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDto dto,
                                            HttpServletRequest request) {
        String email = extractUserEmailFromRequest(request);

        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword(), dto.getConfirmNewPassword());

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin."));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String email = extractUserEmailFromRequest(request);

        Optional<UserProfileDto> profile = userService.getProfileByEmail(email);

        if (profile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Không tìm thấy người dùng với email: " + email));
        }

        return ResponseEntity.ok(profile.get());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileDto profileDto,
                                           HttpServletRequest request) {
        String email = extractUserEmailFromRequest(request);

        try {
            userService.updateProfile(email, profileDto);
            return ResponseEntity.ok(Map.of("message", "Cập nhật hồ sơ thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi cập nhật hồ sơ: " + e.getMessage()));
        }
    }
    @PostMapping("/profile/{userId}/avatar")
    public ResponseEntity<User> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        User user = userService.updateUserAvatar(userId, file);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/profile/{userId}/avatar")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long userId) throws IOException {
        userService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
