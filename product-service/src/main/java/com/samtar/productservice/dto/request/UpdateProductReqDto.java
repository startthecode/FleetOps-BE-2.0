package com.samtar.productservice.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.productservice.constants.MessageConstant;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateProductReqDto(
        @NotBlank(message = MessageConstant.PRODUCT_ID_MANDATORY)
        String productId,

        @NotBlank(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
        String warehouseId,


        @Size(max = 255, message = MessageConstant.PRODUCT_NAME_MAX_LENGTH)
        String productName,


        @Size(max = 100, message = MessageConstant.PRODUCT_SKU_MAX_LENGTH)
        String sku,


        @Size(max = 1000, message = MessageConstant.PRODUCT_DESCRIPTION_MAX_LENGTH)
        String description,


        String categoryId,


        String brandId,


        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = MessageConstant.PRODUCT_SELLING_PRICE_INVALID
        )
        BigDecimal sellingPrice,


        @DecimalMin(
                value = "0.0",
//                inclusive = true,
                message = MessageConstant.PRODUCT_COST_PRICE_INVALID
        )
        BigDecimal costPrice,


        @Min(
                value = 0,
                message = MessageConstant.PRODUCT_STOCK_INVALID
        )
        Integer stockQuantity,

        @Min(
                value = 0,
                message = MessageConstant.PRODUCT_REORDER_LEVEL_INVALID
        )
        Integer reorderLevel,


        String sellerId,


        @Size(max = 30, message = MessageConstant.PRODUCT_UNIT_MAX_LENGTH)
        String unit,

        Boolean active,

        @Size(max = 200, message = MessageConstant.PRODUCT_BARCODE_MAX_LENGTH)
        String barcode,

        @Size(max = 500, message = MessageConstant.PRODUCT_IMAGE_URL_MAX_LENGTH)
        @Pattern(
                regexp = "^(https?://.*)?$",
                message = MessageConstant.PRODUCT_IMAGE_URL_INVALID
        )
        String imageUrl,

        @Size(max = 255, message = MessageConstant.PRODUCT_MANUFACTURER_MAX_LENGTH)
        String manufacturer,

        @Size(max = 100, message = MessageConstant.PRODUCT_COUNTRY_OF_ORIGIN_MAX_LENGTH)
        String countryOfOrigin,

        @Size(max = 100, message = MessageConstant.PRODUCT_TAX_CODE_MAX_LENGTH)
        String taxCode,

        Boolean taxable,

        @DecimalMin(
                value = "0.0",
                message = MessageConstant.PRODUCT_TAX_PERCENTAGE_NEGATIVE
        )
        @DecimalMax(
                value = "100.0",
                message = MessageConstant.PRODUCT_TAX_PERCENTAGE_EXCEED
        )
        Double taxPercentage,

        @Size(max = 1000, message = MessageConstant.PRODUCT_NOTES_MAX_LENGTH)
        String notes,

        @NotNull(message = MessageConstant.PRODUCT_RESERVED_QUANTITY_REQUIRED)
        @Min(
                value = 0,
                message = MessageConstant.PRODUCT_RESERVED_QUANTITY_INVALID
        )
        Integer reservedQuantity,

        @NotNull(message = MessageConstant.PRODUCT_AVAILABLE_QUANTITY_REQUIRED)
        @Min(
                value = 0,
                message = MessageConstant.PRODUCT_AVAILABLE_QUANTITY_INVALID
        )
        Integer availableQuantity
) {


}