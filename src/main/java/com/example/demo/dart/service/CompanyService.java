package com.example.demo.dart.service;

import com.example.demo.dart.entity.Company;
import com.example.demo.dart.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    // 회사 이름에 포함된 문자열로 회사 검색, 중복 제거
    public List<Company> searchCompanyByName(String coName) {
        List<Company> companies = companyRepository.findByCoNameContaining(coName);
        return companies.stream().distinct().collect(Collectors.toList());
    }

    // 회사 코드로 회사 검색
    public Company findByCompanyCode(String companyCode) {
        return companyRepository.findById(companyCode).orElse(null);
    }
}
