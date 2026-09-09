package com.samtar.warehouseservice.dto.response;

import com.samtar.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseRespDto(
        UUID id,
        UUID sellerId,
        String name,
        String code,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String postalCode,
        BigDecimal latitude,
        BigDecimal longitude,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
