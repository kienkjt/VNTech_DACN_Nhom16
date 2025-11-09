package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.ChangePasswordRequestDto;
import com.nhom16.VNTech.dto.UserProfileDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "${app.frontend.url:http://localhost:3000}")
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
    public ResponseEntity<APIResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto,
            HttpServletRequest request) {

        String email = extractUserEmailFromRequest(request);
        boolean success = userService.changePassword(
                email,
                dto.getOldPassword(),
                dto.getNewPassword(),
                dto.getConfirmNewPassword()
        );

        if (success) {
            return ResponseEntity.ok(APIResponse.success(null, "Đổi mật khẩu thành công!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin."));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserProfileDto>> getProfile(HttpServletRequest request) {
        String email = extractUserEmailFromRequest(request);

        Optional<UserProfileDto> profile = userService.getProfileByEmail(email);

        if (profile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("Không tìm thấy người dùng với email: " + email));
        }

        return ResponseEntity.ok(APIResponse.success(profile.get(), "Lấy thông tin hồ sơ thành công!"));
    }

    @PutMapping("/profile")
    public ResponseEntity<APIResponse<Void>> updateProfile(
            @Valid @RequestBody UserProfileDto profileDto,
            HttpServletRequest request) {

        String email = extractUserEmailFromRequest(request);
        userService.updateProfile(email, profileDto);

        return ResponseEntity.ok(APIResponse.success(null, "Cập nhật hồ sơ thành công!"));
    }
    @PostMapping("/profile/{userId}/avatar")
    public ResponseEntity<APIResponse<UserProfileDto>> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        UserProfileDto updatedUser = userService.updateUserAvatar(userId, file);
        return ResponseEntity.ok(APIResponse.success(updatedUser, "Tải ảnh đại diện thành công!"));
    }


    @DeleteMapping("/profile/{userId}/avatar")
    public ResponseEntity<APIResponse<Void>> deleteAvatar(@PathVariable Long userId) throws IOException {
        userService.deleteUserAvatar(userId);
        return ResponseEntity.ok(APIResponse.success(null, "Xóa ảnh đại diện thành công!"));
    }
}
