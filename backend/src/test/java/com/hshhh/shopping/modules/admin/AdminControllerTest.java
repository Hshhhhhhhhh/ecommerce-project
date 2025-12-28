package com.hshhh.shopping.modules.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hshhh.shopping.modules.admin.controller.AdminController;
import com.hshhh.shopping.modules.file.service.FileService;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.product.dto.ProductFormDTO;
import com.hshhh.shopping.modules.product.dto.ProductPageQueryDTO;
import com.hshhh.shopping.modules.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void createProduct_Success() throws Exception {
        ProductFormDTO dto = new ProductFormDTO();
        dto.setName("Test Product");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setStock(10);
        dto.setCategoryId(1L);

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).createProduct(any(ProductFormDTO.class));
    }

    @Test
    void updateProduct_Success() throws Exception {
        ProductFormDTO dto = new ProductFormDTO();
        dto.setName("Updated Product");
        dto.setPrice(new BigDecimal("150.00"));
        dto.setStock(20);
        dto.setCategoryId(1L);

        mockMvc.perform(put("/api/v1/admin/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).updateProduct(eq(1L), any(ProductFormDTO.class));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).deleteProduct(1L);
    }

    @Test
    void getProducts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/products")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).searchProducts(any(ProductPageQueryDTO.class));
    }

    @Test
    void uploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(fileService.upload(any())).thenReturn("http://localhost:8080/uploads/test.jpg");

        mockMvc.perform(multipart("/api/v1/admin/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://localhost:8080/uploads/test.jpg"));
    }
}
