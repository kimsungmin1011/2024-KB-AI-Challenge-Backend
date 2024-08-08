package com.example.demo.dart.controller;

import com.example.demo.dart.entity.Corporation;
import com.example.demo.dart.service.CorporationService;
import com.example.demo.dart.service.ExternalApiService;
import com.example.demo.dart.service.ChatGptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/corporation")
public class CorporationController {

    private final CorporationService corporationService;
    private final ExternalApiService externalApiService;
    private final ChatGptService chatGptService;

    @Autowired
    public CorporationController(CorporationService corporationService, ExternalApiService externalApiService, ChatGptService chatGptService) {
        this.corporationService = corporationService;
        this.externalApiService = externalApiService;
        this.chatGptService = chatGptService;
    }

    @Operation(summary = "회사 이름으로 검색", description = "회사 이름에 포함된 문자열로 회사를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/search")
    public List<Corporation> searchCompany(@RequestParam String name) {
        return corporationService.searchCompanyByName(name);
    }

    @Operation(summary = "회사의 재무 데이터 검색", description = "회사 이름으로 재무 데이터를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/financial-data")
    public Map<String, Object> getFinancialData(@RequestParam String companyName) {
        Corporation company = corporationService.findByExactCompanyName(companyName);
        if (company == null) {
            return Map.of("message", "회사를 찾을 수 없습니다");
        }
        return externalApiService.getFinancialData(company);
    }

    @Operation(summary = "회사 코드로 재무 데이터 검색", description = "회사 코드로 재무 데이터를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/financial-data/code")
    public Map<String, Object> getFinancialDataByCode(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return Map.of("message", "회사를 찾을 수 없습니다");
        }
        return externalApiService.getFinancialData(company);
    }

    @Operation(summary = "회사 코드로 위험도 평가", description = "회사 코드로 ChatGPT를 사용하여 위험도를 평가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 평가됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/risk-assessment")
    public Map<String, Object> getRiskAssessment(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return Map.of("message", "회사를 찾을 수 없습니다");
        }
        Map<String, Object> financialData = externalApiService.getFinancialData(company);
        return chatGptService.analyzeFinancialDataWithChatGPT(financialData);
    }
}
