package com.samtar.dto.cache;

import java.util.UUID;

public record SessionRespDto(
        UUID id,
        String ipAddress,
        UUID userid
) {
}
