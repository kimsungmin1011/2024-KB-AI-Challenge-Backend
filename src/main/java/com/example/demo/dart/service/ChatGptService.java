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

    public Map<String, Object> analyzeRiskGradeWithChatGPT(List<FinancialDataDto> financialData) {
        StringBuilder prompt = new StringBuilder("You are a financial statement analysis expert specializing in professional risk assessments for companies. Using the provided indicators, evaluate the company's risk level and assign a grade from A (lowest risk) to F (very high risk). Respond in Korean.\n" +
                "\n" +
                "1. Evaluate the company's key financial indicators.\n" +
                "2. Analyze profitability ratios and liquidity ratios, and compare them with those of the same period last year.\n" +
                "3. Based on these indicators, assess the overall financial soundness and stability of the company.\n" +
                "4. Assign a risk grade from A to F based on your analysis:\n" +
                "   - A = Very Low Risk\n" +
                "   - B = Low Risk\n" +
                "   - C = Medium Risk\n" +
                "   - D = High Risk\n" +
                "   - E = Very High Risk\n" +
                "   - F = Financial Distress\n" +
                "Respond in Korean.\n");  // 한국어로 응답 요청 추가
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

    public Map<String, Object> analyzeFinalOpinionWithChatGPT(List<FinancialDataDto> financialData) {
        StringBuilder prompt = new StringBuilder("You are a financial statement analysis expert specializing in professional risk assessments for companies. Based on the provided indicators, provide an overall final opinion on the company's financial condition and evaluate potential future risk factors. Respond in Korean.\n" +
                "\n" +
                "1. Summarize the key findings from the financial indicators.\n" +
                "2. Provide your final opinion on the overall financial condition.\n" +
                "3. Evaluate potential future risk factors for the company.\n" +
                "Respond in Korean.\n");  // 한국어로 응답 요청 추가
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
