package com.samtar.locationservice.dto.response;

import com.samtar.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record CountryRespDto(
        UUID id,
        String name,
        String iso2,
        String iso3,
        String phoneCode,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
