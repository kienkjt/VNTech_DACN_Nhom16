package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.entity.Role;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.RoleRepository;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @Override
    public List<User> getAllUsers() {
        logger.info("Lấy danh sách tất cả người dùng");
        return userRepository.findAll();
    }
    @Transactional
    @Override
    public void  deleteUser(Long id){
        logger.info("Xoa người dùng với id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        userRepository.delete(user);
        logger.info("Đã xóa người dùng với id: {}", id);
    }
    @Override
    @Transactional
    public void changeUserRole(String email, String roleName) {
        logger.info("Thay đổi role cho người dùng: {} thành {}", email, roleName);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role: " + roleName));

        user.setRole(role);
        userRepository.save(user);
        logger.info("Đã cập nhật role cho {}: {}", email, roleName);
    }
}