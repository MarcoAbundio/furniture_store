package com.furniturestore.product.service;

import com.furniturestore.product.dto.request.*;
import com.furniturestore.product.dto.response.*;
import com.furniturestore.product.exception.*;
import com.furniturestore.product.mapper.ProductMapper;
import com.furniturestore.product.model.*;
import com.furniturestore.product.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock StockRepository stockRepository;
    @Mock ProductMapper productMapper;
    @InjectMocks ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Sala").isActive(true).build();
        product = Product.builder().id(1L).sku("SOFA-001").name("Sofá 3 plazas")
                .category(category).price(new BigDecimal("5999.99")).isActive(true).build();
    }

    @Test
    @DisplayName("createProduct - should throw BusinessException when SKU already exists")
    void createProduct_skuExists_throwsException() {
        var request = ProductRequest.builder().sku("SOFA-001").categoryId(1L)
                .price(new BigDecimal("5999.99")).initialStock(10).build();
        when(productRepository.existsBySku("SOFA-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("SKU already exists");
    }

    @Test
    @DisplayName("createProduct - should create product and stock successfully")
    void createProduct_success() {
        var request = ProductRequest.builder().sku("TABLE-001").name("Mesa de comedor")
                .categoryId(1L).price(new BigDecimal("3500.00")).initialStock(5).minStock(2).build();
        var expectedResponse = ProductResponse.builder().id(2L).sku("TABLE-001").build();

        when(productRepository.existsBySku("TABLE-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(product);
        when(stockRepository.save(any())).thenReturn(new Stock());
        when(productMapper.toResponse(any())).thenReturn(expectedResponse);

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isNotNull();
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("getProductById - should throw ResourceNotFoundException when not found")
    void getProductById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("adjustStock - should throw BusinessException on insufficient stock")
    void adjustStock_insufficientStock() {
        var stock = Stock.builder().product(product).quantity(3).minStock(2).build();
        var request = StockAdjustmentRequest.builder().quantity(10).type("DECREMENT").build();

        when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.decrementStock(1L, 10)).thenReturn(0);

        assertThatThrownBy(() -> productService.adjustStock(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("deleteProduct - should soft delete (set isActive=false)")
    void deleteProduct_softDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        productService.deleteProduct(1L);

        assertThat(product.getIsActive()).isFalse();
        verify(productRepository).save(product);
    }
}
