package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.PaginatedProductResponse;
import com.yugungsetia.ecommerce_simple.model.ProductRequest;
import com.yugungsetia.ecommerce_simple.model.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponse> findAll();

    Page<ProductResponse> findByPage(Pageable pageable);

    ProductResponse findById(Long id);

    Page<ProductResponse> findByNameAndPageable(String name, Pageable pageable);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(Long productId, ProductRequest productRequest);

    void delete(Long id);

    PaginatedProductResponse convertProductPages(Page<ProductResponse> productPage);
}
