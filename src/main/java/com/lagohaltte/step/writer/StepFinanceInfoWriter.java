package com.lagohaltte.step.writer;

import com.lagohaltte.utils.LagohaltteUtil;
import com.lagohaltte.entity.FinanceInfoEntity;
import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.step.CallStockInfoOpenApi;
import com.lagohaltte.utils.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class StepFinanceInfoWriter extends MongoItemWriter<String> {
    private final CallStockInfoOpenApi callStockInfoOpenApi;
    private final MongoTemplate mongoTemplate;

    @Override
    public void write(@NotNull List<? extends String> items) throws IOException {
//        if (LagohaltteUtil.isWeekendDay()) {
//            return;
//        }
        for (String stockName : items) {
            if (isExistsCollection(stockName)) {
                insertOneFinanceInfo(stockName);
            } else {
                insertNewFinanceInfo(stockName);
            }
        }
    }
    private void insertOneFinanceInfo(String stockName) throws IOException {
        if (isHaveLastDayData(stockName, LagohaltteUtil.getLastDay())) {
            log.info("어제데이터 있음:{}", stockName);
            return;
        }
        FinanceInfoEntity financeInfoEntity = callLastDayStockPriceInfo(stockName);
        if (financeInfoEntity.getTotalCount().equals("0")) {
            log.info("데이터 없음1 :{}", stockName);
            return;
        }
        if (!isSuccessInsertFinanceInfoItem(financeInfoEntity)) {
            log.info("하나의 데이터를 넣지 못 함 :{}", stockName);
            return;
        }
        if (!increaseStockPriceInfoCnt(stockName)) {
            log.info("totalCnt를 올리지 못 함. : {}", stockName);
        }
        log.info("item 제대로 넣음");
    }

    private void insertNewFinanceInfo(String stockName) throws IOException {
        if (isHaveLastDayData(stockName, LagohaltteUtil.getLastDay())) {
            return;
        }
        FinanceInfoEntity financeInfoEntity = callStockAllItems(stockName);
        if (Objects.isNull(financeInfoEntity)) {
            log.info("NULL임 : {}", stockName);
            return;
        }
        if (financeInfoEntity.getTotalCount().equals("0")) {
            log.info("데이터 없음2 : {}", stockName);
            return;
        }
        if (!isSuccessInsertAllFinanceInfo(financeInfoEntity)) {
            log.info("모든 데이터를 넣지 못 함 : {}", stockName);
        }
        log.info("All 제대로 넣음");
    }
    public boolean isExistsCollection(String stockName) {
        Query query = new Query(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(stockName));
        FinanceInfoEntity financeInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        return Objects.nonNull(financeInfoEntity);
    }

    private FinanceInfoEntity callStockAllItems(String stockName) throws IOException {
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, "20200102");
        if(Objects.isNull(stockPriceInfoResponseEntity)){
            return null;
        }
        return FinanceInfoEntity.convertStockPriceInfoToFinanceInfoEntity(Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }

    private FinanceInfoEntity callLastDayStockPriceInfo(String stockName) throws IOException {
        String lastDay = LagohaltteUtil.getLastDay();
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, lastDay);
        return FinanceInfoEntity.convertStockPriceInfoToFinanceInfoEntity(Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }

    private boolean isSuccessInsertFinanceInfoItem(FinanceInfoEntity financeInfoEntity) {
        log.info("push FinanceInfo Item : {}", financeInfoEntity.getStockName());
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
        Update update = new Update();
        update.push(MongoCollection.ITEMS.getFiledName()).atPosition(0).value(financeInfoEntity.getItems().get(0));
        return mongoTemplate.updateFirst(query, update, MongoCollection.COLLECTIONNAME.getFiledName()).wasAcknowledged();
    }

    private boolean increaseStockPriceInfoCnt(String stockName) {
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(stockName));
        FinanceInfoEntity financeInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        if (Objects.isNull(financeInfoEntity)) {
            return false;
        }
        int totalCnt = Integer.parseInt(financeInfoEntity.getTotalCount());
        totalCnt += 1;
        Update update = new Update();
        update.set(MongoCollection.TOTALCOUNT.getFiledName(), Integer.toString(totalCnt));
        return mongoTemplate.upsert(query, update, MongoCollection.COLLECTIONNAME.getFiledName()).wasAcknowledged();
    }

    private boolean isSuccessInsertAllFinanceInfo(FinanceInfoEntity financeInfoEntity) {
        log.info("push mongo all : {}", financeInfoEntity.getStockName());
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
        Update update = new Update();
        update.set(MongoCollection.ISINCODE.getFiledName(), financeInfoEntity.getIsinCd());
        update.set(MongoCollection.STOCKNAME.getFiledName(), financeInfoEntity.getStockName());
        update.set(MongoCollection.MARKETCATEGORY.getFiledName(), financeInfoEntity.getMarketCategory());
        update.set(MongoCollection.STOCKIDCODE.getFiledName(), financeInfoEntity.getStockIdCode());
        update.set(MongoCollection.TOTALCOUNT.getFiledName(), financeInfoEntity.getTotalCount());
        update.set(MongoCollection.ITEMS.getFiledName(), financeInfoEntity.getItems());
        return mongoTemplate.upsert(query, update, MongoCollection.COLLECTIONNAME.getFiledName()).wasAcknowledged();
    }

    private boolean isHaveLastDayData(String stockName, String lastDay) {
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(stockName));
        FinanceInfoEntity financeInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        if (Objects.isNull(financeInfoEntity)) {
            return false;
        }
        FinanceInfoEntity.Item item = financeInfoEntity.getItems().get(0);
        return item.getBaseDate().equals(lastDay);
    }
}
