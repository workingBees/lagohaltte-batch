package com.lagohaltte.step;

import com.lagohaltte.comm.LagohaltteCommon;
import com.lagohaltte.dto.StockPriceInfo;
import com.lagohaltte.dto.FinanceBaseDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class StepStockBaseInfoProcessor implements ItemProcessor<String, FinanceBaseDto> {
    private final LagohaltteCommon lagohaltteCommon;
    private final CallStockInfoOpenApi callStockInfoOpenApi;

    @Override
    public FinanceBaseDto process(@NotNull String item) throws Exception {
        if (lagohaltteCommon.isExistsCollection(item, "FinanceBases"))
            return null;
        ResponseEntity<StockPriceInfo> responseEntity = callStockInfoOpenApi.requestPriceInfo(item, "");
        if (isExistsResponseEntity(responseEntity))
            return lagohaltteCommon.convertStockToFinanceBaseDto(Objects.requireNonNull(responseEntity.getBody()).getResponse().getBody().getItems().getItem().get(0));
        log.info("Not Insert StockName : {}", item);
        return null;
    }
    private boolean isExistsResponseEntity(ResponseEntity<StockPriceInfo> responseEntity) {
        return !Objects.requireNonNull(responseEntity.getBody()).getResponse().getBody().getTotalCount().equals("0");
    }
}
