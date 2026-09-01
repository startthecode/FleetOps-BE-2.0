package com.samtar.inventoryservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "processed_events")
public class ProcessedEventsEntity {
    //    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "processed_id_seq")
//    @SequenceGenerator(name = "processed_id_seq",allocationSize = 30,sequenceName = "processed_id_seq" )
    @Id
    @Column(nullable = false, unique = true, name = "event_id")
    @NotNull()
    Long eventId;

    @Column(name = "event_type")
    String eventType;

    @Column(name = "processed_at")
    Instant processedAt;
}
