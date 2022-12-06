package com.lagohaltte.step.writer;

import com.lagohaltte.entity.FinanceInfoEntity;
import com.lagohaltte.utils.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class StepStockNameWriter extends JdbcBatchItemWriter<FinanceInfoEntity> {
    private final MongoTemplate mongoTemplate;

    @Override
    public void write(List<? extends FinanceInfoEntity> items) {
        for (FinanceInfoEntity item : items) {
            Query query = new Query(Criteria.where("name").is(item.getStockName()));
            Update update = new Update();
            update.set(MongoCollection.STOCKNAME.getFiledName(), StringUtils.trim(item.getStockName()));
            update.set(MongoCollection.ISINCODE.getFiledName(), StringUtils.trim(item.getIsinCd()));
            update.set(MongoCollection.MARKETCATEGORY.getFiledName(), StringUtils.trim(item.getMarketCategory()));
            update.set("srtnCd", StringUtils.trim(item.getStockIdCode()));
            mongoTemplate.upsert(query, update, "FinanceBases");
            log.info("Insert DB : {}", item.getStockName());
        }
    }
}
