package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.payment.PaymentRequestDto;
import com.nhom16.VNTech.dto.payment.PaymentResponseDto;
import com.nhom16.VNTech.dto.payment.PaymentResultDto;
import com.nhom16.VNTech.enums.PaymentMethod;
import com.nhom16.VNTech.service.OrderService;
import com.nhom16.VNTech.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    // Tạo yêu cầu thanh toán
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @Valid @RequestBody PaymentRequestDto paymentRequest,
            HttpServletRequest request) {
        try {
            if (paymentRequest.getPaymentMethod() == null) {
                return ResponseEntity.badRequest().body(
                        APIResponse.error("Vui lòng chọn phương thức thanh toán")
                );
            }
            PaymentResponseDto response;

            if (paymentRequest.getPaymentMethod() == PaymentMethod.COD) {
                paymentService.createCodPayment(paymentRequest);

                response = new PaymentResponseDto();
                response.setCode("00");
                response.setMessage("Đã chọn phương thức COD. Vui lòng thanh toán khi nhận hàng.");
                response.setPaymentUrl(null);
                response.setTransactionId("COD_" + System.currentTimeMillis());

            } else {
                response = paymentService.createPayment(paymentRequest, request);
            }
            return ResponseEntity.ok(
                    APIResponse.success(response, "Tạo yêu cầu thanh toán thành công")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    APIResponse.error("Lỗi khi tạo thanh toán: " + e.getMessage())
            );
        }
    }
    // Xác nhận đơn hàng COD
    @PostMapping("/cod/{orderId}/confirm")
    public ResponseEntity<?> confirmCodOrder(@PathVariable Long orderId) {
        try {
            orderService.updateOrderPaymentStatus(orderId, "PENDING");
            return ResponseEntity.ok(
                    APIResponse.success(null, "Xác nhận đơn hàng COD thành công")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    APIResponse.error("Lỗi xác nhận COD: " + e.getMessage())
            );
        }
    }
    // Xử lý kết quả thanh toán từ VNPAY
    @PostMapping("/vnpay/ipn")
    public ResponseEntity<PaymentResultDto> vnpayIpnCallback(HttpServletRequest request) {
        PaymentResultDto result = paymentService.processIpnCallback(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
        PaymentResultDto result = paymentService.processPaymentResult(request);

        // Redirect về frontend
        try {
            String status = result.getRspCode() != null ? java.net.URLEncoder.encode(result.getRspCode(), java.nio.charset.StandardCharsets.UTF_8.toString()) : "";
            String message = result.getMessage() != null ? java.net.URLEncoder.encode(result.getMessage(), java.nio.charset.StandardCharsets.UTF_8.toString()) : "";
            String orderId = result.getOrderId() != null ? String.valueOf(result.getOrderId()) : "";

            String redirectUrl = String.format(
                    "http://localhost:3000/payment/result?status=%s&message=%s&orderId=%s",
                    status,
                    message,
                    orderId
            );

            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            log.error("Error building redirect URL", e);
            return ResponseEntity.status(500).body("Lỗi tạo redirect URL");
        }
    }
}
