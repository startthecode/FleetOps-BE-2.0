package com.samtar.productservice.mapper;

import com.samtar.productservice.dto.request.CreateProductReqDto;
import com.samtar.productservice.dto.request.UpdateProductReqDto;
import com.samtar.productservice.dto.response.ProductRespDto;
import com.samtar.productservice.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(ignore = true,target = "id")
    @Mapping(ignore = true,target = "createdAt")
    @Mapping(ignore = true,target = "updatedAt")
    @Mapping(ignore = true,target = "version")
    ProductEntity toEntity(CreateProductReqDto productReqDto);

    @Mapping(ignore = true,target = "version")
    ProductRespDto toResponse(ProductEntity ProductEntity);
    List<ProductRespDto> toResponse(List<ProductEntity> ProductEntity);
}
