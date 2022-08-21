package com.lagohaltte.step;

import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

public class StepKOSDAQWriter extends JdbcBatchItemWriter<String> {

    @Override
    public void write(List<? extends String> items) throws Exception {
        System.out.println(items.toString());
    }
}
