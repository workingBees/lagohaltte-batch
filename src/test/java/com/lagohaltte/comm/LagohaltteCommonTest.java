package com.lagohaltte.comm;

import com.lagohaltte.config.MongoDbConfiguration;
import com.lagohaltte.dto.FinanceInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;


@SpringBootTest(classes = {MongoDbConfiguration.class})
class LagohaltteCommonTest {

}