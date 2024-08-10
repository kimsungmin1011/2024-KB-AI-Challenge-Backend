package com.example.demo.dart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class MajorEventsApiService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_KEY = "bd6bf828cd57aad05d2ee4f428224b652ea012bc";
    private static final String BEGIN_DATE = "20150101";
    private static final String END_DATE = "20241231";

    private static final Map<String, String> API_URLS = new HashMap<>() {{
        put("부도발생", "https://opendart.fss.or.kr/api/dfOcr.json");
        put("영업정지", "https://opendart.fss.or.kr/api/bsnSp.json");
        put("회생절차개시신청", "https://opendart.fss.or.kr/api/ctrcvsBgrq.json");
        put("해산사유발생", "https://opendart.fss.or.kr/api/dsRsOcr.json");
        put("유상증자결정", "https://opendart.fss.or.kr/api/piicDecsn.json");
        put("감자결정", "https://opendart.fss.or.kr/api/crDecsn.json");
        put("소송제기", "https://opendart.fss.or.kr/api/lwstLg.json");
        put("영업양도결정", "https://opendart.fss.or.kr/api/bsnTrfDecsn.json");
        put("영업양수결정", "https://opendart.fss.or.kr/api/bsnInhDecsn.json");
        put("회사분할결정", "https://opendart.fss.or.kr/api/cmpDvDecsn.json");
        put("회사합병결정", "https://opendart.fss.or.kr/api/cmpMgDecsn.json");
    }};

    public Map<String, Object> getMajorEvents(String corpCode) {
        Map<String, Object> results = new HashMap<>();

        for (Map.Entry<String, String> entry : API_URLS.entrySet()) {
            String eventName = entry.getKey();
            String apiUrl = entry.getValue();

            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("crtfc_key", API_KEY)
                    .queryParam("corp_code", corpCode)
                    .queryParam("bgn_de", BEGIN_DATE)
                    .queryParam("end_de", END_DATE)
                    .build()
                    .toUri();

            Map<String, Object> response = restTemplate.getForObject(uri, HashMap.class);
            results.put(eventName, response);
        }

        return results;
    }
}
