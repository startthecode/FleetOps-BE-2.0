package com.samtar.locationservice.mapper;

import com.samtar.locationservice.dto.request.CreateCityReqDto;
import com.samtar.locationservice.dto.request.UpdateCityReqDto;
import com.samtar.locationservice.dto.response.CityRespDto;
import com.samtar.locationservice.entity.CityEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityMapper {

    // The state association is resolved and set by CityService, not mapped from the id.
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "status")
    @Mapping(ignore = true, target = "state")
    CityEntity toEntity(CreateCityReqDto payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "state")
    void toUpdatedEntity(@MappingTarget CityEntity city, UpdateCityReqDto payload);

    @Mapping(target = "stateId", source = "state.id")
    CityRespDto toResponse(CityEntity city);

    List<CityRespDto> toResponse(List<CityEntity> cities);
}
