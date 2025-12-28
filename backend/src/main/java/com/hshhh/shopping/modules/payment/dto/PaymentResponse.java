package com.hshhh.shopping.modules.payment.dto;

import com.hshhh.shopping.modules.payment.enums.PaymentMethod;
import com.hshhh.shopping.modules.payment.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
