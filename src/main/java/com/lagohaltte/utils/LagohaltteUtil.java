package com.lagohaltte.utils;

import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.entity.FinanceBaseDto;
import com.lagohaltte.entity.FinanceInfoDto;
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
public class LagohaltteUtil {

    public static FinanceBaseDto convertStockToFinanceBaseDto(StockPriceInfo.Response.Body.Items.Item stockPriceInfoItem) {
        return FinanceBaseDto.builder()
                .isinCd(stockPriceInfoItem.getIsinCd())
                .itmsNm(stockPriceInfoItem.getItmsNm())
                .mrktCtg(stockPriceInfoItem.getMrktCtg())
                .srtnCd(stockPriceInfoItem.getSrtnCd())
                .build();
    }

    public static FinanceInfoDto convertStockPriceInfoToFinanceDto(String stockName, StockPriceInfo stockPriceInfo) {
        FinanceInfoDto financeInfoDto = new FinanceInfoDto();
        financeInfoDto.setName(stockName);
        financeInfoDto.setTotalCount(stockPriceInfo.getResponse().getBody().getTotalCount());
        financeInfoDto.setItems(getFinanceDtoItem(stockPriceInfo.getResponse().getBody().getItems()));
        return financeInfoDto;
    }

    public static String getLastDay() {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static boolean isWeekendDay() {
        LocalDate date = LocalDate.now().minusDays(1);
        date.minusDays(1);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US);
        return dayName.equals("Saturday") || dayName.equals("Sunday");
    }

    private static List<FinanceInfoDto.Item> getFinanceDtoItem(StockPriceInfo.Response.Body.Items stockPriceInfoItems) {
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
