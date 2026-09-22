package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.enums.Status;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Only countryId is required; null fields are ignored by the mapper.
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateCountryReqDto(

        @NotBlank(message = MessageConstant.COUNTRY_ID_MANDATORY)
        String countryId,

        @Size(max = 100, message = MessageConstant.COUNTRY_NAME_MAX_LENGTH)
        String name,

        @Size(min = 2, max = 2, message = MessageConstant.COUNTRY_ISO2_LENGTH)
        String iso2,

        @Size(min = 3, max = 3, message = MessageConstant.COUNTRY_ISO3_LENGTH)
        String iso3,

        @Size(max = 10, message = MessageConstant.COUNTRY_PHONE_CODE_MAX_LENGTH)
        String phoneCode,

        Status status
) {
}
