package com.hshhh.shopping.modules.cart.controller;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.cart.dto.CartSyncDTO;
import com.hshhh.shopping.modules.cart.dto.UpdateCartDTO;
import com.hshhh.shopping.modules.cart.service.CartService;
import com.hshhh.shopping.modules.cart.vo.CartItemVO;
import com.hshhh.shopping.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车模块")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Operation(summary = "获取购物车列表")
    @GetMapping
    public Result<List<CartItemVO>> getCart() {
        Long userId = getCurrentUserId();
        return Result.success(cartService.getCart(userId));
    }

    @Operation(summary = "同步购物车")
    @PostMapping("/sync")
    public Result<Void> syncCart(@RequestBody @Valid CartSyncDTO syncDTO) {
        Long userId = getCurrentUserId();
        cartService.syncCart(userId, syncDTO);
        return Result.success();
    }

    @Operation(summary = "更新购物车商品数量")
    @PatchMapping("/items/{productId}")
    public Result<Void> updateQuantity(@PathVariable Long productId, @RequestBody @Valid UpdateCartDTO updateDTO) {
        Long userId = getCurrentUserId();
        cartService.updateQuantity(userId, productId, updateDTO.getQuantity());
        return Result.success();
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/items/{productId}")
    public Result<Void> removeProduct(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        cartService.removeProduct(userId, productId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserIdByUsername(username);
    }
}
