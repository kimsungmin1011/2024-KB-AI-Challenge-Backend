package com.example.demo.dart.repository;

import com.example.demo.dart.entity.Corporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporationRepository extends JpaRepository<Corporation, String> {
    List<Corporation> findByCorpNameContaining(String corpName);
    Corporation findByCorpName(String corpName);
}
