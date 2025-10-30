package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private JwtUtil jwtUtil;

    private String extractEmailFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getEmailFromToken(token);
        }
        throw new RuntimeException("Token không hợp lệ hoặc thiếu header Authorization");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        String adminEmail = extractEmailFromHeader(authHeader);
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String adminEmail = extractEmailFromHeader(authHeader);
        adminService.deleteUser(id);
        return ResponseEntity.ok("Quản trị viên " + adminEmail + " đã xóa người dùng có id: " + id);
    }
    @PutMapping("/users/{email}/role")
    public ResponseEntity<?> changeRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String email,
            @RequestParam String role) {

        String adminEmail = extractEmailFromHeader(authHeader);
        adminService.changeUserRole(email, role);
        return ResponseEntity.ok("Quản trị viên " + adminEmail + " đã cập nhật vai trò mới cho: " + email);
    }
}
