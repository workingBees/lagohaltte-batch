package com.lagohaltte.step.processor;

import com.lagohaltte.utils.LagohaltteUtil;
import com.lagohaltte.model.StockPriceInfo;
import com.lagohaltte.entity.FinanceBaseDto;
import com.lagohaltte.step.CallStockInfoOpenApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class StepStockBaseInfoProcessor implements ItemProcessor<String, FinanceBaseDto> {
    private final CallStockInfoOpenApi callStockInfoOpenApi;
    @Override
    public FinanceBaseDto process(@NotNull String item) throws Exception {
        //TODO : 신규상장건에 대한 로직 추가,
//        if (lagohaltteUtil.isExistsCollection(item, "FinanceBases")) {
//            return null;
//        }
        ResponseEntity<StockPriceInfo> responseEntity = callStockInfoOpenApi.requestPriceInfo(item, "");
        if (isExistsResponseEntity(responseEntity)) {
            return LagohaltteUtil.convertStockToFinanceBaseDto(Objects.requireNonNull(responseEntity.getBody()).getResponse().getBody().getItems().getItem().get(0));
        }
        log.info("Not Insert StockName : {}", item);
        return null;
    }

    private boolean isExistsResponseEntity(ResponseEntity<StockPriceInfo> responseEntity) {
        return !Objects.requireNonNull(responseEntity.getBody()).getResponse().getBody().getTotalCount().equals("0");
    }
}
