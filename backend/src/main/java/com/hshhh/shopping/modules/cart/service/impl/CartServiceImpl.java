package com.hshhh.shopping.modules.cart.service.impl;

import com.hshhh.shopping.common.enums.ResultCode;
import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.modules.cart.dto.CartItemDTO;
import com.hshhh.shopping.modules.cart.dto.CartSyncDTO;
import com.hshhh.shopping.modules.cart.entity.CartItem;
import com.hshhh.shopping.modules.cart.repository.CartItemRepository;
import com.hshhh.shopping.modules.cart.service.CartService;
import com.hshhh.shopping.modules.cart.vo.CartItemVO;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CartItemVO> getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return cartItems.stream()
                .map(item -> {
                    Product product = productMap.get(item.getProductId());
                    if (product == null) {
                        return null;
                    }
                    CartItemVO vo = new CartItemVO();
                    vo.setProductId(item.getProductId());
                    vo.setName(product.getName());
                    vo.setImageUrl(product.getImageUrl());
                    vo.setPrice(product.getPrice());
                    vo.setQuantity(item.getQuantity());
                    return vo;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void syncCart(Long userId, CartSyncDTO syncDTO) {
        for (CartItemDTO dto : syncDTO.getItems()) {
            if (!productRepository.existsById(dto.getProductId())) {
                continue;
            }
            
            cartItemRepository.findByUserIdAndProductId(userId, dto.getProductId())
                .ifPresentOrElse(
                    existingItem -> {
                        // 如果已存在，更新数量（这里采用覆盖策略，或者累加策略，取决于业务需求）
                        // 通常同步是指前端状态覆盖后端，或者合并。
                        // 既然是 "Sync"，且前端已经做了本地合并，这里我们假设是覆盖数量
                        // 但考虑到多端同步，累加可能更安全？
                        // 之前的 Redis 实现是 put (覆盖)。保持一致。
                        existingItem.setQuantity(dto.getQuantity());
                        cartItemRepository.save(existingItem);
                    },
                    () -> {
                        CartItem newItem = new CartItem();
                        newItem.setUserId(userId);
                        newItem.setProductId(dto.getProductId());
                        newItem.setQuantity(dto.getQuantity());
                        cartItemRepository.save(newItem);
                    }
                );
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(userId);
                    newItem.setProductId(productId);
                    return newItem;
                });
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeProduct(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
