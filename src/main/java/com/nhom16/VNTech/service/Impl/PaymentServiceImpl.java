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
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final VnPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest, HttpServletRequest request) {
        try {
            // Kiểm tra đơn hàng
            Order order = orderRepository.findById(paymentRequest.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("Đơn hàng đã được xử lý, không thể thanh toán");
            }

            String vnp_TxnRef = VnPayUtil.getRandomNumber(8);
            String vnp_IpAddr = VnPayUtil.getIpAddress(request);

            Map<String, String> vnp_Params = new TreeMap<>(); // Dùng TreeMap để tự động sort
            vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
            vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
            vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(paymentRequest.getAmount() * 100)); // Nhân 100
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

            // Build query string và hash data
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    query.append('&');
                    hashData.append('&');
                }
            }

            // Remove last '&'
            if (query.length() > 0) {
                query.deleteCharAt(query.length() - 1);
                hashData.deleteCharAt(hashData.length() - 1);
            }

            // Tạo secure hash
            String vnp_SecureHash = VnPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + query.toString();

            log.info("Created VNPay payment URL for order: {}, transaction: {}", paymentRequest.getOrderId(), vnp_TxnRef);

            // Cập nhật transaction ID vào payment
            Payment payment = paymentRepository.findByOrdersId(order.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán"));

            payment.setTransactionId(vnp_TxnRef);
            payment.setPaymentUrl(paymentUrl);
            paymentRepository.save(payment);

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
    @Transactional
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

            log.info("Received VNPay callback with params: {}", fields);

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            // Sắp xếp các field theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = fields.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
            }

            String signValue = VnPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());

            if (signValue.equals(vnp_SecureHash)) {
                String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");

                result.setRspCode(vnp_ResponseCode);
                result.setTransactionId(vnp_TxnRef);
                result.setTransactionNo(request.getParameter("vnp_TransactionNo"));

                try {
                    String amountStr = request.getParameter("vnp_Amount");
                    if (amountStr != null && !amountStr.isEmpty()) {
                        result.setAmount(Long.parseLong(amountStr) / 100);
                    }
                } catch (NumberFormatException e) {
                    log.warn("Cannot parse amount: {}", request.getParameter("vnp_Amount"));
                }

                result.setBankCode(request.getParameter("vnp_BankCode"));
                result.setBankTranNo(request.getParameter("vnp_BankTranNo"));
                result.setCardType(request.getParameter("vnp_CardType"));
                result.setPayDate(request.getParameter("vnp_PayDate"));
                result.setOrderInfo(request.getParameter("vnp_OrderInfo"));

                // Extract orderId from orderInfo
                String orderInfo = request.getParameter("vnp_OrderInfo");
                if (orderInfo != null && orderInfo.contains(":")) {
                    try {
                        String[] parts = orderInfo.split(":");
                        if (parts.length > 1) {
                            String orderIdStr = parts[1].trim();
                            result.setOrderId(Long.parseLong(orderIdStr));
                        }
                    } catch (Exception e) {
                        log.warn("Could not extract orderId from orderInfo: {}", orderInfo);
                    }
                }

                if ("00".equals(vnp_ResponseCode)) {
                    result.setMessage("Giao dịch thành công");
                    log.info("VNPay payment successful for transaction: {}", vnp_TxnRef);

                    // CẬP NHẬT TRẠNG THÁI THANH TOÁN VÀ ĐƠN HÀNG
                    try {
                        Payment payment = paymentRepository.findByTransactionId(vnp_TxnRef)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch: " + vnp_TxnRef));

                        // Kiểm tra xem đã thanh toán chưa
                        if (payment.getStatus() != PaymentStatus.PAID) {
                            payment.setStatus(PaymentStatus.PAID);
                            payment.setPaidAt(LocalDateTime.now());
                            payment.setBankCode(request.getParameter("vnp_BankCode"));
                            payment.setBankTransactionNo(request.getParameter("vnp_BankTranNo"));
                            payment.setCardType(request.getParameter("vnp_CardType"));
                            payment.setPayDate(request.getParameter("vnp_PayDate"));
                            paymentRepository.save(payment);

                            // Cập nhật trạng thái đơn hàng
                            Order order = payment.getOrders();
                            if (order.getStatus() == OrderStatus.PENDING) {
                                order.changeStatus(OrderStatus.CONFIRMED);
                                orderRepository.save(order);
                                log.info("Updated order {} to CONFIRMED status", order.getId());
                            }
                        } else {
                            log.info("Payment already marked as PAID for transaction: {}", vnp_TxnRef);
                        }

                    } catch (Exception e) {
                        log.error("Error updating payment status: ", e);
                        result.setRspCode("98");
                        result.setMessage("Giao dịch thành công nhưng cập nhật trạng thái lỗi");
                    }

                } else {
                    result.setMessage("Giao dịch thất bại");
                    log.warn("VNPay payment failed for transaction: {}, response code: {}",
                            vnp_TxnRef, vnp_ResponseCode);

                    // Cập nhật trạng thái thất bại
                    try {
                        paymentRepository.findByTransactionId(vnp_TxnRef).ifPresent(payment -> {
                            payment.setStatus(PaymentStatus.FAILED);
                            paymentRepository.save(payment);
                        });
                    } catch (Exception e) {
                        log.error("Error updating failed payment status: ", e);
                    }
                }
            } else {
                result.setRspCode("97");
                result.setMessage("Invalid checksum");
                log.error("Invalid VNPay checksum for transaction: {}, expected: {}, actual: {}",
                        request.getParameter("vnp_TxnRef"), signValue, vnp_SecureHash);
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

            // Kiểm tra xem đã có payment chưa
            Payment payment = paymentRepository.findByOrdersId(order.getId())
                    .orElse(new Payment());

            payment.setOrders(order);
            payment.setPaymentMethod(PaymentMethod.COD);
            payment.setAmount(paymentRequest.getAmount().intValue());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId("COD_" + System.currentTimeMillis());
            payment.setNote("Thanh toán khi nhận hàng (COD)");

            paymentRepository.save(payment);

            log.info("Created COD payment for order: {}", order.getId());

        } catch (Exception e) {
            log.error("Error creating COD payment: ", e);
            throw new RuntimeException("Lỗi tạo thanh toán COD: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResultDto processIpnCallback(HttpServletRequest request) {
        // Phương thức này dành cho IPN (Instant Payment Notification)
        // VNPay gửi request này để xác nhận thanh toán độc lập
        return processPaymentResult(request);
    }
}