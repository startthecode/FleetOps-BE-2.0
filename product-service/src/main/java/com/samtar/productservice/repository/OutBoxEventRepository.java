package com.samtar.productservice.repository;

import com.samtar.enums.OutboxStatus;
import com.samtar.productservice.entity.OutboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OutBoxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findByStatus(OutboxStatus status, Pageable pageable);
    Set<OutboxEventEntity> findByStatusAndLockedAtBefore(  OutboxStatus status, Instant lockedBefore);
}
