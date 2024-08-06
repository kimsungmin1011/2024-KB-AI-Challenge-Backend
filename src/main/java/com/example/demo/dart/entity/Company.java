package com.example.demo.dart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Company {
    @Id
    private String coCode;
    private String coName;
    private double column1;

    // 동등성 비교를 위한 equals 메서드 재정의
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        return coCode.equals(company.coCode);
    }

    // 해시 코드를 위한 hashCode 메서드 재정의
    @Override
    public int hashCode() {
        return coCode.hashCode();
    }
}
