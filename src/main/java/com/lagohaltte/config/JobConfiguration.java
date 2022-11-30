package com.lagohaltte.config;

import com.lagohaltte.utils.LagohaltteUtil;
import com.lagohaltte.entity.FinanceBaseDto;
import com.lagohaltte.model.StockName;
import com.lagohaltte.step.*;
import com.lagohaltte.step.processor.StepStockBaseInfoProcessor;
import com.lagohaltte.step.reader.StepCrawlingStockNameReader;
import com.lagohaltte.step.reader.StepStockNameReader;
import com.lagohaltte.step.writer.StepFinanceInfoWriter;
import com.lagohaltte.step.writer.StepStockNameWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;
    private final CallStockInfoOpenApi callStockInfoOpenApi;

    @Value("${naverKosdaqUrl}")
    private String naverKosdaqUrl;
    @Value("${naverKospiUrl}")
    private String naverKospiUrl;

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
                .<String, FinanceBaseDto>chunk(1)
                .reader(crawlingKosdaqStockName())
                .processor(new StepStockBaseInfoProcessor( callStockInfoOpenApi))
                .writer(new StepStockNameWriter(mongoTemplate))
                .build();
    }

    @Bean
    public Step listedKospiCorporationStep() {
        return stepBuilderFactory.get("listedKospiCorporationsStep")
                .<String, FinanceBaseDto>chunk(1)
                .reader(crawlingKospiStockName())
                .processor(new StepStockBaseInfoProcessor( callStockInfoOpenApi))
                .writer(new StepStockNameWriter(mongoTemplate))
                .build();
    }

    @Bean
    public Step listedCorporationStockInfoStep() throws Exception {
        return stepBuilderFactory.get("listedCorporationStockInfoStep")
                .<StockName, StockName>chunk(1)
                .reader(new StepStockNameReader(mongoTemplate))
                .writer(new StepFinanceInfoWriter(callStockInfoOpenApi, mongoTemplate))
                .build();
    }

    @Bean

    public StepCrawlingStockNameReader crawlingKospiStockName() {
        StepCrawlingStockNameReader stepCrawlingStockName = new StepCrawlingStockNameReader();
        stepCrawlingStockName.setNaverFinanceUrl(naverKospiUrl);
        return stepCrawlingStockName;
    }

    @Bean
    public StepCrawlingStockNameReader crawlingKosdaqStockName() {
        StepCrawlingStockNameReader stepCrawlingStockName = new StepCrawlingStockNameReader();
        stepCrawlingStockName.setNaverFinanceUrl(naverKosdaqUrl);
        return stepCrawlingStockName;
    }
}
