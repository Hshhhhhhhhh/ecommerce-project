package com.hshhh.shopping.modules.payment;

import com.hshhh.shopping.modules.order.entity.Order;
import com.hshhh.shopping.modules.order.repository.OrderRepository;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.payment.dto.PaymentRequest;
import com.hshhh.shopping.modules.payment.dto.PaymentResponse;
import com.hshhh.shopping.modules.payment.entity.Payment;
import com.hshhh.shopping.modules.payment.enums.PaymentMethod;
import com.hshhh.shopping.modules.payment.enums.PaymentStatus;
import com.hshhh.shopping.modules.payment.repository.PaymentRepository;
import com.hshhh.shopping.modules.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setStatus(0); // Pending Payment

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(PaymentMethod.ALIPAY);
    }

    @Test
    void createPayment_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponse response = paymentService.createPayment(1L, paymentRequest);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertNotNull(response.getTransactionId());
        
        verify(orderService, times(1)).payOrder(1L);
        verify(paymentRepository, atLeastOnce()).save(any(Payment.class));
    }

    @Test
    void createPayment_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(1L, paymentRequest);
        });
    }

    @Test
    void createPayment_WrongUser() {
        order.setUserId(2L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(1L, paymentRequest);
        });
    }

    @Test
    void createPayment_WrongStatus() {
        order.setStatus(1); // Already paid
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(1L, paymentRequest);
        });
    }
}
