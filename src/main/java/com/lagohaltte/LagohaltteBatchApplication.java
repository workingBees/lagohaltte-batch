package com.lagohaltte;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableBatchProcessing
@SpringBootApplication
public class LagohaltteBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LagohaltteBatchApplication.class, args);
    }

}
