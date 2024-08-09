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
        StringBuilder prompt = new StringBuilder("You are a financial expert analyst. Your task is to analyze the company's financial data for multiple fiscal years and quarters provided at the end of this prompt. Based on this data, assess the company's financial health and assign a risk level grade from A to F for each fiscal year, with the corporate risk rating in the first part of the response based on the current 2024 standard. The grading should consider factors such as liquidity, profitability, leverage, efficiency, and relevant market conditions.\n" +
                "\n" +
                "Output format:\n" +
                "\n" +
                "Risk Level Grade: Assign an overall grade (A-F) for the company's risk level, based on the current 2024 standard.\n" +
                "\n" +
                "Reasoning: Provide a detailed explanation for the assigned grade. This should include:\n" +
                "\n" +
                "Liquidity Analysis: Evaluate the company's liquidity using indicators such as the Current Ratio, Quick Ratio, etc.\n" +
                "Profitability Analysis: Analyze profitability metrics if available, such as ROA, ROE, or Net Profit Margin.\n" +
                "Leverage Analysis: Assess the company's leverage by examining the Debt Ratio, Financial Leverage, and Interest Coverage Ratio.\n" +
                "Efficiency Analysis: Analyze the efficiency of the company using ratios such as Inventory Turnover, Accounts Receivable Turnover, etc.\n" +
                "Market Situation & External Factors: Consider any market conditions or external factors that could affect the company's financial stability.\n" +
                "Comparative Analysis: Compare the results with the same period from the previous year to identify trends or significant changes.\n" +
                "Comparative Summary: Summarize how the company’s financial health has changed compared to the previous year(s), noting any improvements, deteriorations, or consistent patterns.\n" +
                "\n" +
                "Translation: After completing the analysis, translate the entire response into Korean.\n" +
                "\n" +
                "Response Language: Do not return the response in English. Only return the translated version in Korean.\n");
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
