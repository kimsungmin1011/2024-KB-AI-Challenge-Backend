package com.example.demo.dart.controller;

import com.example.demo.dart.entity.Corporation;
import com.example.demo.dart.service.CorporationService;
import com.example.demo.dart.service.ExternalApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corporation")
public class CorporationController {

    private final CorporationService corporationService;
    private final ExternalApiService externalApiService;

    @Autowired
    public CorporationController(CorporationService corporationService, ExternalApiService externalApiService) {
        this.corporationService = corporationService;
        this.externalApiService = externalApiService;
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
    public String getFinancialData(@RequestParam String companyName) {
        List<Corporation> companies = corporationService.searchCompanyByName(companyName);
        if (companies.isEmpty()) {
            return "회사를 찾을 수 없습니다";
        }
        Corporation company = companies.get(0); // 첫 번째 결과를 사용
        return externalApiService.getFinancialData(company);
    }

    @Operation(summary = "회사 코드로 재무 데이터 검색", description = "회사 코드로 재무 데이터를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색됨"),
            @ApiResponse(responseCode = "404", description = "회사를 찾을 수 없음")
    })
    @GetMapping("/financial-data/code")
    public String getFinancialDataByCode(@RequestParam String corpCode) {
        Corporation company = corporationService.findByCompanyCode(corpCode);
        if (company == null) {
            return "회사를 찾을 수 없습니다";
        }
        return externalApiService.getFinancialData(company);
    }
}
