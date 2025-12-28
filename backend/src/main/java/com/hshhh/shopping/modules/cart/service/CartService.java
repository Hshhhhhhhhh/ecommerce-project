package com.hshhh.shopping.modules.cart.service;

import com.hshhh.shopping.modules.cart.dto.CartSyncDTO;
import com.hshhh.shopping.modules.cart.vo.CartItemVO;

import java.util.List;

public interface CartService {

    /**
     * 获取购物车列表
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    List<CartItemVO> getCart(Long userId);

    /**
     * 同步购物车
     * @param userId 用户ID
     * @param syncDTO 同步数据
     */
    void syncCart(Long userId, CartSyncDTO syncDTO);

    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 新数量
     */
    void updateQuantity(Long userId, Long productId, Integer quantity);

    /**
     * 删除购物车商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeProduct(Long userId, Long productId);
}
