package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.LoginRequestDto;
import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.entity.VerificationToken;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.repository.VerificationTokenRepository;
import com.nhom16.VNTech.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private UserRepository userRepository;
    @Qualifier("verificationTokenServiceImpl")
    @Autowired private VerificationTokenService tokenService;
    @Autowired private VerificationTokenRepository tokenRepository;
    @Autowired private EmailService emailService;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User register(UserRegistrationDto userDto) {
        Optional<User> existingUserOpt = userRepository.findByEmail(userDto.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // Nếu user đã xác thực
            if (existingUser.isVerified()) {
                throw new RuntimeException("Email đã được đăng ký!");
            }
            // Nếu user chưa xác thực => gửi lại OTP
            String otp = tokenService.createVerificationToken(existingUser);
            String subject = "VNTech - Xác thực lại tài khoản của bạn";
            String message = "Xin chào " + existingUser.getUsername() + ",\n\nMã OTP của bạn là: " + otp +
                    "\nMã có hiệu lực trong 2 phút.\n\nCảm ơn bạn đã đăng ký tại VNTech.";
            emailService.sendEmail(existingUser.getEmail(), subject, message);
            return existingUser;
        }

        // Nếu email chưa tồn tại
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername() != null ? userDto.getUsername() : userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Gửi OTP xác thực
        String otp = tokenService.createVerificationToken(user);
        String subject = "VNTech - Xác thực tài khoản của bạn";
        String message = "Xin chào " + user.getUsername() + ",\n\nMã OTP của bạn là: " + otp +
                "\nMã có hiệu lực trong 2 phút.\n\nCảm ơn bạn đã đăng ký tại VNTech.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return user;
    }

    @Override
    public User authenticateUser(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + loginRequest.getEmail()));

        if (!user.isVerified()) {
            throw new RuntimeException("Tài khoản chưa được xác minh qua OTP!");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        return user;
    }

    @Override
    public boolean verifyOtp(String otp) {
        return tokenService.validateVerificationToken(otp);
    }

    @Override
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) throw new RuntimeException("Email không tồn tại!");

        User user = userOpt.get();
        String otp = tokenService.createVerificationToken(user);

        String subject = "VNTech - Xác nhận quên mật khẩu";
        String message = "Xin chào " + user.getUsername() + ",\n\nMã OTP đặt lại mật khẩu của bạn là: " + otp +
                "\nMã có hiệu lực trong 2 phút.\n\nNếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.";

        emailService.sendEmail(user.getEmail(), subject, message);
    }

    @Override
    public boolean resetPassword(String otp, String newPassword) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);
        if (tokenOpt.isEmpty()) return false;

        VerificationToken vt = tokenOpt.get();
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(vt);
            return false;
        }

        User user = vt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(vt);
        return true;
    }
    @Override
    public boolean validateResetOtp(String otp) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);
        if (tokenOpt.isEmpty()) return false;

        VerificationToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return false;
        }
        return true; // chỉ xác nhận hợp lệ, không set verified
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
