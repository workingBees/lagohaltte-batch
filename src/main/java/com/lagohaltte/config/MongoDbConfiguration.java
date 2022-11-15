package com.lagohaltte.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
public class MongoDbConfiguration extends AbstractMongoClientConfiguration {

    @Value("${mongoDbUrl}")
    private String mongoDdUrl ;
    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString(mongoDdUrl);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    protected String getDatabaseName() {
        return "lagohaltte";
    }
}
