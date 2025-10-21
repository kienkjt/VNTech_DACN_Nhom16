package com.nhom16.VNTech.service;

import com.nhom16.VNTech.entity.User;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<User> getAllUsers();
    void deleteUser(String email);
    void changeUserRole(String email, String roleName);
}
