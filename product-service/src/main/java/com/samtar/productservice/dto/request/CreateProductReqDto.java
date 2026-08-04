package com.samtar.productservice.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samtar.productservice.constants.MessageConstant;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateProductReqDto(

                           @NotBlank(message = MessageConstant.PRODUCT_NAME_MANDATORY)
                           @Size(max = 255, message = MessageConstant.PRODUCT_NAME_MAX_LENGTH)
                           String productName,

                           @NotBlank(message = MessageConstant.PRODUCT_SKU_MANDATORY)
                           @Size(max = 100, message = MessageConstant.PRODUCT_SKU_MAX_LENGTH)
                           String sku,

                           @NotBlank(message = MessageConstant.PRODUCT_DESCRIPTION_MANDATORY)
                           @Size(max = 1000, message = MessageConstant.PRODUCT_DESCRIPTION_MAX_LENGTH)
                           String description,

                           @NotBlank(message = MessageConstant.CATEGORY_ID_MANDATORY)
                           String categoryId,

                           @NotBlank(message = MessageConstant.BRAND_ID_MANDATORY)
                           String brandId,

                           @NotNull(message = MessageConstant.PRODUCT_SELLING_PRICE_INVALID)
                           @DecimalMin(
                                   value = "0.0",
                                   inclusive = false,
                                   message = MessageConstant.PRODUCT_SELLING_PRICE_INVALID
                           )
                           BigDecimal sellingPrice,

                           @NotNull(message = MessageConstant.PRODUCT_COST_PRICE_INVALID)
                           @DecimalMin(
                                   value = "0.0",
                                   inclusive = true,
                                   message = MessageConstant.PRODUCT_COST_PRICE_INVALID
                           )
                           BigDecimal costPrice,

                           @NotNull(message = MessageConstant.PRODUCT_STOCK_INVALID)
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

                           @NotBlank(message = MessageConstant.PRODUCT_SUPPLIER_INVALID_ID)
                           String sellerId,

                           @NotBlank(message = MessageConstant.PRODUCT_UNIT_MANDATORY)
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
                           String notes

) {

    public CreateProductReqDto {

        active = active == null ? Boolean.TRUE : active;

        taxable = taxable == null ? Boolean.TRUE : taxable;

        taxPercentage = taxPercentage == null ? 0.0 : taxPercentage;
    }
    }