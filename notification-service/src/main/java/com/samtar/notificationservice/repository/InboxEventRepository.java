package com.samtar.notificationservice.repository;

import com.samtar.enums.InboxStatus;
import com.samtar.notificationservice.entity.InboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEventEntity, UUID> {

    // Idempotency check: has this event already been consumed?
    boolean existsByEventId(UUID eventId);

    // Feeds the retry scheduler with events awaiting (re)processing.
    List<InboxEventEntity> findByStatus(InboxStatus status, Pageable pageable);
}
