package com.hshhh.shopping.modules.product.service.impl;

import com.hshhh.shopping.common.PageResult;
import com.hshhh.shopping.common.enums.ResultCode;
import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.modules.product.dto.ProductFormDTO;
import com.hshhh.shopping.modules.product.dto.ProductPageQueryDTO;
import com.hshhh.shopping.modules.product.dto.StockCheckDTO;
import com.hshhh.shopping.modules.product.entity.Category;
import com.hshhh.shopping.modules.product.entity.Product;
import com.hshhh.shopping.modules.product.repository.CategoryRepository;
import com.hshhh.shopping.modules.product.repository.ProductRepository;
import com.hshhh.shopping.modules.product.service.ProductService;
import com.hshhh.shopping.modules.product.vo.CategoryVO;
import com.hshhh.shopping.modules.product.vo.ProductVO;
import com.hshhh.shopping.modules.product.vo.StockCheckVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryVO> getCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ProductVO> searchProducts(ProductPageQueryDTO queryDTO) {
        int page = Math.max(queryDTO.getPage() - 1, 0);
        int size = queryDTO.getSize();
        
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(queryDTO.getSort())) {
            if ("price_asc".equals(queryDTO.getSort())) {
                sort = Sort.by(Sort.Direction.ASC, "price");
            } else if ("price_desc".equals(queryDTO.getSort())) {
                sort = Sort.by(Sort.Direction.DESC, "price");
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Only show products on shelf
            predicates.add(cb.equal(root.get("status"), 1));

            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String likePattern = "%" + queryDTO.getKeyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), likePattern),
                        cb.like(root.get("description"), likePattern)
                ));
            }

            if (queryDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), queryDTO.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductVO> items = productPage.getContent().stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());

        return new PageResult<>(items, productPage.getTotalElements(), queryDTO.getPage(), size);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.PRODUCT_NOT_EXIST));
        return convertToProductVO(product);
    }

    @Override
    public StockCheckVO checkStock(StockCheckDTO checkDTO) {
        Product product = productRepository.findById(checkDTO.getProductId())
                .orElseThrow(() -> new BusinessException(ResultCode.PRODUCT_NOT_EXIST));
        
        boolean available = product.getStock() >= checkDTO.getQuantity();
        return new StockCheckVO(available, product.getStock());
    }

    @Override
    public void createProduct(ProductFormDTO productFormDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productFormDTO, product);
        
        // Set default status if not provided
        if (product.getStatus() == null) {
            product.setStatus(1); // Default to On Shelf
        }
        
        Category category = categoryRepository.findById(productFormDTO.getCategoryId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        product.setCategory(category);
        
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Long id, ProductFormDTO productFormDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.PRODUCT_NOT_EXIST));
        
        // Copy properties but ignore null values to prevent overwriting existing data with nulls
        // However, BeanUtils.copyProperties copies everything by default.
        // We need to be careful. Here we assume productFormDTO has all fields.
        // But wait, if status is null in DTO, it will set product status to null!
        
        // Manual copy or use a helper to ignore nulls. 
        // For now, let's manually set fields or ensure DTO has defaults.
        // Better approach: Only update fields that are present in DTO, or ensure DTO is fully populated.
        
        product.setName(productFormDTO.getName());
        product.setDescription(productFormDTO.getDescription());
        product.setPrice(productFormDTO.getPrice());
        product.setStock(productFormDTO.getStock());
        product.setImageUrl(productFormDTO.getImageUrl());
        
        if (productFormDTO.getStatus() != null) {
            product.setStatus(productFormDTO.getStatus());
        }
        
        Category category = categoryRepository.findById(productFormDTO.getCategoryId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        product.setCategory(category);
        
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        productRepository.deleteById(id);
    }

    private CategoryVO convertToCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        // TODO: Implement product count if needed
        vo.setProductCount(0); 
        return vo;
    }

    private ProductVO convertToProductVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        if (product.getCategory() != null) {
            vo.setCategoryName(product.getCategory().getName());
        }
        return vo;
    }
}
