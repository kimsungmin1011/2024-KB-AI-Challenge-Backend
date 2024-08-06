package com.example.demo.dart.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "corporation")
@Data
public class Corporation {
    @Id
    private String corpCode;
    private String corpName;
    private String stockCode;
}
