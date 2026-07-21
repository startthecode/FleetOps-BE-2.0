package com.samtar.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samtar.userservice.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findById(UUID sessionId);
    // List<SessionEntity> findByUserId(UUID userId);
    void deleteById(UUID sessionId);

}
