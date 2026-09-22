package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCityReqDto(

        @NotBlank(message = MessageConstant.STATE_ID_MANDATORY)
        String stateId,

        @NotBlank(message = MessageConstant.CITY_NAME_MANDATORY)
        @Size(max = 100, message = MessageConstant.CITY_NAME_MAX_LENGTH)
        String name
) {
}
