package com.samtar.warehouseservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.enums.Status;
import com.samtar.warehouseservice.constants.MessageConstant;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateWarehouseReqDto(

        @NotBlank(message = MessageConstant.WAREHOUSE_NAME_MANDATORY)
        @Size(max = 150, message = MessageConstant.WAREHOUSE_NAME_MAX_LENGTH)
        String name,

        @NotBlank(message = MessageConstant.WAREHOUSE_CODE_MANDATORY)
        @Size(max = 50, message = MessageConstant.WAREHOUSE_CODE_MAX_LENGTH)
        String code,

        @NotBlank(message = MessageConstant.ADDRESS_LINE1_MANDATORY)
        @Size(max = 255, message = MessageConstant.ADDRESS_LINE1_MAX_LENGTH)
        String addressLine1,

        @Size(max = 255, message = MessageConstant.ADDRESS_LINE2_MAX_LENGTH)
        String addressLine2,

        @NotBlank(message = MessageConstant.CITY_MANDATORY)
        @Size(max = 100, message = MessageConstant.CITY_MAX_LENGTH)
        String city,

        @NotBlank(message = MessageConstant.POSTAL_CODE_MANDATORY)
        @Size(max = 20, message = MessageConstant.POSTAL_CODE_MAX_LENGTH)
        String postalCode,

        @DecimalMin(value = "-90.0", message = MessageConstant.LATITUDE_INVALID)
        @DecimalMax(value = "90.0", message = MessageConstant.LATITUDE_INVALID)
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = MessageConstant.LONGITUDE_INVALID)
        @DecimalMax(value = "180.0", message = MessageConstant.LONGITUDE_INVALID)
        BigDecimal longitude

) {

}
