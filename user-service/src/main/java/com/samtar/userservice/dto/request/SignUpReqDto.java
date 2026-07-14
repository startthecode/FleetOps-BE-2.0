package com.samtar.userservice.dto.request;

import com.samtar.enums.ROLE;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.constants.RegexConstant;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpReqDto(@NotBlank(message = "username Cannot be blank") String username,

                           @NotBlank(message = "Password can not be blank")
                           @Pattern(regexp = RegexConstant.USERNAME, message = MessageConstant.INVALID_USERNAME)
                           String password,

                           @NotBlank(message = "Email can not be blank")
                           @Pattern(regexp = RegexConstant.EMAIL, message = MessageConstant.INVALID_EMAIL)
                           String email,

                           Boolean isVendorCreation


)

{
}
