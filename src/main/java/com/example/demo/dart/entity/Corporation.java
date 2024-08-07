package com.example.demo.dart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Corporation {
    @Id
    private String corpCode;
    private String corpName;
    private String stockCode;
}
