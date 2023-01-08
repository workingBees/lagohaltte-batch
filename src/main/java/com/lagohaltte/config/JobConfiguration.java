package com.lagohaltte.config;

import com.lagohaltte.entity.FinanceInfoEntity;
import com.lagohaltte.listener.CustomListener;
import com.lagohaltte.step.*;
import com.lagohaltte.step.processor.StepClassifyProcessor;
import com.lagohaltte.step.reader.StepCrawlingStockNameReader;
import com.lagohaltte.step.writer.StepFinanceInfoWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${naverKosdaqUrl}")
    private String naverKosdaqUrl;
    @Value("${naverKospiUrl}")
    private String naverKospiUrl;

    @Bean
    public Job listedCorporationsJob() throws Exception {
        return jobBuilderFactory.get("listedCorporationsJob")
                .start(listedKosdaqCorporationsStep())
                .next(listedKospiCorporationStep())
                .build();
    }

    @Bean
    public Step listedKosdaqCorporationsStep() {
        return stepBuilderFactory.get("listedKosdaqCorporationsStep")
                .<String, FinanceInfoEntity>chunk(1)
                .reader(crawlingKosdaqStockName())
                .processor(new StepClassifyProcessor(callStockInfoOpenApi, mongoTemplate))
                .writer(new StepFinanceInfoWriter(mongoTemplate))
                .listener(customExecutionListener())
                .build();
    }

    @Bean
    public Step listedKospiCorporationStep() {
        return stepBuilderFactory.get("listedKospiCorporationsStep")
                .<String, FinanceInfoEntity>chunk(1)
                .reader(crawlingKospiStockName())
                .processor(new StepClassifyProcessor(callStockInfoOpenApi, mongoTemplate))
                .writer(new StepFinanceInfoWriter(mongoTemplate))
                .listener(customExecutionListener())
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

    @Bean
    public StepExecutionListener customExecutionListener() {
        return new CustomListener();
    }
}
