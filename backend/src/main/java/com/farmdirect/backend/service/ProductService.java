package com.farmdirect.backend.service;

import com.farmdirect.backend.dto.request.ProductRequest;
import com.farmdirect.backend.dto.response.ProductResponse;
import com.farmdirect.backend.enums.Category;
import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductRequest request, String farmerId);
    ProductResponse updateProduct(String productId, ProductRequest request, String farmerId);
    void deleteProduct(String productId, String farmerId);
    ProductResponse getProductById(String productId);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(Category category);
    List<ProductResponse> getProductsByVillage(String village);
    List<ProductResponse> getProductsByCategoryAndVillage(Category category, String village);
    List<ProductResponse> getMyProducts(String farmerId);
}