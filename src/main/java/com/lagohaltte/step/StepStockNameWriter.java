package com.lagohaltte.step;

import com.lagohaltte.dto.FinanceBaseDto;
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
public class StepStockNameWriter extends JdbcBatchItemWriter<FinanceBaseDto> {
    private final MongoTemplate mongoTemplate;

    @Override
    public void write(List<? extends FinanceBaseDto> items) {
        for (FinanceBaseDto item : items) {
            Query query = new Query(Criteria.where("name").is(item.getItmsNm()));
            Update update = new Update();
            update.set("name", StringUtils.trim(item.getItmsNm()));
            update.set("isinCd", StringUtils.trim(item.getIsinCd()));
            update.set("mrktCtg", StringUtils.trim(item.getMrktCtg()));
            update.set("srtnCd", StringUtils.trim(item.getSrtnCd()));
            mongoTemplate.upsert(query, update, "FinanceBases");
            log.info("Insert DB : {}", item.getItmsNm());
        }
    }
}
