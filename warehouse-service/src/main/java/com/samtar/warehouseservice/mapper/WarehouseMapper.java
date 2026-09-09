package com.samtar.warehouseservice.mapper;

import com.samtar.warehouseservice.dto.request.CreateWarehouseReqDto;
import com.samtar.warehouseservice.dto.request.UpdateWarehouseReqDto;
import com.samtar.warehouseservice.dto.response.WarehouseRespDto;
import com.samtar.warehouseservice.entity.WarehouseEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "sellerId")
    WarehouseEntity toEntity(CreateWarehouseReqDto warehouseReqDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "sellerId")
    void toUpdatedEntity(@MappingTarget WarehouseEntity warehouse, UpdateWarehouseReqDto payload);

    WarehouseRespDto toResponse(WarehouseEntity warehouseEntity);

    List<WarehouseRespDto> toResponse(List<WarehouseEntity> warehouseEntities);
}
