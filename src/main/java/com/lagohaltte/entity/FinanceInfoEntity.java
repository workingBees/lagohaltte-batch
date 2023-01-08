package com.lagohaltte.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lagohaltte.model.StockPriceInfo;
import lombok.*;
import lombok.extern.java.Log;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


//setter 지양
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Document(collection = "FinanceInfo")
public class FinanceInfoEntity {
    @Id
    private String _id;
    private String isinCd; //국제인증고유코드
    private String stockName; //유가증권국제인증고유코드이름
    private String marketCategory; //주식시장 구분
    private String stockIdCode; //6자리 코드
    private int totalCount;
    private List<Item> items;
    @Getter
    @Setter
    @ToString
    @Builder
    public static class Item {
        private long marketCap; //종가 * 상장주식수
        private int closePrice; // 종가
        private int dayRangePrice; // 전일대비 등락
        private float dayRangeRate; // 전일대비 등락에 따른 비율
        private int openPrice; //시초가
        private int higherPrice; //하루중 고가
        private int lowerPrice; //하루중 저가
        private long tradeVolume; //체결수량의 누적합계
        private long tradePriceVolume; //거래건별 체결 가격 * 체결수량의 누적합계
        private long totalStock; //상장주식수
        private String baseDate; //기준일자
        //TODO : static item builder로
        public static Item convertStockInfoToItem(StockPriceInfo.Response.Body.Items.Item stockPriceInfoItem) {
            return Item.builder()
                    .marketCap(Long.parseLong(stockPriceInfoItem.getMrktTotAmt()))
                    .closePrice(Integer.parseInt(stockPriceInfoItem.getClpr()))
                    .dayRangePrice(Integer.parseInt(stockPriceInfoItem.getVs()))
                    .dayRangeRate(Float.parseFloat(stockPriceInfoItem.getFltRt()))
                    .openPrice(Integer.parseInt(stockPriceInfoItem.getMkp()))
                    .higherPrice(Integer.parseInt(stockPriceInfoItem.getHipr()))
                    .lowerPrice(Integer.parseInt(stockPriceInfoItem.getLopr()))
                    .tradeVolume(Long.parseLong(stockPriceInfoItem.getTrqu()))
                    .tradePriceVolume(Long.parseLong(stockPriceInfoItem.getTrPrc()))
                    .totalStock(Long.parseLong(stockPriceInfoItem.getLstgStCnt()))
                    .baseDate(stockPriceInfoItem.getBasDt())
                    .build();
        }
    }

    public static FinanceInfoEntity convertStockPriceInfoToFinanceInfoEntity(StockPriceInfo stockPriceInfo) {
        FinanceInfoEntity financeInfoEntity = new FinanceInfoEntity();
        financeInfoEntity.setStockName(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getItmsNm());
        financeInfoEntity.setIsinCd(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getIsinCd());
        financeInfoEntity.setMarketCategory(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getMrktCtg());
        financeInfoEntity.setStockIdCode(stockPriceInfo.getResponse().getBody().getItems().getItem().get(0).getSrtnCd());
        financeInfoEntity.setTotalCount(Integer.parseInt(stockPriceInfo.getResponse().getBody().getTotalCount()));
        financeInfoEntity.setItems(getFinanceInfoEntityItem(stockPriceInfo.getResponse().getBody().getItems()));
        return financeInfoEntity;
    }

    private static List<FinanceInfoEntity.Item> getFinanceInfoEntityItem(StockPriceInfo.Response.Body.Items stockPriceInfoItems) {
        return stockPriceInfoItems.getItem().stream()
                .map(Item::convertStockInfoToItem).collect(Collectors.toList());
    }
}
