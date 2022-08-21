package com.lagohaltte.step;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

@Service
@NoArgsConstructor
@RequiredArgsConstructor
public class CrawlingFinance {

    @Value("${stockPriceInfoBaseURL}")
    String baseURL;

    @Value("${publicDataServiceKey}")
    String serviceKey;
    String apiPath ="/getStockPriceInfo";
    StringBuilder stockURLBuilder;

    public void reqeustPriceInfo(String item) throws IOException {

        //1. 오픈 api의 요첨 규격에 맞는 파라미터 생성
        stockURLBuilder.append(baseURL);
        stockURLBuilder.append(apiPath);
        stockURLBuilder.append("?"+ URLEncoder.encode("serviceKey","UTF-8")+"="+ serviceKey);
        stockURLBuilder.append("&"+ URLEncoder.encode("numOfRows","UTF-8")+"+"+ URLEncoder.encode("1000","UTF-8"));
        stockURLBuilder.append("&"+URLEncoder.encode("pageNo","UTF-8")+"="+ URLEncoder.encode("1","UTF-8"));
        stockURLBuilder.append("&"+URLEncoder.encode("resultType","UTF-8")+"="+URLEncoder.encode("json","UTF-8"));
        stockURLBuilder.append("&"+URLEncoder.encode("itmsNm","UTF-8") +"=" + URLEncoder.encode(item,"UTF-8"));

        //2. url 객체 생성.
        URL url = new URL(stockURLBuilder.toString());

        //3. 요청하고자하는 url과 통신하기 위한 connection 객체 생성
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //4. 통신을 위한 메모스 GET
        conn.setRequestMethod("GET");

        //5. 통신을 위한 content-type 설정
        conn.setRequestProperty("Content-type", "application/json");


    }
}
