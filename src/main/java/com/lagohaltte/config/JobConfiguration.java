package com.lagohaltte.config;

import com.lagohaltte.step.StepCrawlingKOSDAQ;
import com.lagohaltte.step.StepCrawlingKOSPI;
import com.lagohaltte.step.StepKOSDAQWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job listedCorporationsJob() {
        return jobBuilderFactory.get("listedCorporationsJob")
                .start(listedCorporationsStep())
                .build();
    }

    @Bean
    public Step listedCorporationsStep() {
        return stepBuilderFactory.get("listedCorporationsStep")
                .<String, String>chunk(1)
                .reader(new StepCrawlingKOSDAQ())
                .writer(new StepKOSDAQWriter())
                .build();
    }
}
