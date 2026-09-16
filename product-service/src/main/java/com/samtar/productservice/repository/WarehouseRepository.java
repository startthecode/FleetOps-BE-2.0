package com.samtar.productservice.repository;

import com.samtar.productservice.entity.referenceEntity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
    boolean existsByWarehouseId(UUID warehouseId);
}
