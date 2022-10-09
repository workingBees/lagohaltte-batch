package com.lagohaltte.step;

import com.lagohaltte.domain.StockPriceInfo;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CallStockInfoOpenApi {
    @Value("${stockPriceInfoBaseURL}")
    private String baseURL ;
    @Value("${publicDataServiceKey}")
    private String serviceKey ;
    private final WebClient webClient = WebClient.create();
    public ResponseEntity<StockPriceInfo> requestPriceInfo(String stockName) throws IOException {
        //RestTemplate 이걸로 변경
        RestTemplate restTemplate = new RestTemplate();
        String apiPath = "/getStockPriceInfo";
        String uri = UriComponentsBuilder.fromUriString(baseURL)
                .path(apiPath)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 700)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("itmsNm", StringUtils.trim(stockName))
                .build()
                .toUriString();
        return restTemplate.getForEntity(uri, StockPriceInfo.class);


//        return webClient.get()
//                .uri(uri)
//                .retrieve()
//                .bodyToMono(StockPriceInfo.class);
    }
}
