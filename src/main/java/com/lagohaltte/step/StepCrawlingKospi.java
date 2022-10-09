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
public class StepCrawlingKospi extends AbstractPagingItemReader<String> {
    final static String naverKospiFinance = "https://finance.naver.com/sise/sise_market_sum.naver";

    @Override
    protected void doReadPage() {
        this.setPageSize(50);
        log.info("reader start. current page:{}", this.getPage()+1);
        String url = naverKospiFinance + "&page" + (this.getPage() + 1);
        log.info("navar kospi finance url: {}", url);

        Connection conn = Jsoup.connect(url);
        Document document;
        try {
            document = conn.get();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        Elements stockNameUrlElements = document.select("table.type_2 tbody tr");
        if (stockNameUrlElements.size() <= 1) {
            results = null;
        }
        results = stockNameUrlElements.stream()
                .filter(element -> element.hasAttr("onmouseover"))
                .filter(element -> element.children().is("td"))
                .map(element -> element.select("a").text().trim())
                .collect(Collectors.toList());
        log.info("Crawling kospi results {} ", results);
    }
    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
