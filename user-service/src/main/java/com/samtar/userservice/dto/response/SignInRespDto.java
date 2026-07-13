package com.samtar.userservice.dto.response;

import com.samtar.enums.ROLE;

public record SignInRespDto(
        String accessToken,
        String refreshToken,
        String username,
        ROLE role,
        Long accessExpiryMs
) {
}
