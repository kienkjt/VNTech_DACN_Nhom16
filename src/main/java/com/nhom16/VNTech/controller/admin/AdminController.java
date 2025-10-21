package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        adminService.deleteUser(email);
        return ResponseEntity.ok("Đã xóa người dùng có email: " + email);
    }

    @PutMapping("/users/{email}/role")
    public ResponseEntity<?> changeRole(@PathVariable String email, @RequestParam String role) {
        adminService.changeUserRole(email, role);
        return ResponseEntity.ok("Đã cập nhật vai trò cho " + email);
    }
}
