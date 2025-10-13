package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Đăng ký người dùng mới
    public User registerNewUserAccount(UserRegistrationDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được đăng ký!");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername() != null ? userDto.getUsername() : userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(false);
        userRepository.save(user);

        // Tạo và gửi mã OTP qua email
        String otp = tokenService.createVerificationToken(user);
        String subject = "VNTech - Xác thực tài khoản của bạn";
        String message = "Xin chào " + user.getUsername() + ",\n\nMã OTP của bạn là: " + otp +
                "\nMã có hiệu lực trong 2 phút.\n\nCảm ơn bạn đã đăng ký tại VNTech.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return user;
    }

    // Xác thực đăng nhập người dùng
    public User authenticateUser(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + loginRequest.getEmail()));

        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản chưa được xác minh qua OTP!");
        }

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        return user;
    }
}
