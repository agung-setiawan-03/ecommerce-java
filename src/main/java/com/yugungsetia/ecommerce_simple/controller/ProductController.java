package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.model.ProductRequest;
import com.yugungsetia.ecommerce_simple.model.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.math.BigDecimal;

@RestController
@RequestMapping("products")
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ProductResponse.
                        builder()
                        .name("product" + id)
                        .price(BigDecimal.ONE)
                        .description("deskripsi produk")
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                List.of(
                        ProductResponse.builder()
                                .name("product1")
                                .price(BigDecimal.ONE)
                                .description("deskripsi produk")
                                .build(),
                        ProductResponse.builder()
                                .name("product2")
                                .price(BigDecimal.TWO)
                                .description("deskripsi produk")
                                .build()
                )
        );
    }


    @PostMapping("")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(
                ProductResponse.
                        builder()
                        .name(request.getName())
                        .price(request.getPrice())
                        .description(request.getDescription())
                        .build()
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody @Valid ProductRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(
                ProductResponse.
                        builder()
                        .name(request.getName() + " " + id)
                        .price(request.getPrice())
                        .description(request.getDescription())
                        .build()
        );
    }
}
