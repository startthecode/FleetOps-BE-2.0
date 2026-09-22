package com.samtar.locationservice.dto.response;

import com.samtar.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record CityRespDto(
        UUID id,
        UUID stateId,
        String name,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
