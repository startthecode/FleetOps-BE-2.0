package com.samtar.inventoryservice.dto.response;

import com.samtar.inventoryservice.constants.MessageConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ResponseDto(
        UUID productId,
        UUID warehouseId,
        int quantity,
        int reservedQuantity,
        int availableQuantity
) {
}
