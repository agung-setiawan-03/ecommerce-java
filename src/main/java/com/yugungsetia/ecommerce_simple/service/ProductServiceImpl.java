package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import com.yugungsetia.ecommerce_simple.entity.Category;
import com.yugungsetia.ecommerce_simple.entity.Product;
import com.yugungsetia.ecommerce_simple.entity.ProductCategory;
import com.yugungsetia.ecommerce_simple.model.CategoryResponse;
import com.yugungsetia.ecommerce_simple.model.PaginatedProductResponse;
import com.yugungsetia.ecommerce_simple.model.ProductRequest;
import com.yugungsetia.ecommerce_simple.model.ProductResponse;
import com.yugungsetia.ecommerce_simple.repository.CategoryRepository;
import com.yugungsetia.ecommerce_simple.repository.ProductCategoryRepository;
import com.yugungsetia.ecommerce_simple.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream().map(product -> {
                    List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
                    return ProductResponse.fromProductAndCategories(product, productCategories);
                })
                .toList();
    }

    @Override
    public Page<ProductResponse> findByPage(Pageable pageable) {
        return productRepository.findByPageable(pageable)
                .map(product ->  {
                    List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
                    return ProductResponse.fromProductAndCategories(product, productCategories);
                });
    }

    @Override
    public ProductResponse findById(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan dengan id : " + productId));
        List<CategoryResponse> productCategories = getProductCategories(productId);

        return ProductResponse.fromProductAndCategories(existingProduct, productCategories);
    }

    @Override
    public Page<ProductResponse> findByNameAndPageable(String name, Pageable pageable) {
        name = "%" + name + "%";
        name = name.toLowerCase();
        return productRepository.findByNamePageable(name, pageable)
                .map(product ->  {
                    List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
                    return ProductResponse.fromProductAndCategories(product, productCategories);
                });
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        List<Category> categories = getCategoriesByIds(productRequest.getCategoryIds());

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .weight(productRequest.getWeight())
                .build();

        Product createdProduct = productRepository.save(product);
        List<ProductCategory> productCategories = categories.stream()
                .map(category -> {
                    ProductCategory productCategory = ProductCategory.builder().build();
                    ProductCategory.ProductCategoryId productCategoryId = new ProductCategory.ProductCategoryId();
                    productCategoryId.setCategoryId(category.getCategoryId());
                    productCategoryId.setProductId(createdProduct.getProductId());
                    productCategory.setId(productCategoryId);
                    return productCategory;
                })
                .toList();

        productCategoryRepository.saveAll(productCategories);

        List<CategoryResponse> categoryResponseList = categories.stream()
                .map(CategoryResponse::fromCategory)
                .toList();


        return ProductResponse.fromProductAndCategories(createdProduct, categoryResponseList);
    }

    @Override
    @Transactional
    public ProductResponse update(Long productId, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan dengan id : " + productId));

        List<Category> categories = getCategoriesByIds(productRequest.getCategoryIds());

        existingProduct.setProductId(productId);
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStockQuantity(productRequest.getStockQuantity());
        existingProduct.setWeight(productRequest.getWeight());
        productRepository.save(existingProduct);

        List<ProductCategory> existingProductCategories = productCategoryRepository.findCategoriesByProductId(productId);
        productCategoryRepository.deleteAll(existingProductCategories);

        List<ProductCategory> productCategories = categories.stream()
                .map(category -> {
                    ProductCategory productCategory = ProductCategory.builder().build();
                    ProductCategory.ProductCategoryId productCategoryId = new ProductCategory.ProductCategoryId();
                    productCategoryId.setCategoryId(category.getCategoryId());
                    productCategoryId.setProductId(productId);
                    productCategory.setId(productCategoryId);
                    return productCategory;
                })
                .toList();

        productCategoryRepository.saveAll(productCategories);

        List<CategoryResponse> categoryResponseList = categories.stream()
                .map(CategoryResponse::fromCategory)
                .toList();


        return ProductResponse.fromProductAndCategories(existingProduct, categoryResponseList);
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan dengan id : " + productId));
        List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(productId);

        productCategoryRepository.deleteAll(productCategories);
        productRepository.delete(existingProduct);
    }

    @Override
    public PaginatedProductResponse convertProductPages(Page<ProductResponse> productPage) {
        return PaginatedProductResponse.builder()
                .data(productPage.getContent())
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }


    private List<Category> getCategoriesByIds(List<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan dengan id : " + categoryId)))
                .toList();
    }


    private List<CategoryResponse> getProductCategories(Long productId) {
        List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(productId);
        List<Long> categoryIds = productCategories.stream()
                .map(productCategory -> productCategory.getId().getProductId())
                .toList();
        return categoryRepository.findAllById(categoryIds)
                .stream().map(CategoryResponse::fromCategory)
                .toList();
    }
}
