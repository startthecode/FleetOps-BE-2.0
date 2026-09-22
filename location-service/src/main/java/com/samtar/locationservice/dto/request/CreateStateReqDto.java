package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateStateReqDto(

        @NotBlank(message = MessageConstant.COUNTRY_ID_MANDATORY)
        String countryId,

        @NotBlank(message = MessageConstant.STATE_NAME_MANDATORY)
        @Size(max = 100, message = MessageConstant.STATE_NAME_MAX_LENGTH)
        String name,

        @NotBlank(message = MessageConstant.STATE_CODE_MANDATORY)
        @Size(max = 10, message = MessageConstant.STATE_CODE_MAX_LENGTH)
        String code
) {
}
