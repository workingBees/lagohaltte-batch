package com.lagohaltte.utils;

import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.entity.FinanceInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LagohaltteUtil {

    public static String getLastDay() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static boolean isWeekendDay() {
        LocalDate date = LocalDate.now().minusDays(1);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.equals(DayOfWeek.SUNDAY)||dayOfWeek.equals(DayOfWeek.SATURDAY);
    }
}
