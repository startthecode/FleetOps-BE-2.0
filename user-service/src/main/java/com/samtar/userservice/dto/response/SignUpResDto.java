package com.samtar.userservice.dto.response;

public record SignUpResDto(
        String accessToken,
        String refreshToken,
        String username,
        String role,
        String accessExpiryMs
) {
}
