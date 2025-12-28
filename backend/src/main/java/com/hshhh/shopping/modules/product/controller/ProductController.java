package com.hshhh.shopping.modules.product.controller;

import com.hshhh.shopping.common.PageResult;
import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.product.dto.ProductPageQueryDTO;
import com.hshhh.shopping.modules.product.dto.StockCheckDTO;
import com.hshhh.shopping.modules.product.service.ProductService;
import com.hshhh.shopping.modules.product.vo.CategoryVO;
import com.hshhh.shopping.modules.product.vo.ProductVO;
import com.hshhh.shopping.modules.product.vo.StockCheckVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品模块")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取商品分类")
    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories() {
        return Result.success(productService.getCategories());
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<PageResult<ProductVO>> searchProducts(@ParameterObject ProductPageQueryDTO queryDTO) {
        return Result.success(productService.searchProducts(queryDTO));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @Operation(summary = "检查商品库存")
    @PostMapping("/check-stock")
    public Result<StockCheckVO> checkStock(@RequestBody @Valid StockCheckDTO checkDTO) {
        return Result.success(productService.checkStock(checkDTO));
    }
}
