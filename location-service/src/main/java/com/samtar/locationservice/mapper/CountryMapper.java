package com.samtar.locationservice.mapper;

import com.samtar.locationservice.dto.request.CreateCountryReqDto;
import com.samtar.locationservice.dto.request.UpdateCountryReqDto;
import com.samtar.locationservice.dto.response.CountryRespDto;
import com.samtar.locationservice.entity.CountryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "status")
    @Mapping(ignore = true, target = "states")
    CountryEntity toEntity(CreateCountryReqDto payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "states")
    void toUpdatedEntity(@MappingTarget CountryEntity country, UpdateCountryReqDto payload);

    CountryRespDto toResponse(CountryEntity country);

    List<CountryRespDto> toResponse(List<CountryEntity> countries);
}
