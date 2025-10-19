package com.yugungsetia.ecommerce_simple.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCartItemRequest {

    @NotNull(message = "Produk id wajib diisi")
    private Long productId;

    @NotNull(message = "Quantity wajib diisi")
    @Min(value = 1, message = "Quantity harus minimal 1")
    private Integer quantity;
}
