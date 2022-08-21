package com.lagohaltte.step;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepCrawlingKOSDAQTest {

    @Test
    void 네이버파이낸스코스닥크롤() {
        //given
        StepCrawlingKOSDAQ stepCrawlingKOSDAQ = new StepCrawlingKOSDAQ();
        //when
        stepCrawlingKOSDAQ.doReadPage();

        //then
    }
}