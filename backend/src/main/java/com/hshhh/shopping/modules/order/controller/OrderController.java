package com.hshhh.shopping.modules.order.controller;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.order.dto.CreateOrderDTO;
import com.hshhh.shopping.modules.order.dto.OrderDTO;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public Result<OrderDTO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = getCurrentUserId();
        return Result.success(orderService.createOrder(userId, createOrderDTO));
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> getOrder(@PathVariable Long id) {
        // TODO: Check if order belongs to current user
        return Result.success(orderService.getOrder(id));
    }

    @GetMapping
    public Result<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return Result.success(orderService.getUserOrders(userId, PageRequest.of(page, size, Sort.by("createTime").descending())));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id) {
        // TODO: Check if current user is admin
        orderService.shipOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/send-confirmation-email")
    public Result<Void> sendConfirmationEmail(@PathVariable Long id) {
        orderService.sendConfirmationEmail(id);
        return Result.success();
    }

    @PostMapping("/confirm-by-email")
    public Result<Void> confirmByEmail(@RequestParam String token) {
        orderService.confirmOrderByToken(token);
        return Result.success();
    }
}
