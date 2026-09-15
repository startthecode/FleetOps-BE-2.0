package com.samtar.warehouseservice.repository;

import com.samtar.warehouseservice.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
    List<WarehouseEntity> findByCode(String seller_id);
    boolean existsByCode(String seller_id);
}
