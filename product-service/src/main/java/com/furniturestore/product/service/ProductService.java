package com.furniturestore.product.service;

import com.furniturestore.product.dto.request.*;
import com.furniturestore.product.dto.response.*;
import org.springframework.data.domain.*;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> searchProducts(String query, Pageable pageable);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);

    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    StockResponse getStockByProductId(Long productId);
    StockResponse adjustStock(Long productId, StockAdjustmentRequest request);
    List<StockResponse> getLowStockProducts();
}
