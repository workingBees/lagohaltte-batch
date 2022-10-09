package com.lagohaltte.step;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@RequiredArgsConstructor
public class StepStockNameWriter extends JdbcBatchItemWriter<String> {
    private final MongoTemplate mongoTemplate;
    @Override
    public void write(List<? extends String> items) throws Exception {
        for (String item : items) {
            Query query = new Query(Criteria.where("name").is(item));
            Update update = new Update();
            update.set("name", StringUtils.trim(item));
            mongoTemplate.upsert(query, update, "StockName");
        }
    }
}
