package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.entity.VerificationToken;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.repository.VerificationTokenRepository;
import com.nhom16.VNTech.service.VerificationTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    // Tạo OTP mới
    @Transactional
    @Override
    public String createVerificationToken(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(2);

        // Kiểm tra nếu đã có token
        Optional<VerificationToken> existing = tokenRepository.findByUserId(user.getId());
        VerificationToken token;
        if (existing.isPresent()) {
            token = existing.get();
            token.setToken(otp);
            token.setExpiryDate(expiryDate);
        } else {
            token = new VerificationToken(otp, user, expiryDate);
        }

        tokenRepository.save(token);
        return otp;
    }


    // Xác thực mã OTP
    @Override
    public boolean validateVerificationToken(String otp) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);
        if (tokenOpt.isEmpty()) return false;

        VerificationToken token = tokenOpt.get();

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return false;
        }

        User user = token.getUser();
        user.setVerified(true);
        userRepository.save(user); // lưu lại trạng thái xác thực

        tokenRepository.delete(token);
        return true;
    }
}
