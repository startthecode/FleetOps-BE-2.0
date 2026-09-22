package com.samtar.locationservice.service;

import com.samtar.exception.BaseException;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateStateReqDto;
import com.samtar.locationservice.dto.request.UpdateStateReqDto;
import com.samtar.locationservice.dto.response.StateRespDto;
import com.samtar.locationservice.entity.CountryEntity;
import com.samtar.locationservice.entity.StateEntity;
import com.samtar.locationservice.mapper.StateMapper;
import com.samtar.locationservice.repository.CityRepository;
import com.samtar.locationservice.repository.CountryRepository;
import com.samtar.locationservice.repository.StateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StateService {
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final StateMapper stateMapper;

    @Transactional
    public StateRespDto create(CreateStateReqDto payload) {
        CountryEntity country = requireCountry(UUID.fromString(payload.countryId()));
        if (stateRepository.existsByCountry_IdAndCodeIgnoreCase(country.getId(), payload.code().trim())) {
            throw new BaseException(MessageConstant.STATE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        StateEntity state = stateMapper.toEntity(payload);
        state.setCountry(country);
        state.setName(payload.name().trim());
        state.setCode(payload.code().trim().toUpperCase());
        return stateMapper.toResponse(stateRepository.save(state));
    }

    @Transactional
    public StateRespDto update(UpdateStateReqDto payload) {
        StateEntity state = findEntity(payload.stateId());
        UUID currentCountryId = state.getCountry().getId();

        CountryEntity targetCountry = null;
        if (payload.countryId() != null) {
            UUID requestedCountryId = UUID.fromString(payload.countryId());
            if (!requestedCountryId.equals(currentCountryId)) {
                targetCountry = requireCountry(requestedCountryId);
            }
        }

        // uk_state_code is (country_id, code), so a change to either side needs the re-check.
        UUID targetCountryId = targetCountry != null ? targetCountry.getId() : currentCountryId;
        String targetCode = payload.code() != null ? payload.code().trim() : state.getCode();
        boolean keyChanged = !targetCountryId.equals(currentCountryId)
                || !targetCode.equalsIgnoreCase(state.getCode());
        if (keyChanged && stateRepository.existsByCountry_IdAndCodeIgnoreCase(targetCountryId, targetCode)) {
            throw new BaseException(MessageConstant.STATE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        stateMapper.toUpdatedEntity(state, payload);
        if (targetCountry != null) state.setCountry(targetCountry);
        if (payload.code() != null) state.setCode(payload.code().trim().toUpperCase());
        return stateMapper.toResponse(stateRepository.save(state));
    }

    @Transactional
    public void delete(String stateId) {
        StateEntity state = findEntity(stateId);
        if (cityRepository.existsByState_Id(state.getId())) {
            throw new BaseException(MessageConstant.STATE_HAS_CITIES, HttpStatus.CONFLICT);
        }
        stateRepository.delete(state);
    }

    @Transactional
    public StateRespDto findById(String stateId) {
        return stateMapper.toResponse(findEntity(stateId));
    }

    @Transactional
    public List<StateRespDto> findAll() {
        return stateMapper.toResponse(stateRepository.findAll());
    }

    @Transactional
    public List<StateRespDto> findByCountry(String countryId) {
        UUID id = UUID.fromString(countryId);
        requireCountry(id);
        return stateMapper.toResponse(stateRepository.findByCountry_Id(id));
    }

    private CountryEntity requireCountry(UUID countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new BaseException(MessageConstant.COUNTRY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private StateEntity findEntity(String stateId) {
        return stateRepository.findById(UUID.fromString(stateId))
                .orElseThrow(() -> new BaseException(MessageConstant.STATE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
