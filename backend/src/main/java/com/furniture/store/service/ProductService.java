package com.furniture.store.service;

import com.furniture.store.dto.product.ProductRequest;
import com.furniture.store.dto.product.ProductResponse;
import com.furniture.store.exception.ResourceNotFoundException;
import com.furniture.store.model.Category;
import com.furniture.store.model.Product;
import com.furniture.store.repository.CategoryRepository;
import com.furniture.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public Page<ProductResponse> getAll(String search, Long categoryId,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        Pageable pageable) {
        if (search == null || search.isBlank()) {
            return productRepository.findAllActiveNoSearch(categoryId, minPrice, maxPrice, pageable)
                    .map(this::toResponse);
        }
        return productRepository.findAllActiveWithSearch(search, categoryId, minPrice, maxPrice, pageable)
                .map(this::toResponse);
    }

    public Page<ProductResponse> getAllForAdmin(String search, Long categoryId, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return productRepository.findAllForAdminNoSearch(categoryId, pageable)
                    .map(this::toResponse);
        }
        return productRepository.findAllForAdminWithSearch(search, categoryId, pageable)
                .map(this::toResponse);
    }

    public ProductResponse getById(Long id) {
        Product product = findById(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product", id);
        }
        return toResponse(product);
    }

    public ProductResponse getByIdAdmin(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        if (request.getActive() != null) product.setActive(request.getActive());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    /** Reaktivira soft-delete-ovani proizvod (postavlja active = true). */
    @Transactional
    public ProductResponse activate(Long id) {
        Product product = findById(id);
        product.setActive(true);
        return toResponse(productRepository.save(product));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory() != null
                        ? categoryService.toResponse(product.getCategory()) : null)
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
