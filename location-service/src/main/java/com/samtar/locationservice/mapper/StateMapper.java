package com.samtar.locationservice.mapper;

import com.samtar.locationservice.dto.request.CreateStateReqDto;
import com.samtar.locationservice.dto.request.UpdateStateReqDto;
import com.samtar.locationservice.dto.response.StateRespDto;
import com.samtar.locationservice.entity.StateEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StateMapper {

    // The country association is resolved and set by StateService, not mapped from the id.
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "status")
    @Mapping(ignore = true, target = "country")
    @Mapping(ignore = true, target = "cities")
    StateEntity toEntity(CreateStateReqDto payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "country")
    @Mapping(ignore = true, target = "cities")
    void toUpdatedEntity(@MappingTarget StateEntity state, UpdateStateReqDto payload);

    @Mapping(target = "countryId", source = "country.id")
    StateRespDto toResponse(StateEntity state);

    List<StateRespDto> toResponse(List<StateEntity> states);
}
