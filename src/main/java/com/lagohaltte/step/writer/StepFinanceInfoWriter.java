package com.lagohaltte.step.writer;

import com.lagohaltte.entity.FinanceBaseDto;
import com.lagohaltte.utils.LagohaltteUtil;
import com.lagohaltte.entity.FinanceInfoDto;
import com.lagohaltte.model.StockName;
import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.step.CallStockInfoOpenApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class StepFinanceInfoWriter extends MongoItemWriter<StockName> {
    private final CallStockInfoOpenApi callStockInfoOpenApi;
    private final MongoTemplate mongoTemplate;
    private final String financeInfoCollectionName = "FinanceInfo";

    @Override
    public void write(List<? extends StockName> items) throws IOException {
        for (StockName stockName : items) {
            if (isExistsCollection(stockName.getName(), financeInfoCollectionName)) {
                //존재할 경우, 아이템 하나 넣는거
                insertOneStockInfo(stockName.getName());
            } else {
                //존재하지 않을 경우, 아이템 전부 다 넣는거
                insertAllStockInfo(stockName.getName());
            }
        }
    }
    private void insertOneStockInfo(String stockName) throws IOException {
        if (isHaveLastDayData(stockName, LagohaltteUtil.getLastDay())) {
            return;
        }
        FinanceInfoDto financeInfoDto = callLastDayStockPriceInfo(stockName);
        if (Objects.isNull(financeInfoDto)) {
            return;
        }
        if (financeInfoDto.getTotalCount().equals("0")) {
            return;
        }
        if (!pushFinanceInfoItem(financeInfoDto)) {
            log.info("하나의 데이터를 넣지 못 함.");
            return;
        }
        if (!increaseStockPriceInfoCnt(stockName)) {
            log.info("totalCnt를 올리지 못 함.");
        }
        log.info("item 제대로 넣음");
    }

    private void insertAllStockInfo(String stockName) throws IOException {
        FinanceInfoDto financeInfoDto = callStockAllItems(stockName);
        if (financeInfoDto.getTotalCount().equals("0")) {
            return;
        }
        if (!pushAllFinanceInfo(financeInfoDto)) {
            log.info("모든 데이터를 넣지 못 함.");
        }
        log.info("All 제대로 넣음");
    }
    public boolean isExistsCollection(String stockName, String collectionName) {
        Query query = new Query(Criteria.where("name").is(stockName));
        FinanceBaseDto financeBaseDto = mongoTemplate.findOne(query, FinanceBaseDto.class, collectionName);
        return Objects.nonNull(financeBaseDto);
    }

    private FinanceInfoDto callStockAllItems(String stockName) throws IOException {
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, "20200102");
        return LagohaltteUtil.convertStockPriceInfoToFinanceDto(stockName, Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }

    private FinanceInfoDto callLastDayStockPriceInfo(String stockName) throws IOException {
        if (LagohaltteUtil.isWeekendDay()) return null;
        String lastDay = LagohaltteUtil.getLastDay();
        ResponseEntity<StockPriceInfo> stockPriceInfoResponseEntity = callStockInfoOpenApi.requestPriceInfo(stockName, lastDay);
        return LagohaltteUtil.convertStockPriceInfoToFinanceDto(stockName, Objects.requireNonNull(stockPriceInfoResponseEntity.getBody()));
    }

    private boolean pushFinanceInfoItem(FinanceInfoDto financeInfoDto) {
        log.info("push mongo Item : {}", financeInfoDto.getName());
        //1. stockName을 가진 Document 찾기
        Query query = new Query().addCriteria(Criteria.where("name").is(financeInfoDto.getName()));
        Update update = new Update();

        //2. FinanceDto.Item 추가
        update.push("items").atPosition(0).value(financeInfoDto.getItems().get(0));
        return mongoTemplate.updateFirst(query, update, financeInfoCollectionName).wasAcknowledged();
    }

    private boolean increaseStockPriceInfoCnt(String stockName) {
        Query query = new Query().addCriteria(Criteria.where("name").is(stockName));
        FinanceInfoDto financeInfoDto = mongoTemplate.findOne(query, FinanceInfoDto.class, financeInfoCollectionName);
        if (Objects.isNull(financeInfoDto)) {
            return false;
        }
        int totalCnt = Integer.parseInt(financeInfoDto.getTotalCount());
        totalCnt += 1;
        Update update = new Update();
        update.set("totalCount", Integer.toString(totalCnt));
        return mongoTemplate.upsert(query, update, financeInfoCollectionName).wasAcknowledged();
    }

    private boolean pushAllFinanceInfo(FinanceInfoDto financeInfoDto) {
        log.info("push mongo all : {}", financeInfoDto.getName());
        Query query = new Query().addCriteria(Criteria.where("name").is(financeInfoDto.getName()));
        Update update = new Update();
        update.set("name", StringUtils.trim(financeInfoDto.getName()));
        update.set("totalCount", StringUtils.trim(financeInfoDto.getTotalCount()));
        update.set("items", financeInfoDto.getItems());
        return mongoTemplate.upsert(query, update, financeInfoCollectionName).wasAcknowledged();
    }

    private boolean isHaveLastDayData(String stockName, String lastDay) {
        Query query = new Query().addCriteria(Criteria.where("name").is(stockName));
        FinanceInfoDto financeInfoDto = mongoTemplate.findOne(query, FinanceInfoDto.class, financeInfoCollectionName);
        if (Objects.isNull(financeInfoDto)) {
            return false;
        }
        FinanceInfoDto.Item item = financeInfoDto.getItems().get(0);
        return item.getBasDt().equals(lastDay);
    }
}
