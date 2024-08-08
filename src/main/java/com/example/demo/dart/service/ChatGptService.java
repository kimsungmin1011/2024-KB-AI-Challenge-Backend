package com.example.demo.dart.service;

import com.example.demo.dart.dto.FinancialDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGptService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CHATGPT_API_KEY = "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";
    private static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";

    public Map<String, Object> analyzeFinancialDataWithChatGPT(List<FinancialDataDto> financialData) {
        StringBuilder prompt = new StringBuilder("다음 재무 데이터를 전년 동기비(QoQ)로 비교분석하고, A~F 등급으로 회사의 위험 수준을 평가하시오:\n");
        for (FinancialDataDto data : financialData) {
            prompt.append("년도: ").append(data.get사업연도()).append(", 분기: ").append(data.get분기()).append("\n");
            for (FinancialDataDto.IndicatorDto indicator : data.get지표목록()) {
                prompt.append(indicator.toString()).append("\n");
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt.toString())));
        requestBody.put("max_tokens", 4096);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + CHATGPT_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(CHATGPT_API_URL, request, Map.class);
    }
}
