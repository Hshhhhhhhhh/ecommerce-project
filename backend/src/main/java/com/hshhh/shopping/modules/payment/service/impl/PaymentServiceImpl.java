package com.hshhh.shopping.modules.payment.service.impl;

import com.hshhh.shopping.modules.order.entity.Order;
import com.hshhh.shopping.modules.order.repository.OrderRepository;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.payment.dto.PaymentRequest;
import com.hshhh.shopping.modules.payment.dto.PaymentResponse;
import com.hshhh.shopping.modules.payment.entity.Payment;
import com.hshhh.shopping.modules.payment.enums.PaymentStatus;
import com.hshhh.shopping.modules.payment.repository.PaymentRepository;
import com.hshhh.shopping.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, PaymentRequest request) {
        // 1. Validate Order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        if (order.getStatus() != 0) { // 0: Pending Payment
            throw new RuntimeException("Order is not in pending payment state");
        }

        // 2. Create Payment Record
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setUserId(userId);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        
        paymentRepository.save(payment);

        // 3. Mock Payment Processing
        boolean paymentSuccess = processPaymentMock(payment);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
            paymentRepository.save(payment);
            
            // Update Order Status
            orderService.payOrder(order.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage("Mock payment failed");
            paymentRepository.save(payment);
            throw new RuntimeException("Payment failed");
        }

        return convertToDTO(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return convertToDTO(payment);
    }

    private boolean processPaymentMock(Payment payment) {
        // Simulate processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Always return true for now
        return true;
    }

    private PaymentResponse convertToDTO(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        return response;
    }
}
