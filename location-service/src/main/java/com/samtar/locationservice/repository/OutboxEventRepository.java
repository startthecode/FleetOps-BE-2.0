package com.samtar.locationservice.repository;

import com.samtar.enums.OutboxStatus;
import com.samtar.locationservice.entity.OutboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity,Long> {
    List<OutboxEventEntity> findByStatus(OutboxStatus status, Pageable pageable);
    List<OutboxEventEntity> findByStatusAndLockedAtBefore(OutboxStatus status, Pageable pageable);
}
