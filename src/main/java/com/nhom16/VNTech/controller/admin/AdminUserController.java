package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.user.UserDto;
import com.nhom16.VNTech.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<List<UserDto>>> getAllUsers(Principal principal) {
        try {
            String adminEmail = principal.getName();
            logger.info("Quản trị viên {} đang lấy danh sách người dùng", adminEmail);

            List<UserDto> users = adminUserService.getAllUserDtos();
            return ResponseEntity.ok(APIResponse.success(users, "Lấy danh sách người dùng thành công"));
        } catch (Exception ex) {
            logger.error("Lỗi khi lấy danh sách người dùng: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(ex.getMessage()));
        }
    }
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<UserDto>> getUserById(
            Principal principal,
            @PathVariable Long id) {
        try {
            String adminEmail = principal.getName();
            logger.info("Quản trị viên {} đang xem thông tin người dùng id={}", adminEmail, id);

            UserDto user = adminUserService.getUserDtoById(id);
            return ResponseEntity.ok(APIResponse.success(user, "Lấy thông tin người dùng thành công"));
        } catch (Exception ex) {
            logger.error("Lỗi khi lấy người dùng id {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(ex.getMessage()));
        }
    }
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<Void>> deleteUserById(
            Principal principal,
            @PathVariable Long id) {
        try {
            String adminEmail = principal.getName();
            logger.info("Quản trị viên {} yêu cầu xóa người dùng id={}", adminEmail, id);

            adminUserService.deleteUser(id);
            return ResponseEntity.ok(
                    APIResponse.success(null,
                            "Quản trị viên " + adminEmail + " đã xóa người dùng có id: " + id)
            );
        } catch (Exception ex) {
            logger.error("Lỗi khi xóa người dùng id {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(ex.getMessage()));
        }
    }
}
