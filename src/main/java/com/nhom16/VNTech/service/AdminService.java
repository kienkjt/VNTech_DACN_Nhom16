package com.nhom16.VNTech.service;

import com.nhom16.VNTech.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<User> getAllUsers();
    @Transactional
    void  deleteUser(Long id);

    void changeUserRole(String email, String roleName);
}
