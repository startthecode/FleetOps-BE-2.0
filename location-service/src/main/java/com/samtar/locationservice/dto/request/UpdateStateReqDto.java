package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.enums.Status;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Only stateId is required; null fields are ignored by the mapper.
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateStateReqDto(

        @NotBlank(message = MessageConstant.STATE_ID_MANDATORY)
        String stateId,

        String countryId,

        @Size(max = 100, message = MessageConstant.STATE_NAME_MAX_LENGTH)
        String name,

        @Size(max = 10, message = MessageConstant.STATE_CODE_MAX_LENGTH)
        String code,

        Status status
) {
}
