package com.yugungsetia.ecommerce_simple.repository;

import com.yugungsetia.ecommerce_simple.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
