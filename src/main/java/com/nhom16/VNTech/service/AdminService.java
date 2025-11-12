package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.user.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdminService {
    @Transactional
    void  deleteUser(Long id);
    void changeUserRole(String email, String roleName);
    List<UserDto> getAllUserDtos();
    UserDto getUserDtoById(Long id);
}
