package com.farmdirect.backend.service.impl;

import com.farmdirect.backend.dto.request.ProductRequest;
import com.farmdirect.backend.dto.response.ProductResponse;
import com.farmdirect.backend.entity.Product;
import com.farmdirect.backend.entity.User;
import com.farmdirect.backend.enums.Category;
import com.farmdirect.backend.exception.ResourceNotFoundException;
import com.farmdirect.backend.exception.UnauthorizedException;
import com.farmdirect.backend.repository.ProductRepository;
import com.farmdirect.backend.repository.UserRepository;
import com.farmdirect.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Convert entity to response DTO
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .unit(product.getUnit())
                .stockQty(product.getStockQty())
                .description(product.getDescription())
                .village(product.getVillage())
                .farmerName(product.getFarmer().getName())
                .farmerId(product.getFarmer().getId())
                .build();
    }

    @Override
    public ProductResponse addProduct(ProductRequest request, String farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Farmer not found"));

        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .unit(request.getUnit())
                .stockQty(request.getStockQty())
                .description(request.getDescription())
                .village(request.getVillage())
                .farmer(farmer)
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(String productId,
                                         ProductRequest request,
                                         String farmerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found"));

        // Only the farmer who owns it can update
        if (!product.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException(
                    "You are not authorized to update this product");
        }

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setStockQty(request.getStockQty());
        product.setDescription(request.getDescription());
        product.setVillage(request.getVillage());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(String productId, String farmerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found"));

        if (!product.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException(
                    "You are not authorized to delete this product");
        }

        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));
        return toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByVillage(String village) {
        return productRepository.findByVillageContainingIgnoreCase(village)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByCategoryAndVillage(
            Category category, String village) {
        return productRepository
                .findByCategoryAndVillageContainingIgnoreCase(category, village)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getMyProducts(String farmerId) {
        return productRepository.findByFarmerId(farmerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}