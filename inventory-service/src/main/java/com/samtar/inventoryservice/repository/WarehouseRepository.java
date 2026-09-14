package com.samtar.inventoryservice.repository;

import com.samtar.inventoryservice.entity.referenceEntity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
    boolean existsByWarehouseId(UUID warehouseId);
}
