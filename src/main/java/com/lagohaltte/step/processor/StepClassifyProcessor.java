package com.lagohaltte.step.processor;

import com.lagohaltte.entity.FinanceInfoEntity;
import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.step.CallStockInfoOpenApi;
import com.lagohaltte.utils.LagohaltteUtil;
import com.lagohaltte.utils.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class StepClassifyProcessor implements ItemProcessor<String, FinanceInfoEntity> {
    private final CallStockInfoOpenApi callStockInfoOpenApi;
    private final MongoTemplate mongoTemplate;

    @Override
    public FinanceInfoEntity process(@NotNull String stockName) {
        try {
            String dayDate = getFinanceLastItemBaseDate(stockName);
            if (dayDate != null && LagohaltteUtil.isHaveYesterdayDate(dayDate)) {
                log.info("Yesterday data exists : {}", stockName);
                return null;
            }
            return dayDate == null ? callStockAllItems(stockName) : callStockAllItems(stockName, dayDate);
        } catch (Exception exception) {
            log.error("StepClassifyProcessor Error : {}", stockName, exception);
        }
        return null;
    }

    private String getFinanceLastItemBaseDate(String stockName) {
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(stockName));
        FinanceInfoEntity financeInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        if (Objects.isNull(financeInfoEntity)) {
            return null;
        }
        return financeInfoEntity.getItems().get(0).getBaseDate();
    }

    private FinanceInfoEntity callStockAllItems(String stockName) throws Exception{
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, LagohaltteUtil.baseDate);
        if (stockPriceInfoResponseEntity == null) {
            throw new NoSuchElementException(stockName);
        }
        return FinanceInfoEntity.convertStockPriceInfoToFinanceInfoEntity(Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }

    private FinanceInfoEntity callStockAllItems(String stockName, String beginDate) throws Exception{
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, beginDate);
        if (stockPriceInfoResponseEntity == null) {
            throw new NoSuchElementException(stockName);
        }
        return FinanceInfoEntity.convertStockPriceInfoToFinanceInfoEntity(Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }
}
