package com.samtar.locationservice.service;

import com.samtar.exception.BaseException;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateCountryReqDto;
import com.samtar.locationservice.dto.request.UpdateCountryReqDto;
import com.samtar.locationservice.dto.response.CountryRespDto;
import com.samtar.locationservice.entity.CountryEntity;
import com.samtar.locationservice.mapper.CountryMapper;
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
public class CountryService {
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CountryMapper countryMapper;

    @Transactional
    public CountryRespDto create(CreateCountryReqDto payload) {
        if (countryRepository.existsByNameIgnoreCase(payload.name().trim())
                || countryRepository.existsByIso2IgnoreCase(payload.iso2().trim())
                || countryRepository.existsByIso3IgnoreCase(payload.iso3().trim())) {
            throw new BaseException(MessageConstant.COUNTRY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        CountryEntity country = countryMapper.toEntity(payload);
        country.setName(payload.name().trim());
        country.setIso2(payload.iso2().trim().toUpperCase());
        country.setIso3(payload.iso3().trim().toUpperCase());
        return countryMapper.toResponse(countryRepository.save(country));
    }

    @Transactional
    public CountryRespDto update(UpdateCountryReqDto payload) {
        CountryEntity country = findEntity(payload.countryId());

        // Each of these is unique on its own, so re-check only the ones actually changing.
        if (payload.name() != null && !payload.name().trim().equalsIgnoreCase(country.getName())
                && countryRepository.existsByNameIgnoreCase(payload.name().trim())) {
            throw new BaseException(MessageConstant.COUNTRY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (payload.iso2() != null && !payload.iso2().trim().equalsIgnoreCase(country.getIso2())
                && countryRepository.existsByIso2IgnoreCase(payload.iso2().trim())) {
            throw new BaseException(MessageConstant.COUNTRY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (payload.iso3() != null && !payload.iso3().trim().equalsIgnoreCase(country.getIso3())
                && countryRepository.existsByIso3IgnoreCase(payload.iso3().trim())) {
            throw new BaseException(MessageConstant.COUNTRY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        countryMapper.toUpdatedEntity(country, payload);
        if (payload.iso2() != null) country.setIso2(payload.iso2().trim().toUpperCase());
        if (payload.iso3() != null) country.setIso3(payload.iso3().trim().toUpperCase());
        return countryMapper.toResponse(countryRepository.save(country));
    }

    @Transactional
    public void delete(String countryId) {
        CountryEntity country = findEntity(countryId);
        if (stateRepository.existsByCountry_Id(country.getId())) {
            throw new BaseException(MessageConstant.COUNTRY_HAS_STATES, HttpStatus.CONFLICT);
        }
        countryRepository.delete(country);
    }

    @Transactional
    public CountryRespDto findById(String countryId) {
        return countryMapper.toResponse(findEntity(countryId));
    }

    @Transactional
    public List<CountryRespDto> findAll() {
        return countryMapper.toResponse(countryRepository.findAll());
    }

    private CountryEntity findEntity(String countryId) {
        return countryRepository.findById(UUID.fromString(countryId))
                .orElseThrow(() -> new BaseException(MessageConstant.COUNTRY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
