package com.lagohaltte.utils;

import com.lagohaltte.entity.FinanceInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LagohaltteUtil {

    public static String baseDate = "20200102";
    public static String getLastDay() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static boolean isWeekendDay() {
        LocalDate date = LocalDate.now().minusDays(1);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.equals(DayOfWeek.SUNDAY)||dayOfWeek.equals(DayOfWeek.SATURDAY);
    }

    public static boolean isHaveYesterdayDate(String itemYesterdayDataDate) {
        return itemYesterdayDataDate.equals(getLastDay());
    }
}
