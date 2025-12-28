package com.hshhh.shopping.modules.payment.controller;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.payment.dto.PaymentRequest;
import com.hshhh.shopping.modules.payment.dto.PaymentResponse;
import com.hshhh.shopping.modules.payment.service.PaymentService;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public Result<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        Long userId = getCurrentUserId();
        return Result.success(paymentService.createPayment(userId, request));
    }

    @GetMapping("/order/{orderId}")
    public Result<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        // In a real app, we should check if the user owns the order or is admin
        // For now, we'll just return the payment info
        return Result.success(paymentService.getPaymentByOrderId(orderId));
    }
}
