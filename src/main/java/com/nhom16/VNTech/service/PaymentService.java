package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.payment.PaymentRequestDto;
import com.nhom16.VNTech.dto.payment.PaymentResponseDto;
import com.nhom16.VNTech.dto.payment.PaymentResultDto;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequest, HttpServletRequest request);
    PaymentResultDto processPaymentResult(HttpServletRequest request);
    PaymentResultDto processIpnCallback(HttpServletRequest request);
    void createCodPayment(PaymentRequestDto paymentRequest);
}