package com.hshhh.shopping.modules.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hshhh.shopping.modules.product.dto.StockCheckDTO;
import com.hshhh.shopping.modules.product.entity.Category;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.CategoryRepository;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setName("Electronics");
        category.setSortOrder(1);
        categoryRepository.save(category);

        product = new Product();
        product.setName("iPhone 15");
        product.setDescription("Latest Apple iPhone");
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(100);
        product.setStatus(1); // On Shelf
        product.setCategory(category);
        productRepository.save(product);
    }

    @Test
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/v1/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Electronics"));
    }

    @Test
    void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                .param("keyword", "iPhone")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetProductDetail() throws Exception {
        mockMvc.perform(get("/api/v1/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("iPhone 15"))
                .andExpect(jsonPath("$.data.categoryName").value("Electronics"));
    }

    @Test
    void testCheckStock() throws Exception {
        StockCheckDTO checkDTO = new StockCheckDTO();
        checkDTO.setProductId(product.getId());
        checkDTO.setQuantity(10);

        mockMvc.perform(post("/api/v1/products/check-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
        
        checkDTO.setQuantity(101);
        mockMvc.perform(post("/api/v1/products/check-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));
    }
}
