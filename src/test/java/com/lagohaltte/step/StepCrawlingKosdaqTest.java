package com.lagohaltte.step;

import org.junit.jupiter.api.Test;

class StepCrawlingKosdaqTest {

    @Test
    void 네이버파이낸스코스닥크롤() {
        //given
        StepCrawlingKosdaq stepCrawlingKOSDAQ = new StepCrawlingKosdaq();
        //when
        stepCrawlingKOSDAQ.doReadPage();

        //then
    }
}