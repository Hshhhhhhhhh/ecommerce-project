package com.hshhh.shopping.modules.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hshhh.shopping.modules.cart.dto.CartItemDTO;
import com.hshhh.shopping.modules.cart.dto.CartSyncDTO;
import com.hshhh.shopping.modules.cart.dto.UpdateCartDTO;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.data.redis.repositories.enabled=false")
public class CartTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock 基础 Redis 连接工厂，解决 RedisKeyValueAdapter 报错
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    // Mock 响应式 Redis 连接工厂，解决 RedisReactiveAutoConfiguration 报错
    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Mock Redis Hash 操作
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        
        userRepository.deleteAll();
        productRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setStock(100);
        testProduct.setStatus(1);
        productRepository.save(testProduct);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCartLifecycle() throws Exception {
        // 1. Sync Cart (Add Item)
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(testProduct.getId());
        itemDTO.setQuantity(2);

        CartSyncDTO syncDTO = new CartSyncDTO();
        syncDTO.setItems(Collections.singletonList(itemDTO));

        mockMvc.perform(post("/api/v1/cart/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(syncDTO)))
                .andExpect(status().isOk());

        // Mock the data that should be in Redis after sync
        Map<Object, Object> cartData = new HashMap<>();
        cartData.put(testProduct.getId().toString(), 2);
        when(hashOperations.entries(anyString())).thenReturn(cartData);

        // 2. Get Cart
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.data[0].quantity").value(2));

        // 3. Update Quantity
        UpdateCartDTO updateDTO = new UpdateCartDTO();
        updateDTO.setQuantity(5);

        mockMvc.perform(patch("/api/v1/cart/items/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // Mock updated data
        cartData.put(testProduct.getId().toString(), 5);
        when(hashOperations.entries(anyString())).thenReturn(cartData);

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantity").value(5));

        // 4. Remove Item
        mockMvc.perform(delete("/api/v1/cart/items/" + testProduct.getId()))
                .andExpect(status().isOk());

        // Mock empty data
        when(hashOperations.entries(anyString())).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
