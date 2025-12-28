package com.hshhh.shopping.modules.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hshhh.shopping.modules.cart.service.CartService;
import com.hshhh.shopping.modules.order.dto.CreateOrderDTO;
import com.hshhh.shopping.modules.order.dto.CreateOrderItemDTO;
import com.hshhh.shopping.modules.order.entity.Order;
import com.hshhh.shopping.modules.order.repository.OrderRepository;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import com.hshhh.shopping.modules.user.entity.Address;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.AddressRepository;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.data.redis.repositories.enabled=false")
public class OrderTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;
    
    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        addressRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

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

        testAddress = new Address();
        testAddress.setUser(testUser);
        testAddress.setReceiverName("Test Receiver");
        testAddress.setPhone("1234567890");
        testAddress.setProvince("Test Province");
        testAddress.setCity("Test City");
        testAddress.setDistrict("Test District");
        testAddress.setDetailAddress("Test Detail");
        testAddress.setIsDefault(true);
        addressRepository.save(testAddress);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateOrder() throws Exception {
        CreateOrderItemDTO itemDTO = new CreateOrderItemDTO();
        itemDTO.setProductId(testProduct.getId());
        itemDTO.setQuantity(2);

        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setAddressId(testAddress.getId());
        createOrderDTO.setItems(Collections.singletonList(itemDTO));

        // Mock CartService
        doNothing().when(cartService).removeProduct(anyLong(), anyLong());

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(200.00))
                .andExpect(jsonPath("$.data.status").value(0));
    }
}
