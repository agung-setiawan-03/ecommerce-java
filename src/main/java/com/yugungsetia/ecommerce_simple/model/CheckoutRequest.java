package com.yugungsetia.ecommerce_simple.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckoutRequest {
    private Long userId;

    @NotEmpty(message = "Minimal satu item di keranjang belanja di pilih untuk checkout")
    @Size(min = 1, message = "Minimal satu item harus di pilih")
    private List<Long> selectedCartItemIds;

    @NotNull(message = "Id Alamat user wajib diisi")
    private Long userAddressId;
}
