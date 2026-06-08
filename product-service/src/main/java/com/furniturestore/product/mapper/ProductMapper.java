package com.furniturestore.product.mapper;

import com.furniturestore.product.dto.response.*;
import com.furniturestore.product.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "stockQuantity", ignore = true)
    ProductResponse toResponse(Product product);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "isLowStock", expression = "java(stock.getQuantity() <= stock.getMinStock())")
    StockResponse toStockResponse(Stock stock);
}
