package com.lagohaltte.utils;

import lombok.Getter;

@Getter
public enum MongoCollection {
    COLLECTIONNAME("FinanceInfoTest"),
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
    MongoCollection(String filedName) {
        this.filedName = filedName;
    }
}
