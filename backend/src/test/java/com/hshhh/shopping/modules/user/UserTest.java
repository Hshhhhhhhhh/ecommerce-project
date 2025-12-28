package com.hshhh.shopping.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hshhh.shopping.modules.user.dto.AddressDTO;
import com.hshhh.shopping.modules.user.dto.UserUpdateDTO;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.AddressRepository;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole("USER");
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProfile() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProfile() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("test@example.com");
        updateDTO.setPhone("1234567890");

        mockMvc.perform(put("/api/v1/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddressLifecycle() throws Exception {
        // 1. Add Address
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setReceiverName("John Doe");
        addressDTO.setPhone("13800138000");
        addressDTO.setDetailAddress("123 Main St");
        addressDTO.setIsDefault(true);

        mockMvc.perform(post("/api/v1/user/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.receiverName").value("John Doe"));
        
        // 2. Get Addresses and verify
        mockMvc.perform(get("/api/v1/user/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].receiverName").value("John Doe"));

        // Fetch ID from DB for update/delete
        Long addressId = addressRepository.findByUserId(testUser.getId()).get(0).getId();
        
        // 3. Update Address
        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setReceiverName("Jane Doe");
        updateDTO.setPhone("13900139000");
        updateDTO.setDetailAddress("456 Second St");
        updateDTO.setIsDefault(false);

        mockMvc.perform(put("/api/v1/user/address/" + addressId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // Verify Update
        mockMvc.perform(get("/api/v1/user/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].receiverName").value("Jane Doe"));

        // 4. Delete Address
        mockMvc.perform(delete("/api/v1/user/address/" + addressId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/user/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
