package com.example.demo.dart.service;

import com.example.demo.dart.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getFinancialData(Company company) {
        String url = "https://opendart.fss.or.kr/api/fnlttSinglIndx.json?crtfc_key=bd6bf828cd57aad05d2ee4f428224b652ea012bc&corp_code="
                + company.getCoCode()
                + "&bsns_year=2024&reprt_code=11013&idx_cl_code=M210000";

        return restTemplate.getForObject(url, String.class);
    }
}
