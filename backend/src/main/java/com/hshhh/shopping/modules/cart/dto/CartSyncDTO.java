package com.hshhh.shopping.modules.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "购物车同步参数")
public class CartSyncDTO {

    @Schema(description = "购物车商品列表")
    @NotNull(message = "商品列表不能为空")
    @Valid
    private List<CartItemDTO> items;
}
