package com.example.demo.dart.service;

import com.example.demo.dart.entity.Corporation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    public String getFinancialData(Corporation company) {
        String apiUrl = "https://external-api.example.com/financial-data?code=" + company.getCorpCode();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
