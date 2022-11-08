package com.lagohaltte.comm;

import com.lagohaltte.dto.StockPriceInfo;
import com.lagohaltte.dto.FinanceBaseDto;
import com.lagohaltte.dto.FinanceInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LagohaltteCommon {
    private final MongoTemplate mongoTemplate;
    private final String baseDt = "20200102";

    public boolean isExistsCollection(String stockName, String collectionName) {
        Query query = new Query(Criteria.where("name").is(stockName));
        FinanceBaseDto financeBaseDto = mongoTemplate.findOne(query, FinanceBaseDto.class, collectionName);
        return Objects.nonNull(financeBaseDto);
    }

    public FinanceBaseDto convertStockToFinanceBaseDto(StockPriceInfo.Response.Body.Items.Item stockPriceInfoItem) {
        return FinanceBaseDto.builder()
                .isinCd(stockPriceInfoItem.getIsinCd())
                .itmsNm(stockPriceInfoItem.getItmsNm())
                .mrktCtg(stockPriceInfoItem.getMrktCtg())
                .srtnCd(stockPriceInfoItem.getSrtnCd())
                .build();
    }

    public FinanceInfoDto convertStockPriceInfoToFinanceDto(String stockName, StockPriceInfo stockPriceInfo) {
        FinanceInfoDto financeInfoDto = new FinanceInfoDto();
        financeInfoDto.setName(stockName);
        financeInfoDto.setTotalCount(stockPriceInfo.getResponse().getBody().getTotalCount());
        financeInfoDto.setItems(getFinanceDtoItem(stockPriceInfo.getResponse().getBody().getItems()));
        return financeInfoDto;
    }

    public String getLastDay() {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public boolean isWeekendDay() {
        LocalDate date = LocalDate.now();
        date.minusDays(1);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US);
        return dayName.equals("Saturday") || dayName.equals("Sunday");
    }

    private List<FinanceInfoDto.Item> getFinanceDtoItem(StockPriceInfo.Response.Body.Items stockPriceInfoItems) {
        return stockPriceInfoItems.getItem().stream()
                .map(item -> {
                    return FinanceInfoDto.Item.builder()
                            .mrktTotAmt(item.getMrktTotAmt())
                            .clpr(item.getClpr())
                            .vs(item.getVs())
                            .fltRt(item.getFltRt())
                            .mkp(item.getMkp())
                            .hipr(item.getHipr())
                            .lopr(item.getLopr())
                            .trqu(item.getTrqu())
                            .trPrc(item.getTrPrc())
                            .lstgStCnt(item.getLstgStCnt())
                            .basDt(item.getBasDt())
                            .build();
                }).collect(Collectors.toList());
    }
}
