package com.lagohaltte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Document(collection = "FinanceBase")
public class FinanceBaseDto {
    @Id
    private String _id;
    private String isinCd; //국제인증고유코드
    private String itmsNm; //유가증권국제인증고유코드이름
    private String mrktCtg; //주식시장 구분
    private String srtnCd; //6자리 코드
}
