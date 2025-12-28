package com.hshhh.shopping.modules.product.service;

import com.hshhh.shopping.common.PageResult;
import com.hshhh.shopping.modules.product.dto.ProductFormDTO;
import com.hshhh.shopping.modules.product.dto.ProductPageQueryDTO;
import com.hshhh.shopping.modules.product.dto.StockCheckDTO;
import com.hshhh.shopping.modules.product.vo.CategoryVO;
import com.hshhh.shopping.modules.product.vo.ProductVO;
import com.hshhh.shopping.modules.product.vo.StockCheckVO;

import java.util.List;

public interface ProductService {

    List<CategoryVO> getCategories();

    PageResult<ProductVO> searchProducts(ProductPageQueryDTO queryDTO);

    ProductVO getProductDetail(Long id);

    StockCheckVO checkStock(StockCheckDTO checkDTO);

    void createProduct(ProductFormDTO productFormDTO);

    void updateProduct(Long id, ProductFormDTO productFormDTO);

    void deleteProduct(Long id);
}
