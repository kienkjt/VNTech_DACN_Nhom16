package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User registerNewUserAccount(UserRegistrationDto userDto);
    User authenticateUser(LoginRequestDto loginRequest);
    void initiateForgotPassword(String email);
    boolean resetPassword(String token, String newPassword);
    boolean changePassword(String email, String oldPassword, String newPassword);
    Optional<User> findByEmail(String email);
    String encodePassword(String rawPassword);
    void saveUser(User user);

}
