package com.samtar.productservice.entity;


import com.samtar.productservice.constants.MessageConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "product_items")
public class ProductEntity extends BaseEntity{
    @Column(unique = true, nullable = false, length = 255)
    @NotBlank(message = MessageConstant.PRODUCT_NAME_MANDATORY)
    private String productName;

    @Column(unique = true, nullable = false, length = 120)
    @NotBlank(message = MessageConstant.PRODUCT_SKU_MANDATORY)
    private String sku;

    @Column(nullable = false, length = 500)
    @NotBlank(message = MessageConstant.PRODUCT_DESCRIPTION_MANDATORY)
    private String description;

    // UUID of Category Service
    @Column(nullable = false)
    @NotNull(message = MessageConstant.CATEGORY_ID_MANDATORY)
    private UUID categoryId;

    // UUID of Brand Service
    @Column(nullable = false)
    @NotNull(message = MessageConstant.BRAND_ID_MANDATORY)
    private UUID brandId;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = MessageConstant.PRODUCT_SELLING_PRICE_INVALID)
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = MessageConstant.PRODUCT_COST_PRICE_INVALID)
    private BigDecimal costPrice;

    @Column(nullable = false)
    @Min(value = 0, message = MessageConstant.PRODUCT_STOCK_INVALID)
    private Integer stockQuantity;

    @Column(nullable = false)
    @Min(value = 0, message = MessageConstant.PRODUCT_REORDER_LEVEL_INVALID)
    private Integer reorderLevel;

    @Column(nullable = false)
    @NotNull(message = MessageConstant.PRODUCT_SUPPLIER_INVALID_ID)
    private UUID sellerId;

    @Column(nullable = false, length = 30)
    @NotBlank(message = MessageConstant.PRODUCT_UNIT_MANDATORY)
    private String unit; // Piece, Kg, Liter, Box, etc.

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 255)
    private String barcode;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 255)
    private String manufacturer;

    @Column(length = 100)
    private String countryOfOrigin;

    @Column(length = 100)
    private String taxCode;

    @Column(nullable = false)
    private Boolean taxable = true;

    @Column(nullable = false)
    private Double taxPercentage = 0.0;

    @Column(length = 1000)
    private String notes;

}
