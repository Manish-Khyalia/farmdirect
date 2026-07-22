package com.farmdirect.backend.controller;

import com.farmdirect.backend.dto.request.ProductRequest;
import com.farmdirect.backend.dto.response.ProductResponse;
import com.farmdirect.backend.enums.Category;
import com.farmdirect.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // public endpoints(no token needed)

    @GetMapping("/public/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/public/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(
            @PathVariable Category category) {
        return ResponseEntity.ok(
                productService.getProductsByCategory(category));
    }

    @GetMapping("/public/village/{village}")
    public ResponseEntity<List<ProductResponse>> getByVillage(
            @PathVariable String village) {
        return ResponseEntity.ok(
                productService.getProductsByVillage(village));
    }

    @GetMapping("/public/filter")
    public ResponseEntity<List<ProductResponse>> getByFilter(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String village) {
        if (category != null && village != null) {
            return ResponseEntity.ok(
                    productService.getProductsByCategoryAndVillage(
                            category, village));
        } else if (category != null) {
            return ResponseEntity.ok(
                    productService.getProductsByCategory(category));
        } else if (village != null) {
            return ResponseEntity.ok(
                    productService.getProductsByVillage(village));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ─── FARMER ONLY ENDPOINTS (token required) ───

    @PostMapping("/farmer/add")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                productService.addProduct(request, farmerId));
    }

    @PutMapping("/farmer/update/{productId}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest request,
            Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                productService.updateProduct(productId, request, farmerId));
    }

    @DeleteMapping("/farmer/delete/{productId}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable String productId,
            Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        productService.deleteProduct(productId, farmerId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/farmer/my-products")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<ProductResponse>> getMyProducts(
            Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(
                productService.getMyProducts(farmerId));
    }
}