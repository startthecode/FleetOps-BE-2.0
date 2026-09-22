package com.samtar.locationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.locationservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCountryReqDto(

        @NotBlank(message = MessageConstant.COUNTRY_NAME_MANDATORY)
        @Size(max = 100, message = MessageConstant.COUNTRY_NAME_MAX_LENGTH)
        String name,

        @NotBlank(message = MessageConstant.COUNTRY_ISO2_MANDATORY)
        @Size(min = 2, max = 2, message = MessageConstant.COUNTRY_ISO2_LENGTH)
        String iso2,

        @NotBlank(message = MessageConstant.COUNTRY_ISO3_MANDATORY)
        @Size(min = 3, max = 3, message = MessageConstant.COUNTRY_ISO3_LENGTH)
        String iso3,

        @Size(max = 10, message = MessageConstant.COUNTRY_PHONE_CODE_MAX_LENGTH)
        String phoneCode
) {
}
