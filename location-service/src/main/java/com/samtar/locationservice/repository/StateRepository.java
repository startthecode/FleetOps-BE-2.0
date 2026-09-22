package com.samtar.locationservice.repository;

import com.samtar.locationservice.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StateRepository extends JpaRepository<StateEntity, UUID> {
    List<StateEntity> findByCountry_Id(UUID countryId);
    boolean existsByCountry_Id(UUID countryId);
    boolean existsByCountry_IdAndCodeIgnoreCase(UUID countryId, String code);
}
