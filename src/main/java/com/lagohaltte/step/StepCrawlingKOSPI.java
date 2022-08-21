package com.lagohaltte.step;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class StepCrawlingKOSPI extends AbstractPagingItemReader<String> {
    final static String naverKospiFinance = "https://finance.naver.com/sise/sise_market_sum.naver";
    @Override
    protected void doReadPage() {
        StringBuilder naverKospiFinanceBuilder = new StringBuilder();
        for(int i = 1;;i++){
            naverKospiFinanceBuilder.setLength(0);
            naverKospiFinanceBuilder.append(naverKospiFinance).append("?&page=").append(i);
            Connection conn = Jsoup.connect(naverKospiFinanceBuilder.toString());
            try {
                Document document = conn.get();
                Elements stockNameUrlElements = document.select("table.type_2 tbody tr");
                if (stockNameUrlElements.size() <= 1) {
                    logger.info(i);
                    break;
                }
                results = stockNameUrlElements.stream()
                        .filter(element -> element.hasAttr("onmouseover"))
                        .filter(element -> element.children().is("td"))
                        .map(element -> element.select("a").text())
                        .collect(Collectors.toList());
                logger.info(results);

            } catch (IOException e) {
            }
        }
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
