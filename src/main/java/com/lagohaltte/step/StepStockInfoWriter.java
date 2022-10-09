package com.lagohaltte.step;

import com.lagohaltte.domain.StockName;
import com.lagohaltte.domain.StockPriceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class StepStockInfoWriter extends MongoItemWriter<StockName> {
    private final CallStockInfoOpenApi callStockInfoOpenApi;
    private final MongoTemplate mongoTemplate;
    @Override
    public void write(List<? extends StockName> items) throws Exception {
        items.forEach(s -> {
            try {
                ResponseEntity<StockPriceInfo> requestPriceInfo = callStockInfoOpenApi.requestPriceInfo(s.getName());
                mongoTemplate.insert(Objects.requireNonNull(requestPriceInfo.getBody()), "StockPriceInfo");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
