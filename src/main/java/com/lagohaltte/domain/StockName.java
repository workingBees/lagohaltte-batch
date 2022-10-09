package com.lagohaltte.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class StockName {
    private String _id;
    private String name;
}
