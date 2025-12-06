package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.config.VnPayConfig;
import com.nhom16.VNTech.dto.payment.PaymentRequestDto;
import com.nhom16.VNTech.dto.payment.PaymentResponseDto;
import com.nhom16.VNTech.dto.payment.PaymentResultDto;
import com.nhom16.VNTech.entity.Order;
import com.nhom16.VNTech.entity.Payment;
import com.nhom16.VNTech.enums.OrderStatus;
import com.nhom16.VNTech.enums.PaymentMethod;
import com.nhom16.VNTech.enums.PaymentStatus;
import com.nhom16.VNTech.repository.OrderRepository;
import com.nhom16.VNTech.repository.PaymentRepository;
import com.nhom16.VNTech.service.PaymentService;
import com.nhom16.VNTech.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnPayServiceImpl implements PaymentService {

    private final VnPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest, HttpServletRequest request) {
        try {
            String vnp_TxnRef = VnPayUtil.getRandomNumber(8);
            String vnp_IpAddr = VnPayUtil.getIpAddress(request);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
            vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
            vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(paymentRequest.getAmount() * 100)); // Nhân 100 để chuyển sang đơn vị đồng
            vnp_Params.put("vnp_CurrCode", vnPayConfig.getCurrencyCode());

            if (paymentRequest.getBankCode() != null && !paymentRequest.getBankCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", paymentRequest.getBankCode());
            }

            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + paymentRequest.getOrderId());
            vnp_Params.put("vnp_OrderType", vnPayConfig.getOrderType());
            vnp_Params.put("vnp_Locale", paymentRequest.getLanguage());
            vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

           List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                   hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String vnp_SecureHash = VnPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + query.toString();

            log.info("Created VNPay payment URL for order: {}", paymentRequest.getOrderId());

            PaymentResponseDto response = new PaymentResponseDto();
            response.setCode("00");
            response.setMessage("Success");
            response.setPaymentUrl(paymentUrl);
            response.setTransactionId(vnp_TxnRef);

            return response;

        } catch (Exception e) {
            log.error("Error creating VNPay payment: ", e);

            PaymentResponseDto response = new PaymentResponseDto();
            response.setCode("99");
            response.setMessage("Lỗi hệ thống: " + e.getMessage());
            return response;
        }
    }

    @Override
    public PaymentResultDto processPaymentResult(HttpServletRequest request) {
        PaymentResultDto result = new PaymentResultDto();

        try {
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            String signValue = VnPayUtil.hashAllFields(fields, vnPayConfig.getHashSecret());

            if (signValue.equals(vnp_SecureHash)) {
                String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

                result.setRspCode(vnp_ResponseCode);
                result.setTransactionId(request.getParameter("vnp_TxnRef"));
                result.setTransactionNo(request.getParameter("vnp_TransactionNo"));
                result.setAmount(Long.parseLong(request.getParameter("vnp_Amount")) / 100);
                result.setBankCode(request.getParameter("vnp_BankCode"));
                result.setBankTranNo(request.getParameter("vnp_BankTranNo"));
                result.setCardType(request.getParameter("vnp_CardType"));
                result.setPayDate(request.getParameter("vnp_PayDate"));
                result.setOrderInfo(request.getParameter("vnp_OrderInfo"));

                // Extract orderId from orderInfo
                String orderInfo = request.getParameter("vnp_OrderInfo");
                if (orderInfo != null && orderInfo.contains(":")) {
                    try {
                        String orderIdStr = orderInfo.split(":")[1].trim();
                        result.setOrderId(Long.parseLong(orderIdStr));
                    } catch (Exception e) {
                        log.warn("Could not extract orderId from orderInfo: {}", orderInfo);
                    }
                }

                if ("00".equals(vnp_ResponseCode)) {
                    result.setMessage("Giao dịch thành công");
                    log.info("VNPay payment successful for transaction: {}", result.getTransactionId());
                } else {
                    result.setMessage("Giao dịch thất bại");
                    log.warn("VNPay payment failed for transaction: {}, response code: {}",
                            result.getTransactionId(), vnp_ResponseCode);
                }
            } else {
                result.setRspCode("97");
                result.setMessage("Invalid checksum");
                log.error("Invalid VNPay checksum for transaction: {}", request.getParameter("vnp_TxnRef"));
            }

        } catch (Exception e) {
            log.error("Error processing VNPay payment result: ", e);
            result.setRspCode("99");
            result.setMessage("Lỗi xử lý: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional
    public void createCodPayment(PaymentRequestDto paymentRequest) {
        try {
            Order order = orderRepository.findById(paymentRequest.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

            Payment payment = new Payment();
            payment.setOrders(order);
            payment.setPaymentMethod(PaymentMethod.COD);
            payment.setAmount(paymentRequest.getAmount().intValue());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId("COD_" + System.currentTimeMillis());
            payment.setNote("Thanh toán khi nhận hàng (COD)");

            paymentRepository.save(payment);

            order.changeStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            log.info("Created COD payment for order: {}", order.getId());

        } catch (Exception e) {
            log.error("Error creating COD payment: ", e);
            throw new RuntimeException("Lỗi tạo thanh toán COD: " + e.getMessage());
        }
    }
}