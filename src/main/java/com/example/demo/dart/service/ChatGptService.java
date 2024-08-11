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

    public Map<String, Object> analyzeQuarterOverQuarter(List<FinancialDataDto> financialData) {
        StringBuilder prompt = new StringBuilder(
                "You are a financial statement analysis expert specializing in professional risk assessments for companies. Below is the financial data for a company across different quarters. Analyze this data and provide a Quarter-over-Quarter analysis comparing the company's financial performance.\n\n" +
                        "**Context:** The data includes various financial indicators such as profitability and stability metrics. Use the given data to highlight trends, strengths, or potential risks.\n\n" +
                        "**Task:** Start with a summary of key findings from a risk assessment perspective, followed by a detailed analysis presented in a markdown table. If the data is missing or cannot be interpreted, clearly state that you cannot provide an answer. Do not create or hallucinate data. If data is insufficient, clearly state in Korean that you cannot provide an answer due to lack of data. Adjust the table headers accordingly if specific quarter data is missing. Respond in Korean and use markdown format.\n\n" +
                        "**Response format:**\n" +
                        "1. Summary of key findings (in Korean).\n" +
                        "2. A markdown table with the analysis.\n\n" +
                        "Here is the data:\n"
        );

        // 각 재무 데이터를 표 형태로 추가
        for (FinancialDataDto data : financialData) {
            String year = data.get사업연도() != null ? data.get사업연도() : "";
            String quarter = data.get분기() != null ? data.get분기() : "";

            for (FinancialDataDto.IndicatorDto indicator : data.get지표목록()) {
                prompt.append("| ").append(year)
                        .append(" | ").append(quarter)
                        .append(" | ").append(indicator.get지표분류명())
                        .append(" | ").append(indicator.get지표명())
                        .append(" | ").append(indicator.get지표값())
                        .append(" |\n");
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt.toString())));
        requestBody.put("max_tokens", 1500); // 간결한 응답 유도

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + CHATGPT_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(CHATGPT_API_URL, request, Map.class);
    }

    public Map<String, Object> analyzeRiskGradeFinalOpinion(List<FinancialDataDto> financialData, Map<String, Object> majorEvents) {
        StringBuilder prompt = new StringBuilder(
                "You are a financial statement analysis expert specializing in professional risk assessments for companies. Below is the financial data for a company, including key indicators and major events. Based on this data, assign a risk grade from A (lowest risk) to F (very high risk) and provide a final opinion on the company's financial condition.\n\n" +
                        "**Context:** The data includes financial indicators such as profitability, stability metrics, and significant events that may impact the company's risk level.\n\n" +
                        "**Task:**\n" +
                        "1. Start your response with the risk grade in large font.\n" +
                        "2. Provide a detailed final opinion on the company's financial condition, considering the major events.\n" +
                        "3. If any data is missing or cannot be interpreted, clearly state that you cannot provide an answer. Do not create or hallucinate any data. If data is insufficient, clearly state in Korean that you cannot provide an answer due to lack of data.\n\n" +
                        "**Response format:**\n" +
                        "1. Risk grade (in large font, in Korean).\n" +
                        "2. Final opinion (in markdown format, in Korean).\n\n" +
                        "Here is the data:\n"
        );

        prompt.append("### 주요 사항:\n");
        majorEvents.forEach((key, value) -> {
            prompt.append("- ").append(key).append(": ").append(value.toString()).append("\n");
        });

        for (FinancialDataDto data : financialData) {
            String year = data.get사업연도() != null ? data.get사업연도() : "";
            String quarter = data.get분기() != null ? data.get분기() : "";

            prompt.append("\n년도: ").append(year).append(", 분기: ").append(quarter).append("\n");
            for (FinancialDataDto.IndicatorDto indicator : data.get지표목록()) {
                prompt.append("- ").append(indicator.get지표명()).append(": ").append(indicator.get지표값()).append("\n");
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("temperature", 0);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt.toString())));
        requestBody.put("max_tokens", 4096); // 최대 토큰

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + CHATGPT_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(CHATGPT_API_URL, request, Map.class);
    }
}
