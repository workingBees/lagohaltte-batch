package com.lagohaltte.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collation = "StockPrice")
public class StockPriceInfo {
    private Response response;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response{
        private Header header;
        private Body body;

        @Getter
        @Setter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }
        @Getter
        @Setter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private String numOfRows;
            private String pageNo;
            private String totalCount;
            private Items items;

            @Getter
            @Setter
            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Items {
                private List<Item> item;

                @Getter
                @Setter
                @ToString
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Item {
                    private String mrktTotAmt; //종가 * 상장주식수
                    private String mrktCtg; //주식시장 구분
                    private String clpr; // 종가
                    private String vs; // 전일대비 등락
                    private String fltRt; // 전일대비 등락에 따른 비율
                    private String mkp; //시초가
                    private String hipr; //하루중 고가
                    private String lopr; //하루중 저가
                    private String trqu; //체결수량의 누적합계
                    private String trPrc; //거래건별 체결 가격 * 체결수략의 누적합계
                    private String lstgStCnt; //상장주식수
                    private String basDt; //기준일자
                    private String srtnCd; //6자리 코드
                    private String isinCd; //국제인증고유코드
                    private String itmsNm; //유가증권국제인증고유코드이름
                }
            }
        }
    }
}
