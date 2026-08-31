package com.samtar.dto.cache;

import com.samtar.enums.ROLE;
import java.util.UUID;

public record SessionCache(
        String username,
        String email,
        ROLE role,
        UUID id
) {
    public SessionCache {
    }
}
