package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.*;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.AuthService;
import com.nhom16.VNTech.service.UserService;
import com.nhom16.VNTech.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('USER')")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired private UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequestDto dto,
            @RequestParam("email") String email) {

        boolean success = userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        if (success)
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam("email") String email) {
        Optional<User> userOpt = userService.getProfileByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng với email: " + email);
        }
        User user = userOpt.get();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("email") String email,
            @RequestBody UpdateProfileDto profileDto) {
        try {
            userService.updateProfile(email, profileDto);
            return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
