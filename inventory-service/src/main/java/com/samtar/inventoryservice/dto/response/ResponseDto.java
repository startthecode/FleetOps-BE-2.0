package com.samtar.inventoryservice.dto.response;

import com.samtar.inventoryservice.constants.MessageConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ResponseDto(
        String productId,
        String warehouseId,
        int quantity,
        int reservedQuantity,
        int availableQuantity
) {
}
