package com.samtar.inventoryservice.entity.referenceEntity;


import com.samtar.inventoryservice.constants.MessageConstant;
import com.samtar.inventoryservice.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "warehouse_reference")
public class WarehouseEntity extends BaseEntity {
    @Column(name = "warehouse_id",unique = true,nullable = false)
    @NotNull(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
    UUID warehouseID;
}
