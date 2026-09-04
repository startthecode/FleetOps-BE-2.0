package com.samtar.inventoryservice.entity;


import com.samtar.inventoryservice.constants.MessageConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "inventory-service", indexes = {
        @Index(name = "idx_inventory_product_id", columnList = "product_id"),
        @Index(name = "idx_warehouse_product_id", columnList = "warehouse_id"),
},
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_warehouse",
                        columnNames = {"product_id", "warehouse_id"}
                )
        }
)
public class InventoryEntity extends BaseEntity {
    @NotNull(message = MessageConstant.PRODUCT_ID_MANDATORY)
    @Column(nullable = false, name = "product_id")
    UUID productId;

    @NotNull(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
    @Column(nullable = false, name = "warehouse_id")
    UUID warehouseId;

    @Column(nullable = false, name = "quantity")
    @Min(value = 0, message = MessageConstant.STOCK_QUANTITY_INVALID)
    int quantity;

    @Column(nullable = false, name = "reserved_quantity")
    @Min(value = 0, message = MessageConstant.RESERVED_QUANTITY_INVALID)

    int reservedQuantity;

    @Column(nullable = false, name = "available_quantity")
    @Min(value = 0, message = MessageConstant.AVAILABLE_QUANTITY_INVALID)
    int availableQuantity;

}
