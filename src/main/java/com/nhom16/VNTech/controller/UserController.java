package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.ChangePasswordRequestDto;
import com.nhom16.VNTech.dto.UpdateProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Không thể xác định người dùng hiện tại!");
        }
        return authentication.getName(); // Đây chính là email trong token
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto dto) {
        String email = getCurrentUserEmail();
        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } else {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(UpdateProfileDto profileDto) {
        String email = getCurrentUserEmail();
        Optional<User> userOpt = userService.getProfileByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng với email: " + email);
        }

        return ResponseEntity.ok(userOpt.get());
    }
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto profileDto) {
        String email = getCurrentUserEmail();

        try {
            userService.updateProfile(email, profileDto);
            return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
