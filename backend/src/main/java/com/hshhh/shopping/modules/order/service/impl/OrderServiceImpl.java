package com.hshhh.shopping.modules.order.service.impl;

import com.hshhh.shopping.modules.admin.dto.DailySalesDTO;
import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.modules.cart.service.CartService;
import com.hshhh.shopping.modules.order.dto.CreateOrderDTO;
import com.hshhh.shopping.modules.order.dto.CreateOrderItemDTO;
import com.hshhh.shopping.modules.order.dto.OrderDTO;
import com.hshhh.shopping.modules.order.dto.OrderItemDTO;
import com.hshhh.shopping.modules.order.entity.Order;
import com.hshhh.shopping.modules.order.entity.OrderItem;
import com.hshhh.shopping.modules.order.repository.OrderRepository;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import com.hshhh.shopping.modules.user.entity.Address;
import com.hshhh.shopping.modules.user.repository.AddressRepository;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import com.hshhh.shopping.common.service.EmailService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Simple in-memory storage for tokens (Token -> OrderId)
    // In production, use Redis or Database
    private static final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        // 1. 获取地址信息
        Address address = addressRepository.findById(createOrderDTO.getAddressId())
                .orElseThrow(() -> new BusinessException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Invalid address");
        }

        // 2. 创建订单对象
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(address.getId());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setStatus(0); // 待支付

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. 处理订单项
        for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new BusinessException("Product not found: " + itemDTO.getProductId()));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            // 扣减库存
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImageUrl());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            
            orderItems.add(orderItem);

            // 计算总价
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            
            // 从购物车移除
            cartService.removeProduct(userId, product.getId());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 4. 保存订单
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }

    @Override
    public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        List<OrderDTO> dtos = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orderPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDTO> dtos = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != 0) { // 只能取消待支付订单
            throw new RuntimeException("Cannot cancel order in current status");
        }
        order.setStatus(4); // 已取消
        
        // 恢复库存
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
        
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found"));
        if (order.getStatus() != 0) {
            throw new BusinessException("Order status is not pending payment");
        }
        order.setStatus(1); // 待发货
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != 1) {
            throw new RuntimeException("Order status is not pending shipment");
        }
        order.setStatus(2); // 待收货
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != 2) {
            throw new RuntimeException("Order status is not pending receipt");
        }
        order.setStatus(3); // 已完成
        orderRepository.save(order);
    }

    @Override
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    public BigDecimal getTotalSales() {
        BigDecimal total = orderRepository.sumTotalAmountPaid();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalSales(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = orderRepository.sumTotalAmountPaidBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<DailySalesDTO> getSalesStats(LocalDateTime start, LocalDateTime end, String format, ChronoUnit unit) {
        List<DailySalesDTO> dbStats = orderRepository.getSalesStats(start, end, format);
        Map<String, BigDecimal> statsMap = dbStats.stream()
                .collect(Collectors.toMap(DailySalesDTO::getDate, DailySalesDTO::getAmount));

        List<DailySalesDTO> result = new ArrayList<>();
        LocalDateTime current = start;
        DateTimeFormatter formatter;
        
        // Java DateTimeFormatter patterns are slightly different from MySQL DATE_FORMAT
        // MySQL %Y-%m-%d -> Java yyyy-MM-dd
        // MySQL %Y-%m -> Java yyyy-MM
        // MySQL %Y -> Java yyyy
        if (format.contains("%d")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if (format.contains("%m")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }

        while (!current.isAfter(end)) {
            String key = current.format(formatter);
            result.add(new DailySalesDTO(key, statsMap.getOrDefault(key, BigDecimal.ZERO)));
            current = current.plus(1, unit);
        }

        return result;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        
        if (order.getItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                BeanUtils.copyProperties(item, itemDTO);
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }
        
        return dto;
    }

    @Override
    public void sendConfirmationEmail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Generate token with OrderID embedded: Base64(orderId:uuid)
        String uuid = UUID.randomUUID().toString();
        String rawToken = orderId + ":" + uuid;
        String token = java.util.Base64.getEncoder().encodeToString(rawToken.getBytes());
        
        // Store in local map (Simplified for demo without Redis)
        tokenStore.put(token, orderId);
        
        // Construct link
        String link = frontendUrl + "/order/confirm?token=" + token;
        
        // Get user email
        String email = "customer@example.com"; // Default
        try {
             com.hshhh.shopping.modules.user.entity.User user = userRepository.findById(order.getUserId()).orElse(null);
             if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                 email = user.getEmail();
             }
        } catch (Exception e) {
            // Ignore
        }
        
        emailService.sendSimpleMessage(email, "Order Confirmation", "Please click the link to confirm receipt: " + link);
    }

    @Override
    @Transactional
    public void confirmOrderByToken(String token) {
        // 1. Try to extract OrderID from token first to check status (even if server restarted)
        try {
            String decoded = new String(java.util.Base64.getDecoder().decode(token));
            if (decoded.contains(":")) {
                String[] parts = decoded.split(":");
                Long extractedOrderId = Long.parseLong(parts[0]);
                Order order = orderRepository.findById(extractedOrderId).orElse(null);
                if (order != null && order.getStatus() == 3) {
                    throw new BusinessException("该订单已确认收货");
                }
            }
        } catch (Exception e) {
            // Ignore decoding errors, fall through to standard validation
        }

        // 2. Standard validation
        Long orderId = tokenStore.get(token);
        if (orderId == null) {
            throw new RuntimeException("Invalid or expired confirmation link");
        }
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == 3) { // If already DONE
            throw new BusinessException("该订单已确认收货");
        }
        
        order.setStatus(3); // DONE
        orderRepository.save(order);
        
        // tokenStore.remove(token); // Keep token to allow duplicate checks
    }
}
