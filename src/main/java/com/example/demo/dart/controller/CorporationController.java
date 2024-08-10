package com.example.demo.dart.controller;

import com.example.demo.dart.dto.FinancialDataDto;
import com.example.demo.dart.entity.Corporation;
import com.example.demo.dart.service.CorporationService;
import com.example.demo.dart.service.ExternalApiService;
import com.example.demo.dart.service.ChatGptService;
import com.example.demo.dart.service.CompanyInfoService;
import com.example.demo.dart.service.MajorEventsApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/corporation")
@CrossOrigin(origins = {"http://localhost:3000", "https://ziggle.kr"})
public class CorporationController {

    private final CorporationService corporationService;
    private final ExternalApiService externalApiService;
    private final ChatGptService chatGptService;
    private final CompanyInfoService companyInfoService;
    private final MajorEventsApiService majorEventsApiService;

    @Autowired
    public CorporationController(CorporationService corporationService, ExternalApiService externalApiService, ChatGptService chatGptService, CompanyInfoService companyInfoService, MajorEventsApiService majorEventsApiService) {
        this.corporationService = corporationService;
        this.externalApiService = externalApiService;
        this.chatGptService = chatGptService;
        this.companyInfoService = companyInfoService;
        this.majorEventsApiService = majorEventsApiService;
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
    public List<FinancialDataDto> getFinancialData(@RequestParam String companyName) {
        Corporation company = corporationService.findByExactCompanyName(companyName);
        if (company == null) {
            return List.of();
        }
        return externalApiService.getFinancialData(company);
    }

    @Operation(summary = "회사 코드로 재무 데이터 검색", description = "회사 코드로 재무 데이터를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/financial-data/code")
    public List<FinancialDataDto> getFinancialDataByCode(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return List.of();
        }
        return externalApiService.getFinancialData(company);
    }

    @Operation(summary = "회사 코드로 연도별 분석", description = "ChatGPT를 사용하여 연도별 분석을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 분석됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/risk-year-analysis")
    public Map<String, Object> analyzeYearOverYear(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return Map.of("message", "회사를 찾을 수 없습니다");
        }
        List<FinancialDataDto> financialData = externalApiService.getFinancialData(company);
        return chatGptService.analyzeQuarterOverQuarter(financialData);
    }

    @Operation(summary = "회사 코드로 리스크 등급 및 최종 의견 평가", description = "ChatGPT를 사용하여 리스크 등급과 최종 의견을 평가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 평가됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/risk-final-opinion")
    public Map<String, Object> analyzeRiskGradeFinalOpinion(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return Map.of("message", "회사를 찾을 수 없습니다");
        }
        List<FinancialDataDto> financialData = externalApiService.getFinancialData(company);
        Map<String, Object> majorEvents = majorEventsApiService.getMajorEvents(corpCode);
        return chatGptService.analyzeRiskGradeFinalOpinion(financialData, majorEvents);
    }

    @Operation(summary = "회사 코드로 기업 개황 조회", description = "회사 코드로 기업 개황 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/info")
    public Map<String, Object> getCompanyInfo(@RequestParam String corpCode) {
        return companyInfoService.getCompanyInfo(corpCode);
    }

    @Operation(summary = "회사 코드로 주요 사항 조회", description = "회사 코드로 부도발생, 영업정지 등 주요 사항을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/events")
    public Map<String, Object> getMajorEvents(@RequestParam String corpCode) {
        return majorEventsApiService.getMajorEvents(corpCode);
    }
}
