package com.lagohaltte.step;

import org.junit.jupiter.api.Test;

class StepCrawlingStockNameTest {

    @Test
    void 네이버파이낸스코스닥크롤() {
        //given
        StepCrawlingStockName stepCrawlingStockName = new StepCrawlingStockName();
        //when
        stepCrawlingStockName.doReadPage();

        //then
    }
}