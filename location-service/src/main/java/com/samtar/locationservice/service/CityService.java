package com.samtar.locationservice.service;

import com.samtar.exception.BaseException;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateCityReqDto;
import com.samtar.locationservice.dto.request.UpdateCityReqDto;
import com.samtar.locationservice.dto.response.CityRespDto;
import com.samtar.locationservice.entity.CityEntity;
import com.samtar.locationservice.entity.StateEntity;
import com.samtar.locationservice.mapper.CityMapper;
import com.samtar.locationservice.repository.CityRepository;
import com.samtar.locationservice.repository.StateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CityService {
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final CityMapper cityMapper;

    @Transactional
    public CityRespDto create(CreateCityReqDto payload) {
        StateEntity state = requireState(UUID.fromString(payload.stateId()));
        if (cityRepository.existsByState_IdAndNameIgnoreCase(state.getId(), payload.name().trim())) {
            throw new BaseException(MessageConstant.CITY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        CityEntity city = cityMapper.toEntity(payload);
        city.setState(state);
        city.setName(payload.name().trim());
        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public CityRespDto update(UpdateCityReqDto payload) {
        CityEntity city = findEntity(payload.cityId());
        UUID currentStateId = city.getState().getId();

        StateEntity targetState = null;
        if (payload.stateId() != null) {
            UUID requestedStateId = UUID.fromString(payload.stateId());
            if (!requestedStateId.equals(currentStateId)) {
                targetState = requireState(requestedStateId);
            }
        }

        // uk_city_name is (state_id, name), so a change to either side needs the re-check.
        UUID targetStateId = targetState != null ? targetState.getId() : currentStateId;
        String targetName = payload.name() != null ? payload.name().trim() : city.getName();
        boolean keyChanged = !targetStateId.equals(currentStateId)
                || !targetName.equalsIgnoreCase(city.getName());
        if (keyChanged && cityRepository.existsByState_IdAndNameIgnoreCase(targetStateId, targetName)) {
            throw new BaseException(MessageConstant.CITY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        cityMapper.toUpdatedEntity(city, payload);
        if (targetState != null) city.setState(targetState);
        if (payload.name() != null) city.setName(payload.name().trim());
        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public void delete(String cityId) {
        cityRepository.delete(findEntity(cityId));
    }

    @Transactional
    public CityRespDto findById(String cityId) {
        return cityMapper.toResponse(findEntity(cityId));
    }

    @Transactional
    public List<CityRespDto> findAll() {
        return cityMapper.toResponse(cityRepository.findAll());
    }

    @Transactional
    public List<CityRespDto> findByState(String stateId) {
        UUID id = UUID.fromString(stateId);
        requireState(id);
        return cityMapper.toResponse(cityRepository.findByState_Id(id));
    }

    private StateEntity requireState(UUID stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new BaseException(MessageConstant.STATE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private CityEntity findEntity(String cityId) {
        return cityRepository.findById(UUID.fromString(cityId))
                .orElseThrow(() -> new BaseException(MessageConstant.CITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
