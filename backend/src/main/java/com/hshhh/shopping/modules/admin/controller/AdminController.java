package com.hshhh.shopping.modules.admin.controller;

import com.hshhh.shopping.common.PageResult;
import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.file.service.FileService;
import com.hshhh.shopping.modules.order.dto.OrderDTO;
import com.hshhh.shopping.modules.order.service.OrderService;
import com.hshhh.shopping.modules.product.dto.ProductFormDTO;
import com.hshhh.shopping.modules.product.dto.ProductPageQueryDTO;
import com.hshhh.shopping.modules.product.service.ProductService;
import com.hshhh.shopping.modules.product.vo.ProductVO;
import com.hshhh.shopping.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理后台模块")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // Uncomment when roles are set up
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final FileService fileService;
    private final UserService userService;

    // --- Product Management ---

    @Operation(summary = "创建商品")
    @PostMapping("/products")
    public Result<Void> createProduct(@RequestBody @Valid ProductFormDTO productFormDTO) {
        productService.createProduct(productFormDTO);
        return Result.success();
    }

    @Operation(summary = "更新商品")
    @PutMapping("/products/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductFormDTO productFormDTO) {
        productService.updateProduct(id, productFormDTO);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @Operation(summary = "获取商品列表")
    @GetMapping("/products")
    public Result<PageResult<ProductVO>> getProducts(@ParameterObject ProductPageQueryDTO queryDTO) {
        return Result.success(productService.searchProducts(queryDTO));
    }

    // --- Order Management ---

    @Operation(summary = "获取所有订单")
    @GetMapping("/orders")
    public Result<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return Result.success(orderService.getAllOrders(pageable));
    }

    @Operation(summary = "订单发货")
    @PutMapping("/orders/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success();
    }

    // --- File Upload ---

    @Operation(summary = "上传图片")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.success(url);
    }

    // --- Stats ---

    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(@RequestParam(defaultValue = "day") String range) {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gmvStart;
        LocalDateTime chartStart;
        String format;
        ChronoUnit unit;

        switch (range) {
            case "year":
                // GMV: This Year
                gmvStart = LocalDate.of(now.getYear(), 1, 1).atStartOfDay();
                // Chart: Last 10 years
                chartStart = now.minusYears(9);
                format = "%Y";
                unit = ChronoUnit.YEARS;
                break;
            case "month":
                // GMV: This Month
                gmvStart = LocalDate.of(now.getYear(), now.getMonth(), 1).atStartOfDay();
                // Chart: Last 10 months
                chartStart = now.minusMonths(9);
                format = "%Y-%m";
                unit = ChronoUnit.MONTHS;
                break;
            case "day":
            default:
                // GMV: Today
                gmvStart = LocalDate.now().atStartOfDay();
                // Chart: Last 10 days
                chartStart = now.minusDays(9);
                format = "%Y-%m-%d";
                unit = ChronoUnit.DAYS;
                break;
        }

        stats.put("gmv", orderService.getTotalSales(gmvStart, now));
        stats.put("orderCount", orderService.countOrders());
        stats.put("userCount", userService.countUsers());
        stats.put("chartData", orderService.getSalesStats(chartStart, now, format, unit));
        return Result.success(stats);
    }
}
