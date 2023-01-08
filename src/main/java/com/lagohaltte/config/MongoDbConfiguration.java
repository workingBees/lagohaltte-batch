package com.lagohaltte.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDbConfiguration extends AbstractMongoClientConfiguration {

    @Value("${mongoDbUrl}")
    private String mongoDdUrl ;
    @Override
    public @NotNull MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString(mongoDdUrl);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    protected @NotNull String getDatabaseName() {
        return "lagohaltte";
    }
}
