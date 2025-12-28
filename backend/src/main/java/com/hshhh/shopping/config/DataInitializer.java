package com.hshhh.shopping.config;

import com.hshhh.shopping.modules.auth.dto.UserRegisterDTO;
import com.hshhh.shopping.modules.auth.service.AuthService;
import com.hshhh.shopping.modules.product.entity.Category;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.CategoryRepository;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initAdmin();
            initTestUser();
            initProducts();
        };
    }

    private void initAdmin() {
        String adminUsername = "admin";
        userRepository.findByUsername(adminUsername).ifPresentOrElse(
            user -> {
                if (!"ADMIN".equals(user.getRole())) {
                    log.info("Updating admin user role...");
                    user.setRole("ADMIN");
                    userRepository.save(user);
                }
            },
            () -> {
                log.info("Creating admin user...");
                User admin = new User();
                admin.setUsername(adminUsername);
                // Password: admin123 (SHA-256 hashed)
                admin.setPassword(passwordEncoder.encode("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"));
                admin.setEmail("admin@example.com");
                admin.setRole("ADMIN");
                userRepository.save(admin);
                log.info("Admin user created. Username: admin, Password: admin123");
            }
        );
    }

    private void initTestUser() {
        String testUsername = "testuser";
        if (userRepository.findByUsername(testUsername).isEmpty()) {
            log.info("Creating test user...");
            UserRegisterDTO registerDTO = new UserRegisterDTO();
            registerDTO.setUsername(testUsername);
            // Password: 123456 (SHA-256 hashed)
            registerDTO.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
            registerDTO.setEmail("test@example.com");
            try {
                authService.register(registerDTO);
                log.info("Test user created. Username: testuser, Password: 123456");
            } catch (Exception e) {
                log.error("Failed to create test user: " + e.getMessage());
            }
        } else {
            log.info("Test user already exists.");
        }
    }

    private void initProducts() {
        if (categoryRepository.count() > 0) {
            log.info("Products already initialized.");
            return;
        }

        log.info("Initializing products...");

        Category digital = createCategory("数码产品", 1);
        Category audio = createCategory("音频配件", 2);
        Category computer = createCategory("电脑配件", 3);
        Category accessories = createCategory("数码配件", 4);

        createProduct("旗舰智能手机 Pro Max", "搭载最新A17芯片，120Hz屏幕", new BigDecimal("5999"), 100, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400", digital);
        createProduct("超轻薄笔记本 15寸", "轻至1.2kg，16GB内存", new BigDecimal("8299"), 50, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400", digital);
        createProduct("无线降噪耳机", "主动降噪，40小时续航", new BigDecimal("1999"), 200, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400", audio);
        createProduct("人体工学键盘", "机械轴体，RGB背光", new BigDecimal("599"), 150, "https://images.unsplash.com/photo-1587829741301-dc798b83aca2?w=400", computer);
        createProduct("4K显示器 27寸", "4K分辨率，HDR400", new BigDecimal("3299"), 30, "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400", computer);
        createProduct("无线充电器", "15W快充，支持多设备", new BigDecimal("299"), 300, "https://images.unsplash.com/photo-1591290619762-c588f8ed1906?w=400", accessories);
        createProduct("智能手表", "健康监测，GPS定位", new BigDecimal("2499"), 80, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400", digital);
        createProduct("蓝牙音箱", "360度环绕音效", new BigDecimal("899"), 120, "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400", audio);
        createProduct("游戏鼠标", "16000DPI，可编程按键", new BigDecimal("399"), 200, "https://images.unsplash.com/photo-1527814050087-3793815479db?w=400", computer);
        createProduct("移动电源 20000mAh", "双向快充，轻薄便携", new BigDecimal("199"), 500, "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400", accessories);

        log.info("Products initialized.");
    }

    private Category createCategory(String name, Integer sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setSortOrder(sortOrder);
        return categoryRepository.save(category);
    }

    private void createProduct(String name, String description, BigDecimal price, Integer stock, String imageUrl, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setStatus(1);
        productRepository.save(product);
    }
}
