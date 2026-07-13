package com.samtar.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignInReqDto(
        @NotBlank(message = "username Cannot be blank")
        String username,

        @NotBlank(message = "Password can not be blank")
        String password
        ) {
}
