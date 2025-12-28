package com.hshhh.shopping.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存检查结果对象")
public class StockCheckVO {

    @Schema(description = "是否有货")
    private Boolean available;

    @Schema(description = "当前库存")
    private Integer currentStock;
}
