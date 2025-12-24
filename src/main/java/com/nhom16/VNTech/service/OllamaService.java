package com.nhom16.VNTech.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String prompt) {

        Map<String, Object> request = Map.of(
                "model", "gemma:2b",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                Bạn là trợ lý AI phân tích dữ liệu cho hệ thống thương mại điện tử VNTech.
                                QUY TẮC:
                                - Chỉ trả lời bằng tiếng Việt
                                - Không bịa
                                - Chỉ dựa trên dữ liệu được cung cấp
                                - Nếu thiếu dữ liệu, nói rõ
                                - Trả lời ngắn gọn, ưu tiên bullet point
                                """
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);

        Map res = restTemplate.postForObject(
                "http://localhost:11434/api/chat",
                entity,
                Map.class
        );

        Map message = (Map) res.get("message");
        return message.get("content").toString();
    }
}
