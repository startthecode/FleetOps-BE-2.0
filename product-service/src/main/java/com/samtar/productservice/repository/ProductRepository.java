package com.samtar.productservice.repository;

import com.samtar.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByCategoryId(UUID category_id);
    List<ProductEntity> findBySellerId(UUID seller_id);
    Boolean existsByProductNameIgnoreCase(String productName);
    Boolean existsBySkuNameIgnoreCase(String productSku);
}
