package com.samtar.warehouseservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.enums.Status;
import com.samtar.warehouseservice.constants.MessageConstant;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Every field except warehouseId is optional; nulls are ignored by the mapper.
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateWarehouseReqDto(

        @NotBlank(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
        String warehouseId,

        @Size(max = 150, message = MessageConstant.WAREHOUSE_NAME_MAX_LENGTH)
        String name,

        @Size(max = 50, message = MessageConstant.WAREHOUSE_CODE_MAX_LENGTH)
        String code,

        @Size(max = 255, message = MessageConstant.ADDRESS_LINE1_MAX_LENGTH)
        String addressLine1,

        @Size(max = 255, message = MessageConstant.ADDRESS_LINE2_MAX_LENGTH)
        String addressLine2,

        @Size(max = 100, message = MessageConstant.CITY_MAX_LENGTH)
        String city,

        @Size(max = 100, message = MessageConstant.STATE_MAX_LENGTH)
        String state,

        @Size(max = 100, message = MessageConstant.COUNTRY_MAX_LENGTH)
        String country,

        @Size(max = 20, message = MessageConstant.POSTAL_CODE_MAX_LENGTH)
        String postalCode,

        @DecimalMin(value = "-90.0", message = MessageConstant.LATITUDE_INVALID)
        @DecimalMax(value = "90.0", message = MessageConstant.LATITUDE_INVALID)
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = MessageConstant.LONGITUDE_INVALID)
        @DecimalMax(value = "180.0", message = MessageConstant.LONGITUDE_INVALID)
        BigDecimal longitude,

        Status status
) {
}
