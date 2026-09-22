package com.samtar.locationservice.repository;

import com.samtar.locationservice.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByIso2IgnoreCase(String iso2);
    boolean existsByIso3IgnoreCase(String iso3);
}
