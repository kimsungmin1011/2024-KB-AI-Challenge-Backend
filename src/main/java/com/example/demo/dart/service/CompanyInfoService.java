package com.example.demo.dart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class CompanyInfoService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://opendart.fss.or.kr/api/company.json";
    private static final String API_KEY = "bd6bf828cd57aad05d2ee4f428224b652ea012bc";

    public Map<String, Object> getCompanyInfo(String corpCode) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("crtfc_key", API_KEY)
                .queryParam("corp_code", corpCode)
                .build()
                .toUri();

        return restTemplate.getForObject(uri, Map.class);
    }
}
