package com.samtar.warehouseservice.repository;

import com.samtar.enums.OutboxStatus;
import com.samtar.warehouseservice.entity.OutboxEventEntity;
import com.samtar.warehouseservice.entity.WarehouseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity,Long> {
    List<OutboxEventEntity> findByStatus(OutboxStatus status, Pageable pageable);
    Set<OutboxEventEntity> findByStatusAndLockedAtBefore(OutboxStatus status, Instant lockedBefore);
}
