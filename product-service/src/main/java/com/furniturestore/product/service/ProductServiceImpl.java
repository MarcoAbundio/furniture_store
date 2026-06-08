package com.furniturestore.product.service;

import com.furniturestore.product.dto.request.*;
import com.furniturestore.product.dto.response.*;
import com.furniturestore.product.exception.*;
import com.furniturestore.product.mapper.ProductMapper;
import com.furniturestore.product.model.*;
import com.furniturestore.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final ProductMapper productMapper;

    // ===== PRODUCT OPERATIONS =====

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku()))
            throw new BusinessException("SKU already exists: " + request.getSku());

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        var product = Product.builder()
                .sku(request.getSku()).name(request.getName()).description(request.getDescription())
                .category(category).price(request.getPrice()).weightKg(request.getWeightKg())
                .dimensions(request.getDimensions()).material(request.getMaterial())
                .color(request.getColor()).brand(request.getBrand()).isActive(true).build();

        product = productRepository.save(product);

        var stock = Stock.builder()
                .product(product)
                .quantity(request.getInitialStock())
                .minStock(request.getMinStock() != null ? request.getMinStock() : 5)
                .build();
        stockRepository.save(stock);

        var response = productMapper.toResponse(product);
        response.setStockQuantity(stock.getQuantity());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        var response = productMapper.toResponse(product);
        stockRepository.findByProductId(id).ifPresent(s -> response.setStockQuantity(s.getQuantity()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        var product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        var response = productMapper.toResponse(product);
        stockRepository.findByProductId(product.getId()).ifPresent(s -> response.setStockQuantity(s.getQuantity()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable).map(p -> {
            var r = productMapper.toResponse(p);
            stockRepository.findByProductId(p.getId()).ifPresent(s -> r.setStockQuantity(s.getQuantity()));
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        return productRepository.findByCategoryId(categoryId, pageable).map(p -> {
            var r = productMapper.toResponse(p);
            stockRepository.findByProductId(p.getId()).ifPresent(s -> r.setStockQuantity(s.getQuantity()));
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.search(query, pageable).map(p -> {
            var r = productMapper.toResponse(p);
            stockRepository.findByProductId(p.getId()).ifPresent(s -> r.setStockQuantity(s.getQuantity()));
            return r;
        });
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku()))
            throw new BusinessException("SKU already in use: " + request.getSku());

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        product.setSku(request.getSku()); product.setName(request.getName());
        product.setDescription(request.getDescription()); product.setCategory(category);
        product.setPrice(request.getPrice()); product.setWeightKg(request.getWeightKg());
        product.setDimensions(request.getDimensions()); product.setMaterial(request.getMaterial());
        product.setColor(request.getColor()); product.setBrand(request.getBrand());

        var saved = productRepository.save(product);
        var response = productMapper.toResponse(saved);
        stockRepository.findByProductId(id).ifPresent(s -> response.setStockQuantity(s.getQuantity()));
        return response;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    // ===== CATEGORY OPERATIONS =====

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new BusinessException("Category already exists: " + request.getName());

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + request.getParentId()));
        }

        var category = Category.builder()
                .name(request.getName()).description(request.getDescription()).parent(parent).isActive(true).build();

        return productMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return productMapper.toCategoryResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAllActive(pageable).map(productMapper::toCategoryResponse);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        category.setName(request.getName()); category.setDescription(request.getDescription());
        return productMapper.toCategoryResponse(categoryRepository.save(category));
    }

    // ===== STOCK OPERATIONS =====

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockByProductId(Long productId) {
        return productMapper.toStockResponse(stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + productId)));
    }

    @Override
    @Transactional
    public StockResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        var stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + productId));

        if ("INCREMENT".equalsIgnoreCase(request.getType())) {
            int rows = stockRepository.incrementStock(productId, request.getQuantity());
            if (rows == 0) throw new BusinessException("Stock update failed");
        } else if ("DECREMENT".equalsIgnoreCase(request.getType())) {
            int rows = stockRepository.decrementStock(productId, request.getQuantity());
            if (rows == 0) throw new BusinessException("Insufficient stock or stock update failed");
        } else {
            throw new BusinessException("Invalid adjustment type. Use INCREMENT or DECREMENT");
        }

        return productMapper.toStockResponse(stockRepository.findByProductId(productId).orElse(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStockProducts() {
        return stockRepository.findLowStock().stream().map(productMapper::toStockResponse).toList();
    }
}
