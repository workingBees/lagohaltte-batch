package com.lagohaltte.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MongoCollection {
    COLLECTIONNAME("FinanceInfo"),
    ISINCODE("isincd"),
    STOCKNAME("stockName"),
    MARKETCATEGORY("marketCategory"),
    STOCKIDCODE("stockIdCode"),
    TOTALCOUNT("totalCount"),
    ITEMS("items"),
    MARKETCAP("marketCap"),
    CLOSEPRICE("closePrice"),
    DAYRANGEPRICE("dayRangePrice"),
    DAYRANGEPRATE("dayRangeRate"),
    OPENPRICE("openPrice"),
    HIGHERPRICE("higherPrice"),
    LOWERPRICE("lowerPrice"),
    TRADEVOLUME("tradeVolume"),
    TRADEPRICEVOLUME("tradePriceVolume"),
    TOTALSTOCK("totalStock"),
    BASEDATE("baseDate");

    private final String filedName;
}
