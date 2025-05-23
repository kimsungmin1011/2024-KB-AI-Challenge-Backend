package com.example.demo.dart.service;

import com.example.demo.dart.entity.Corporation;
import com.example.demo.dart.repository.CorporationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorporationService {
    @Autowired
    private CorporationRepository corporationRepository;

    public List<Corporation> searchCompanyByName(String corpName) {
        List<Corporation> companies = corporationRepository.findByCorpNameContainingOrderByStockCodeDesc(corpName);
        return companies.stream().distinct().collect(Collectors.toList());
    }

    public Corporation findByExactCompanyName(String corpName) {
        return corporationRepository.findFirstByCorpName(corpName);
    }

    public Corporation findByCompanyCode(String corpCode) {
        return corporationRepository.findById(corpCode).orElse(null);
    }
}
