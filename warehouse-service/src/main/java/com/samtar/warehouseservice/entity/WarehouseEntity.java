package com.samtar.warehouseservice.entity;

import com.samtar.warehouseservice.constants.MessageConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouses",
        indexes = {
                @Index(name = "idx_warehouse_seller_id", columnList = "seller_id"),
                @Index(name = "idx_warehouse_city", columnList = "city")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_warehouse_code",
                        columnNames = {"seller_id", "code"}
                )
        }
)
public class WarehouseEntity extends BaseEntity {

    // UUID of User Service
    @Column(name = "seller_id", nullable = false)
    @NotNull(message = MessageConstant.SELLER_ID_INVALID)
    private UUID sellerId;

    @Column(nullable = false, length = 150)
    @NotBlank(message = MessageConstant.WAREHOUSE_NAME_MANDATORY)
    private String name;

    @Column(nullable = false, length = 50)
    @NotBlank(message = MessageConstant.WAREHOUSE_CODE_MANDATORY)
    private String code;

    @Column(name = "address_line1", nullable = false, length = 255)
    @NotBlank(message = MessageConstant.ADDRESS_LINE1_MANDATORY)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.CITY_MANDATORY)
    private UUID city;

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.STATE_MANDATORY)
    private UUID state;

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.COUNTRY_MANDATORY)
    private UUID country;

    @Column(name = "postal_code", nullable = false, length = 20)
    @NotBlank(message = MessageConstant.POSTAL_CODE_MANDATORY)
    private String postalCode;

    @Column(precision = 10, scale = 7)
    @DecimalMin(value = "-90.0", message = MessageConstant.LATITUDE_INVALID)
    @DecimalMax(value = "90.0", message = MessageConstant.LATITUDE_INVALID)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    @DecimalMin(value = "-180.0", message = MessageConstant.LONGITUDE_INVALID)
    @DecimalMax(value = "180.0", message = MessageConstant.LONGITUDE_INVALID)
    private BigDecimal longitude;

}
