package com.hshhh.shopping.modules.payment.service;

import com.hshhh.shopping.modules.payment.dto.PaymentRequest;
import com.hshhh.shopping.modules.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Long userId, PaymentRequest request);
    PaymentResponse getPaymentByOrderId(Long orderId);
}
