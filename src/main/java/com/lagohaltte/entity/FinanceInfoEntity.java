package com.lagohaltte.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lagohaltte.model.StockPriceInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "FinanceInfo")
public class FinanceInfoEntity {
    @Id
    private String _id;
    private String isinCd; //국제인증고유코드
    private String stockName; //유가증권국제인증고유코드이름
    private String marketCategory; //주식시장 구분
    private String stockIdCode; //6자리 코드
    private String totalCount;
    private List<Item> items;
    @Getter
    @Setter
    @ToString
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String marketCap; //종가 * 상장주식수
        private String closePrice; // 종가
        private String dayRangePrice; // 전일대비 등락
        private String dayRangeRate; // 전일대비 등락에 따른 비율
        private String openPrice; //시초가
        private String higherPrice; //하루중 고가
        private String lowerPrice; //하루중 저가
        private String tradeVolume; //체결수량의 누적합계
        private String tradePriceVolume; //거래건별 체결 가격 * 체결수량의 누적합계
        private String totalStock; //상장주식수
        private String baseDate; //기준일자
    }

    public static FinanceInfoEntity convertStockPriceInfoToFinanceInfoEntity(StockPriceInfo stockPriceInfo) {
        FinanceInfoEntity financeInfoEntity = new FinanceInfoEntity();
        financeInfoEntity.setStockName(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getItmsNm());
        financeInfoEntity.setIsinCd(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getIsinCd());
        financeInfoEntity.setMarketCategory(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getMrktCtg());
        financeInfoEntity.setStockIdCode(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getSrtnCd());
        financeInfoEntity.setTotalCount(stockPriceInfo.getResponse().getBody().getTotalCount());
        financeInfoEntity.setItems(getFinanceInfoEntityItem(stockPriceInfo.getResponse().getBody().getItems()));
        return financeInfoEntity;
    }

    private static List<FinanceInfoEntity.Item> getFinanceInfoEntityItem(StockPriceInfo.Response.Body.Items stockPriceInfoItems) {
        return stockPriceInfoItems.getItem().stream()
                .map(item -> Item.builder()
                        .marketCap(item.getMrktTotAmt())
                        .closePrice(item.getClpr())
                        .dayRangePrice(item.getVs())
                        .dayRangeRate(item.getFltRt())
                        .openPrice(item.getMkp())
                        .higherPrice(item.getHipr())
                        .lowerPrice(item.getLopr())
                        .tradeVolume(item.getTrqu())
                        .tradePriceVolume(item.getTrPrc())
                        .totalStock(item.getLstgStCnt())
                        .baseDate(item.getBasDt())
                        .build()).collect(Collectors.toList());
    }
}
