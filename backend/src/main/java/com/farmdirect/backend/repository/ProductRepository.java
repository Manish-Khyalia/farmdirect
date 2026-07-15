package com.farmdirect.backend.repository;

import com.farmdirect.backend.entity.Product;
import com.farmdirect.backend.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(Category category);
    List<Product> findByFarmerId(String farmerId);
    List<Product> findByVillageContainingIgnoreCase(String village);
    List<Product> findByCategoryAndVillageContainingIgnoreCase(Category category, String village);
}