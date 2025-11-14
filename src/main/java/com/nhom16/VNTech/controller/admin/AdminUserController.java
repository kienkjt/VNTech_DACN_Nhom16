package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.user.UserDto;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    public AdminUserController(AdminUserService adminUserService, JwtUtil jwtUtil) {
        this.adminUserService = adminUserService;
        this.jwtUtil = jwtUtil;
    }

    private String extractEmailFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getEmailFromToken(token);
        }
        throw new RuntimeException("Token không hợp lệ hoặc thiếu header Authorization");
    }

    @GetMapping("/users")
    public ResponseEntity<APIResponse<List<UserDto>>> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String adminEmail = extractEmailFromHeader(authHeader);
            logger.info("Quản trị viên {} đang lấy danh sách người dùng", adminEmail);

            List<UserDto> users = adminUserService.getAllUserDtos();
            return ResponseEntity.ok(APIResponse.success(users, "Lấy danh sách người dùng thành công"));
        } catch (Exception ex) {
            logger.error("Lỗi khi lấy danh sách người dùng: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<APIResponse<UserDto>> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String adminEmail = extractEmailFromHeader(authHeader);
            logger.info("Quản trị viên {} đang xem thông tin người dùng id={}", adminEmail, id);

            UserDto user = adminUserService.getUserDtoById(id);
            return ResponseEntity.ok(APIResponse.success(user, "Lấy thông tin người dùng thành công"));
        } catch (Exception ex) {
            logger.error("Lỗi khi lấy người dùng id {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String adminEmail = extractEmailFromHeader(authHeader);
            logger.info("Quản trị viên {} yêu cầu xóa người dùng id={}", adminEmail, id);

            adminUserService.deleteUser(id);
            return ResponseEntity.ok(APIResponse.success(null,
                    "Quản trị viên " + adminEmail + " đã xóa người dùng có id: " + id));
        } catch (Exception ex) {
            logger.error("Lỗi khi xóa người dùng id {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(ex.getMessage()));
        }
    }

}
