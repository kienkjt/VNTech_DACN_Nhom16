package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment-methods")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentMethodController {

    @GetMapping("")
    public ResponseEntity<APIResponse<List<PaymentMethodDto>>> getPaymentMethods() {
        List<PaymentMethodDto> methods = Arrays.stream(PaymentMethod.values())
                .map(method -> new PaymentMethodDto(
                        method.getCode(),
                        method.getDescription(),
                        getPaymentMethodNote(method)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success(methods, "Lấy danh sách phương thức thanh toán thành công"));
    }

    private String getPaymentMethodNote(PaymentMethod method) {
        switch (method) {
            case COD:
                return "Thanh toán khi nhận hàng";
            case VNPAY:
                return "Thanh toán online qua VNPay";
            default:
                return "";
        }
    }

    @Data
    @AllArgsConstructor
    private static class PaymentMethodDto {
        private String code;
        private String name;
        private String note;
    }
}

