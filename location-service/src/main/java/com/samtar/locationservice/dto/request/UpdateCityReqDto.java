package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.enums.Status;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Only cityId is required; null fields are ignored by the mapper.
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateCityReqDto(

        @NotBlank(message = MessageConstant.CITY_ID_MANDATORY)
        String cityId,

        String stateId,

        @Size(max = 100, message = MessageConstant.CITY_NAME_MAX_LENGTH)
        String name,

        Status status
) {
}
