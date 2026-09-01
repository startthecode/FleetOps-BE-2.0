package com.samtar.inventoryservice.repository;

import com.samtar.inventoryservice.entity.ProcessedEventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEvtRepository extends JpaRepository<ProcessedEventsEntity,Long> {

}
