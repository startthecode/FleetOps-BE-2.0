package com.samtar.notificationservice.entity;

import com.samtar.enums.InboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "inbox_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_inbox_event_id",
                        columnNames = "event_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_inbox_status_received",
                        columnList = "status, received_at"
                ),
                @Index(
                        name = "idx_inbox_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Deduplication key: the EventEnvelope.eventId of the consumed message.
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false, length = 150)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    // Serialized event body, persisted so the retry scheduler can re-process it.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Version
    private Long version;

}
