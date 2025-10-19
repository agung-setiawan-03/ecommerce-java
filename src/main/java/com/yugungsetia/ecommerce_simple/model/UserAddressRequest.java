package com.yugungsetia.ecommerce_simple.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserAddressRequest {

    @NotBlank(message = "Tipe Alamat wajib diisi s.c (Rumah atau Kantor)")
    @Size(max = 100, message = "Tipe Alamat tidak boleh lebih dari 100 karakter")
    private String addressName;

    @NotBlank(message = "Detail Alamat wajib diisi")
    @Size(max = 255, message = "Detail Alamat tidak boleh lebih dari 255 karakter")
    private String streetAddress;

    @NotBlank(message = "Kota wajib diisi")
    @Size(max = 100, message = "Kota tidak boleh lebih dari 100 karakter")
    private String city;

    @NotBlank(message = "Provinsi wajib diisi")
    @Size(max = 100, message = "Provinsi tidak boleh lebih dari 100 karakter")
    private String state;

    @NotBlank(message = "kode Pos wajib diisi")
    @Size(max = 20, message = "Kode pos tidak boleh lebih dari 20 karakter")
    private String postalCode;

    @NotBlank(message = "Negara wajib diisi")
    @Size(max = 100, message = "Negara tidak boleh lebih dari 100 karakter")
    private String country;

    private boolean isDefault;
}
