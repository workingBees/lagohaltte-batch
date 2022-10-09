package com.lagohaltte.config;

import com.lagohaltte.domain.StockName;
import com.lagohaltte.step.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;
    private final CallStockInfoOpenApi callStockInfoOpenApi;

    @Bean
    public Job listedCorporationsJob() throws Exception {
        return jobBuilderFactory.get("listedCorporationsJob")
                .start(listedKosdaqCorporationsStep())
                .next(listedKospiCorporationStep())
                .next(listedCorporationStockInfoStep())
                .build();
    }

    @Bean
    public Step listedKosdaqCorporationsStep() {
        return stepBuilderFactory.get("listedKosdaqCorporationsStep")
                .<String, String>chunk(1)
                .reader(new StepCrawlingKosdaq())
                .writer(new StepStockNameWriter(mongoTemplate))
                .build();
    }

    @Bean
    public Step listedKospiCorporationStep() {
        return stepBuilderFactory.get("listedKospiCorporationsStep")
                .<String, String>chunk(1)
                .reader(new StepCrawlingKospi())
                .writer(new StepStockNameWriter(mongoTemplate))
                .build();
    }

    @Bean
    public Step listedCorporationStockInfoStep() throws Exception {
        return stepBuilderFactory.get("listedCorporationStockInfoStep")
                .<StockName,StockName>chunk(1)
                .reader(new StepStockNameReader(mongoTemplate))
                .writer(new StepStockInfoWriter(callStockInfoOpenApi,mongoTemplate))
                .build();
    }
}
