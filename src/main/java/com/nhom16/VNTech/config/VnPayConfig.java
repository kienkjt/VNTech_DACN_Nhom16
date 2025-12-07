package com.nhom16.VNTech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VnPayConfig {
    private String paymentUrl;
    private String returnUrl;
    private String tmnCode;
    private String hashSecret;
    private String version;
    private String command;
    private String orderType;
    private String locale;
    private String currencyCode;
}