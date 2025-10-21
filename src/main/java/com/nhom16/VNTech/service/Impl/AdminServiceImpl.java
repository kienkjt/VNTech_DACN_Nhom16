package com.nhom16.VNTech.service.impl;

import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }

    @Override
    public void changeUserRole(String email, String roleName) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.getRole().setRoleName(roleName);
            userRepository.save(user);
        });
    }
}
