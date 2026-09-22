package com.samtar.locationservice.repository;

import com.samtar.locationservice.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CityRepository extends JpaRepository<CityEntity, UUID> {
    List<CityEntity> findByState_Id(UUID stateId);
    boolean existsByState_Id(UUID stateId);
    boolean existsByState_IdAndNameIgnoreCase(UUID stateId, String name);
}
