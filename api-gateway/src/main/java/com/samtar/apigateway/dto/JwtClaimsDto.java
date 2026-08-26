package com.samtar.apigateway.dto;

import com.samtar.enums.ROLE;

public record JwtClaimsDto(
        String username,
        ROLE userRole,
        String userId,
        String sessionId,
        String email
) {

}
