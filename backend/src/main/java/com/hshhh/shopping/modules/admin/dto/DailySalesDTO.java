package com.hshhh.shopping.modules.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DailySalesDTO {
    private String date;
    private BigDecimal amount;

    public DailySalesDTO(Object date, BigDecimal amount) {
        this.date = date != null ? date.toString() : "";
        this.amount = amount;
    }
}
