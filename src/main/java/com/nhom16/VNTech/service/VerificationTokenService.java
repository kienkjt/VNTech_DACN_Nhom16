package com.nhom16.VNTech.service;

import com.nhom16.VNTech.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface VerificationTokenService {
    String createVerificationToken(User user);
    boolean validateVerificationToken(String otp);
}
