package com.samtar.inventoryservice.repository;
import com.samtar.inventoryservice.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<InventoryEntity,Long> {
    Optional<InventoryEntity> findByProductIdAndWarehouseId(UUID productId,UUID warehouseID);
    Optional<InventoryEntity> findByProductId(UUID productId);
}
