package com.samtar.productservice.dto.response;

import com.samtar.productservice.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProductRespDto(
        String productName,
        String sku,
        String description,
        String categoryId,
        String brandId,
        BigDecimal sellingPrice,
        BigDecimal costPrice,
        Integer stockQuantity,
        Integer reorderLevel,
        String sellerId,
        String unit,
        Boolean active,
        String imageUrl,
        String manufacturer,
        String countryOfOrigin,
        String taxCode,
        Boolean taxable,
        Double taxPercentage,
        String notes
) {


}
