package com.hshhh.shopping.modules.order.dto;

import lombok.Data;

@Data
public class CreateOrderItemDTO {
    private Long productId;
    private Integer quantity;
}
