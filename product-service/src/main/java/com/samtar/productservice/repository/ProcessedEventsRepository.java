package com.samtar.productservice.repository;

import com.samtar.productservice.entity.ProcessedEventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventsRepository extends JpaRepository<ProcessedEventsEntity, UUID>
{
    boolean existsByEventId(UUID eventId);
}
