package com.farmdirect.backend.dto.request;

import com.farmdirect.backend.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private String unit;

    @NotNull(message = "Stock quantity is required")
    @Positive(message = "Stock must be positive")
    private Integer stockQty;

    private String description;

    @NotBlank(message = "Village name is required")
    private String village;
}