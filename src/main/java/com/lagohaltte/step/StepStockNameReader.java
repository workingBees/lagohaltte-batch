package com.lagohaltte.step;


import com.lagohaltte.domain.StockName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;

@Slf4j
public class StepStockNameReader extends MongoItemReader<StockName> {
    public StepStockNameReader(MongoTemplate mongoTemplate) {
       Query query = new Query(Criteria.where("{}"));
        this.setTemplate(mongoTemplate);
        this.setCollection("StockName");
        this.setTargetType(StockName.class);
        this.setPageSize(10);
        this.setSort(Collections.emptyMap());
        this.setQuery("{}");
    }
}
