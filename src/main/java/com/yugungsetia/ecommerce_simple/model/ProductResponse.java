package com.yugungsetia.ecommerce_simple.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yugungsetia.ecommerce_simple.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductResponse implements Serializable {
    private Long productId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer stockQuantity;
    private BigDecimal weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryResponse> categories;


    public static ProductResponse fromProductAndCategories(Product product, List<CategoryResponse> categories) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .categories(categories)
                .build();
    }
}
