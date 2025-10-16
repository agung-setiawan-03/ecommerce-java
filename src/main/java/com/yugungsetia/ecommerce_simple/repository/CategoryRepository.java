package com.yugungsetia.ecommerce_simple.repository;

import com.yugungsetia.ecommerce_simple.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
