package com.samtar.productservice.repository;

import com.samtar.enums.OutboxStatus;
import com.samtar.productservice.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface OutBoxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
Set<OutboxEventEntity> findByStatus(OutboxStatus status);
}
