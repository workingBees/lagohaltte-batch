package com.lagohaltte.step;

import com.lagohaltte.dto.StockPriceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallStockInfoOpenApi {
    @Value("${stockPriceInfoBaseURL}")
    private String baseURL;
    @Value("${publicDataServiceKey}")
    private String serviceKey;
    @Value("${apipath}")
    private String apiPath;

    public ResponseEntity<StockPriceInfo> requestPriceInfo(String stockName, String beginDate) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = UriComponentsBuilder.fromUriString(baseURL)
                .path(apiPath)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 750)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("itmsNm", StringUtils.trim(stockName))
                .queryParam("beginBasDt", beginDate)
                .build()
                .toUriString();

        return restTemplate.getForEntity(uri, StockPriceInfo.class);
    }
}
