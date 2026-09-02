package com.samtar.inventoryservice.mapper;


import com.samtar.inventoryservice.dto.request.UpdateReqDto;
import com.samtar.inventoryservice.dto.response.ResponseDto;
import com.samtar.inventoryservice.entity.InventoryEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(ignore = true, target = "productId")
    @Mapping(ignore = true, target = "warehouseId")
    void updateEntity(UpdateReqDto updateReqDto, @MappingTarget InventoryEntity inventoryEntity);

    @Mapping(ignore = true, target = "productId")
    ResponseDto toResponse(InventoryEntity entity);
}
