package com.nhom16.VNTech.service.ai;

import org.springframework.stereotype.Component;

@Component
public class AiFormatter {

    public String buildRagPrompt(String contextJson, String question) {
        return """
        Bạn là trợ lý AI cho hệ thống VNTech.

        ===== DỮ LIỆU HỆ THỐNG =====
        %s

        ===== CÂU HỎI =====
        %s

        Hãy phân tích dữ liệu và đưa ra câu trả lời phù hợp.
        """.formatted(contextJson, question);
    }
}
