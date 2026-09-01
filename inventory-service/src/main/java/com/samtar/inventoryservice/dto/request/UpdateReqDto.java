package com.samtar.inventoryservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.inventoryservice.constants.MessageConstant;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateReqDto(
        @NotBlank(message = MessageConstant.PRODUCT_ID_MANDATORY)
        String productId,

        @NotBlank(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
        String warehouseId,

        @Min(value = 0, message = MessageConstant.STOCK_QUANTITY_INVALID)
        int quantity,

        @Min(value = 0, message = MessageConstant.RESERVED_QUANTITY_INVALID)
        int reservedQuantity,

        @Min(value = 0, message = MessageConstant.AVAILABLE_QUANTITY_INVALID)
        int availableQuantity
) {
}
