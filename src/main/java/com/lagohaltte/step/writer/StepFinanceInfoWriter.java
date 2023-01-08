package com.lagohaltte.step.writer;

import com.lagohaltte.entity.FinanceInfoEntity;
import com.lagohaltte.utils.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class StepFinanceInfoWriter extends MongoItemWriter<FinanceInfoEntity> {
    private final MongoTemplate mongoTemplate;

    @Override
    public void write(@NotNull List<? extends FinanceInfoEntity> items) {
        for (FinanceInfoEntity financeInfoEntity : items) {
            if (isExistsCollection(financeInfoEntity.getStockName())) {
                updateOneFinanceInfo(financeInfoEntity);
            } else {
                insertNewFinanceInfo(financeInfoEntity);
            }

        }
    }
    private void updateOneFinanceInfo(FinanceInfoEntity financeInfoEntity) {
        try {
            updateFinanceInfoItem(financeInfoEntity);
            updateFinanceInfoTotalCount(financeInfoEntity);
        } catch (Exception exception) {
            log.error("StepFinanceInfoWriter updateOneFinanceInfo Error : ", exception);
        }
        log.info("Success Update Finance Item");
    }

    private void insertNewFinanceInfo(FinanceInfoEntity financeInfoEntity) {
        try {
            insertAllFinanceInfo(financeInfoEntity);
        } catch (Exception exception) {
            log.error("StepFinanceInfoWriter insertNewFinanceInfo Error : ", exception);
        }
        log.info("Success Insert New FinanceInfo ");
    }

    public boolean isExistsCollection(String stockName) {
        Query query = new Query(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(stockName));
        FinanceInfoEntity financeInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        return Objects.nonNull(financeInfoEntity);
    }

    private void updateFinanceInfoItem(FinanceInfoEntity financeInfoEntity) {
        log.info("update FinanceInfo a Item : {}", financeInfoEntity.getStockName());
        for (int idx = financeInfoEntity.getItems().size() - 1; idx >= 0; idx--) {
            Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
            Update update = new Update();
            update.push(MongoCollection.ITEMS.getFiledName()).atPosition(0).value(financeInfoEntity.getItems().get(idx));
            mongoTemplate.updateFirst(query, update, MongoCollection.COLLECTIONNAME.getFiledName());
        }
    }

    private void updateFinanceInfoTotalCount(FinanceInfoEntity financeInfoEntity) {
        Query query = new Query(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
        FinanceInfoEntity allFinanceInfoEntity = mongoTemplate.findOne(query, FinanceInfoEntity.class, MongoCollection.COLLECTIONNAME.getFiledName());
        Query upsertQuery = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
        if (allFinanceInfoEntity.getItems().size() == 0) {
            return;
        }
        int totalCnt = allFinanceInfoEntity.getItems().size();
        Update update = new Update();
        update.set(MongoCollection.TOTALCOUNT.getFiledName(), totalCnt);
        mongoTemplate.upsert(upsertQuery, update, MongoCollection.COLLECTIONNAME.getFiledName()).wasAcknowledged();
    }

    private void insertAllFinanceInfo(FinanceInfoEntity financeInfoEntity) {
        log.info("insert FinanceInfo all Items : {}", financeInfoEntity.getStockName());
        Query query = new Query().addCriteria(Criteria.where(MongoCollection.STOCKNAME.getFiledName()).is(financeInfoEntity.getStockName()));
        Update update = new Update();
        update.set(MongoCollection.ISINCODE.getFiledName(), financeInfoEntity.getIsinCd());
        update.set(MongoCollection.STOCKNAME.getFiledName(), financeInfoEntity.getStockName());
        update.set(MongoCollection.MARKETCATEGORY.getFiledName(), financeInfoEntity.getMarketCategory());
        update.set(MongoCollection.STOCKIDCODE.getFiledName(), financeInfoEntity.getStockIdCode());
        update.set(MongoCollection.TOTALCOUNT.getFiledName(), financeInfoEntity.getTotalCount());
        update.set(MongoCollection.ITEMS.getFiledName(), financeInfoEntity.getItems());
        mongoTemplate.upsert(query, update, MongoCollection.COLLECTIONNAME.getFiledName());
    }


}
