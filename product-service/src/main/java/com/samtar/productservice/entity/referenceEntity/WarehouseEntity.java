package com.samtar.productservice.entity.referenceEntity;


import com.samtar.productservice.constants.MessageConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "warehouse_reference")
public class WarehouseEntity {
    @Id
    @NotNull(message = MessageConstant.WAREHOUSE_ID_MANDATORY)
    @Column(unique = true,nullable = false,name = "warehouse_id")
    UUID warehouseId;

    @CreationTimestamp
    LocalDateTime createdAt;
}
