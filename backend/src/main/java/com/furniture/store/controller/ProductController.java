package com.furniture.store.controller;

import com.furniture.store.dto.product.ProductRequest;
import com.furniture.store.dto.product.ProductResponse;
import com.furniture.store.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(search, categoryId, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(productService.activate(id));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getAllAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllForAdmin(search, categoryId, pageable));
    }

    /**
     * Admin verzija getById koja vraća i neaktivne (soft-delete-ovane) proizvode.
     * Public getById baca 404 za neaktivne da bi ih sakrio od običnog kataloga,
     * ali admin form za izmjenu treba da ih može učitati radi reaktivacije.
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> getByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getByIdAdmin(id));
    }
}
