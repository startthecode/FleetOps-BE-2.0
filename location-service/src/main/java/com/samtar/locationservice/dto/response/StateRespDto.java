package com.samtar.locationservice.dto.response;

import com.samtar.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record StateRespDto(
        UUID id,
        UUID countryId,
        String name,
        String code,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
