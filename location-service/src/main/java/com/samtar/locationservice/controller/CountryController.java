package com.samtar.locationservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.locationservice.annotation.LowerAuthorityAnnotation;
import com.samtar.locationservice.annotation.MasterLevelAuthorityAnnotation;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateCountryReqDto;
import com.samtar.locationservice.dto.request.UpdateCountryReqDto;
import com.samtar.locationservice.dto.response.CountryRespDto;
import com.samtar.locationservice.service.CountryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/location/country")
public class CountryController {
    private final CountryService countryService;

    @PostMapping("/create")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CountryRespDto>> createCountry(@Valid @RequestBody CreateCountryReqDto payload) {
        SuccessApiResponse<CountryRespDto> response = new SuccessApiResponse<>(MessageConstant.COUNTRY_CREATED_SUCCESS, countryService.create(payload), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CountryRespDto>> updateCountry(@Valid @RequestBody UpdateCountryReqDto payload) {
        SuccessApiResponse<CountryRespDto> response = new SuccessApiResponse<>(MessageConstant.COUNTRY_UPDATED_SUCCESS, countryService.update(payload), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{countryId}")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> deleteCountry(@PathVariable String countryId) {
        countryService.delete(countryId);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.COUNTRY_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<CountryRespDto>>> getAllCountries() {
        SuccessApiResponse<List<CountryRespDto>> response = new SuccessApiResponse<>(MessageConstant.COUNTRY_FETCHED_SUCCESS, countryService.findAll(), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{countryId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CountryRespDto>> getCountry(@PathVariable String countryId) {
        SuccessApiResponse<CountryRespDto> response = new SuccessApiResponse<>(MessageConstant.COUNTRY_FETCHED_SUCCESS, countryService.findById(countryId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
