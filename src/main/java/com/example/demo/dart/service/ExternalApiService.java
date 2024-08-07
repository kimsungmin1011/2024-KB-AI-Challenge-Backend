package com.example.demo.dart.service;

import com.example.demo.dart.entity.Corporation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://opendart.fss.or.kr/api/fnlttSinglIndx.json";

    private static final String API_KEY = "bd6bf828cd57aad05d2ee4f428224b652ea012bc";
    private static final String BSNS_YEAR = "2024";
    private static final String REPRT_CODE = "11013";
    private static final String IDX_CL_CODE = "M220000";

    public Map<String, Object> getFinancialData(Corporation company) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("crtfc_key", API_KEY)
                .queryParam("corp_code", company.getCorpCode())
                .queryParam("bsns_year", BSNS_YEAR)
                .queryParam("reprt_code", REPRT_CODE)
                .queryParam("idx_cl_code", IDX_CL_CODE)
                .build()
                .toUri();

        return restTemplate.getForObject(uri, HashMap.class);
    }
}
