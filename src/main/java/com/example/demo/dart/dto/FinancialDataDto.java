package com.example.demo.dart.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class FinancialDataDto {
    private String 사업연도;
    private String 분기;
    private List<IndicatorDto> 지표목록 = new ArrayList<>();

    @Data
    public static class IndicatorDto {
        private String 지표분류명;
        private String 지표명;
        private String 지표값;
    }
}
