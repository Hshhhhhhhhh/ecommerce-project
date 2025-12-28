package com.hshhh.shopping.modules.order.service;

import com.hshhh.shopping.modules.admin.dto.DailySalesDTO;
import com.hshhh.shopping.modules.order.dto.CreateOrderDTO;
import com.hshhh.shopping.modules.order.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId, CreateOrderDTO createOrderDTO);
    OrderDTO getOrder(Long id);
    Page<OrderDTO> getUserOrders(Long userId, Pageable pageable);
    Page<OrderDTO> getAllOrders(Pageable pageable);
    void cancelOrder(Long id);
    void payOrder(Long id); // 模拟支付
    void shipOrder(Long id); // 发货
    void completeOrder(Long id); // 收货/完成

    void sendConfirmationEmail(Long orderId);
    void confirmOrderByToken(String token);

    long countOrders();
    BigDecimal getTotalSales();
    BigDecimal getTotalSales(LocalDateTime start, LocalDateTime end);
    List<DailySalesDTO> getSalesStats(LocalDateTime start, LocalDateTime end, String format, ChronoUnit unit);
}
