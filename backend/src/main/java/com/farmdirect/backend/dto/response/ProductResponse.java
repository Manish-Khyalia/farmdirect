package com.farmdirect.backend.dto.response;

import com.farmdirect.backend.enums.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private Category category;
    private Double price;
    private String unit;
    private Integer stockQty;
    private String description;
    private String village;
    private String farmerName;
    private String farmerId;
}