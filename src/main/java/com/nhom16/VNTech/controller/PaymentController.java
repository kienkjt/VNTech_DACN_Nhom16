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

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> paymentReturn(HttpServletRequest request) {
        try {
            PaymentResultDto result = paymentService.processPaymentResult(request);

            boolean isSuccess = "00".equals(result.getRspCode());

            if (isSuccess) {
                // Cập nhật trạng thái thanh toán
                orderService.updateOrderPaymentStatus(result.getOrderId(), "PAID");
            }

            return ResponseEntity.ok(
                    new APIResponse<>(isSuccess, result.getMessage(), result)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    APIResponse.error("Lỗi xử lý kết quả thanh toán: " + e.getMessage())
            );
        }
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> paymentIPN(HttpServletRequest request) {
        try {
            PaymentResultDto result = paymentService.processPaymentResult(request);

            if ("00".equals(result.getRspCode())) {
                log.info("IPN: Payment successful for order: {}", result.getOrderId());
            } else {
                log.warn("IPN: Payment failed for order: {}", result.getOrderId());
            }

            // VNPAY yêu cầu format này
            return ResponseEntity.ok(Map.of(
                    "RspCode", "00",
                    "Message", "Confirm Success"
            ));

        } catch (Exception e) {
            log.error("IPN processing error: ", e);

            return ResponseEntity.ok(Map.of(
                    "RspCode", "99",
                    "Message", "Unknown error"
            ));
        }
    }
}
