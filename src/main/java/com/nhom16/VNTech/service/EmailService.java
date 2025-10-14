package com.nhom16.VNTech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
