package com.lagohaltte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "FinanceInfo")
public class FinanceInfoDto {
    @Id
    private String _id;
    private String name; //유가증권국제인증고유코드이름
    private String totalCount;
    private List<Item> items;
    @Getter
    @Setter
    @ToString
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String mrktTotAmt; //종가 * 상장주식수
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
    }
}
