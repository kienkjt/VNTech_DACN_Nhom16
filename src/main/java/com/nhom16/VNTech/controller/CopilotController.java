package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.service.OllamaService;
import com.nhom16.VNTech.service.ai.AiDataService;
import com.nhom16.VNTech.service.ai.AiFormatter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/copilot")
@CrossOrigin
public class CopilotController {

    private final OllamaService ollamaService;
    private final AiDataService aiDataService;
    private final AiFormatter aiFormatter;

    public CopilotController(
            OllamaService ollamaService,
            AiDataService aiDataService,
            AiFormatter aiFormatter
    ) {
        this.ollamaService = ollamaService;
        this.aiDataService = aiDataService;
        this.aiFormatter = aiFormatter;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, Object> body) {

        Object messagesObj = body.get("messages");
        if (!(messagesObj instanceof List<?> messages) || messages.isEmpty()) {
            return Map.of("content", "Không nhận được câu hỏi");
        }

        Map<?, ?> last = (Map<?, ?>) messages.get(messages.size() - 1);
        String userQuestion = String.valueOf(last.get("content"));

        Long userId = null; // hoặc lấy từ JWT / session
        String contextJson = aiDataService.getCustomerContextJson(userId);

        String ragPrompt = aiFormatter.buildRagPrompt(contextJson, userQuestion);

        String aiReply = ollamaService.chat(ragPrompt);

        return Map.of("content", aiReply);
    }
}

