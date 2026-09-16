package com.samtar.productservice.entity;


import com.samtar.enums.Status;
import com.samtar.productservice.constants.MessageConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "processed_events")
public class ProcessedEventsEntity {
    @Id
    @NotNull(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
    @Column(unique = true,nullable = false,name = "event_id")
    UUID eventId;

    @Column(name = "event_type")
    String eventType;

    @Column(name = "processed_at")
    Instant processedAt;
}
