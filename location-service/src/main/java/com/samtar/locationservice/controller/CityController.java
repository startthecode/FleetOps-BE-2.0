package com.samtar.locationservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.locationservice.annotation.LowerAuthorityAnnotation;
import com.samtar.locationservice.annotation.MasterLevelAuthorityAnnotation;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateCityReqDto;
import com.samtar.locationservice.dto.request.UpdateCityReqDto;
import com.samtar.locationservice.dto.response.CityRespDto;
import com.samtar.locationservice.service.CityService;
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
@RequestMapping("/api/v1/location/city")
public class CityController {
    private final CityService cityService;

    @PostMapping("/create")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CityRespDto>> createCity(@Valid @RequestBody CreateCityReqDto payload) {
        SuccessApiResponse<CityRespDto> response = new SuccessApiResponse<>(MessageConstant.CITY_CREATED_SUCCESS, cityService.create(payload), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CityRespDto>> updateCity(@Valid @RequestBody UpdateCityReqDto payload) {
        SuccessApiResponse<CityRespDto> response = new SuccessApiResponse<>(MessageConstant.CITY_UPDATED_SUCCESS, cityService.update(payload), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{cityId}")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> deleteCity(@PathVariable String cityId) {
        cityService.delete(cityId);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.CITY_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<CityRespDto>>> getAllCities() {
        SuccessApiResponse<List<CityRespDto>> response = new SuccessApiResponse<>(MessageConstant.CITY_FETCHED_SUCCESS, cityService.findAll(), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-state/{stateId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<CityRespDto>>> getCitiesByState(@PathVariable String stateId) {
        SuccessApiResponse<List<CityRespDto>> response = new SuccessApiResponse<>(MessageConstant.CITY_FETCHED_SUCCESS, cityService.findByState(stateId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cityId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<CityRespDto>> getCity(@PathVariable String cityId) {
        SuccessApiResponse<CityRespDto> response = new SuccessApiResponse<>(MessageConstant.CITY_FETCHED_SUCCESS, cityService.findById(cityId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
