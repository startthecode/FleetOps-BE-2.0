package com.samtar.locationservice.entity;


import com.samtar.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_event_entity",
        indexes = {
                @Index(
                        name = "idx_outbox_status",
                        columnList = "status"
                )
        })
public class OutboxEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "outbox_event_id")
    @SequenceGenerator(name = "outbox_event_id",sequenceName = "outbox_event_id",allocationSize = 50)
    private Long id;

    @Column(name = "aggregate_Id",nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, length = 150)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Version
    private Long version;

}
