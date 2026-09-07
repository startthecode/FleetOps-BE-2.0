package com.samtar.inventoryservice.repository;

import com.samtar.inventoryservice.entity.ProcessedEventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEvtRepository extends JpaRepository<ProcessedEventsEntity,Long> {
Boolean existsByEventId(UUID eventId);
}
