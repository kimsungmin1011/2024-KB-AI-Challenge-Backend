package com.example.demo.dart.service;

import com.example.demo.dart.dto.FinancialDataDto;
import com.example.demo.dart.dto.FinancialDataDto.IndicatorDto;
import com.example.demo.dart.entity.Corporation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://opendart.fss.or.kr/api/fnlttSinglIndx.json";
    private static final String API_KEY = "bd6bf828cd57aad05d2ee4f428224b652ea012bc";

    // 1분기, 반기, 3분기, 사업
    private List<String> reprtCodes = List.of("11013", "11012", "11014", "11011");
    // 연도
    private List<String> years = List.of("2022", "2023", "2024");
    // 수익성지표, 안정성지표
    private List<String> idxClCodes = List.of("M210000", "M220000");

    public List<FinancialDataDto> getFinancialData(Corporation company) {
        // 모든 결과를 저장할 맵 생성 (중복 방지)
        Map<String, FinancialDataDto> combinedResults = new HashMap<>();

        // 각 연도 및 분기별로 API 호출
        for (String year : years) {
            for (String reprtCode : reprtCodes) {
                for (String idxClCode : idxClCodes) {
                    URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                            .queryParam("crtfc_key", API_KEY)
                            .queryParam("corp_code", company.getCorpCode())
                            .queryParam("bsns_year", year)
                            .queryParam("reprt_code", reprtCode)
                            .queryParam("idx_cl_code", idxClCode)
                            .build()
                            .toUri();

                    // API 호출 결과를 맵으로 받음
                    Map<String, Object> result = restTemplate.getForObject(uri, HashMap.class);
                    // 연도-분기로 키 생성
                    String key = year + "-" + reprtCode;
                    // 키가 존재하지 않으면 새로운 FinancialDataDto 객체 생성 후 맵에 추가
                    combinedResults.computeIfAbsent(key, k -> {
                        FinancialDataDto dto = new FinancialDataDto();
                        dto.set사업연도(year);
                        dto.set분기(convertReportCode(reprtCode));
                        return dto;
                    });
                    // 기존 FinancialDataDto 객체에 지표목록 추가(연도-분기 key로 묶고 뒷부분에 데이터 다는 과정)
                    combinedResults.get(key).get지표목록().addAll(processResponse(result));
                }
            }
        }

        // 맵의 값을 리스트로 변환 후 정렬
        List<FinancialDataDto> sortedResults = new ArrayList<>(combinedResults.values());
        sortedResults.sort(Comparator.comparing(FinancialDataDto::get사업연도)
                .thenComparing(dto -> convertQuarterToOrder(dto.get분기())));

        return sortedResults;
    }

    // API 응답을 파싱해서 지표목록(IndicatorDto 객체)으로 변환함 (분류명, 지표명, 지표값 추출)
    private List<IndicatorDto> processResponse(Map<String, Object> result) {
        List<IndicatorDto> processedList = new ArrayList<>();

        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
        if (list != null) {
            for (Map<String, Object> item : list) {
                IndicatorDto indicatorDto = new IndicatorDto();
                indicatorDto.set지표분류명(item.get("idx_cl_nm").toString());
                indicatorDto.set지표명(item.get("idx_nm").toString());
                indicatorDto.set지표값(item.getOrDefault("idx_val", "").toString());
                processedList.add(indicatorDto);
            }
        }

        return processedList;
    }

    // 보고서 코드를 분기로 변환하는 메서드
    private String convertReportCode(String reprtCode) {
        switch (reprtCode) {
            case "11013":
                return "1분기";
            case "11012":
                return "반기";
            case "11014":
                return "3분기";
            case "11011":
                return "사업";
            default:
                return reprtCode;
        }
    }

    // 분기를 정렬 순서로 변환하는 메서드
    private int convertQuarterToOrder(String quarter) {
        switch (quarter) {
            case "1분기":
                return 1;
            case "반기":
                return 2;
            case "3분기":
                return 3;
            case "사업":
                return 4;
            default:
                return 0;
        }
    }
}
