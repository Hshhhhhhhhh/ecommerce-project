package com.hshhh.shopping.modules.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {
    private Long addressId;
    private String remark;
    // 如果是从购物车结算，可以传选中的商品ID列表，或者直接传商品+数量列表（如果是立即购买）
    // 这里为了简单通用，我们让前端传商品列表
    private List<CreateOrderItemDTO> items;
}
