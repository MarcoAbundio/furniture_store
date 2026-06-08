package com.furniturestore.product.controller;

import com.furniturestore.product.dto.request.StockAdjustmentRequest;
import com.furniturestore.product.dto.response.StockResponse;
import com.furniturestore.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Inventory stock management")
public class StockController {

    private final ProductService productService;

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')")
    @Operation(summary = "Get stock for a product")
    public ResponseEntity<StockResponse> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getStockByProductId(productId));
    }

    @PatchMapping("/product/{productId}/adjust")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Adjust stock (Admin only)")
    public ResponseEntity<StockResponse> adjustStock(
            @PathVariable Long productId, @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(productService.adjustStock(productId, request));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get products with low stock (Admin only)")
    public ResponseEntity<List<StockResponse>> getLowStock() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
}
