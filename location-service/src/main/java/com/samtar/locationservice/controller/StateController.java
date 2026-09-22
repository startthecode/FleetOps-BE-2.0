package com.samtar.locationservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.locationservice.annotation.LowerAuthorityAnnotation;
import com.samtar.locationservice.annotation.MasterLevelAuthorityAnnotation;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateStateReqDto;
import com.samtar.locationservice.dto.request.UpdateStateReqDto;
import com.samtar.locationservice.dto.response.StateRespDto;
import com.samtar.locationservice.service.StateService;
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
@RequestMapping("/api/v1/location/state")
public class StateController {
    private final StateService stateService;

    @PostMapping("/create")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<StateRespDto>> createState(@Valid @RequestBody CreateStateReqDto payload) {
        SuccessApiResponse<StateRespDto> response = new SuccessApiResponse<>(MessageConstant.STATE_CREATED_SUCCESS, stateService.create(payload), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<StateRespDto>> updateState(@Valid @RequestBody UpdateStateReqDto payload) {
        SuccessApiResponse<StateRespDto> response = new SuccessApiResponse<>(MessageConstant.STATE_UPDATED_SUCCESS, stateService.update(payload), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{stateId}")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> deleteState(@PathVariable String stateId) {
        stateService.delete(stateId);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.STATE_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<StateRespDto>>> getAllStates() {
        SuccessApiResponse<List<StateRespDto>> response = new SuccessApiResponse<>(MessageConstant.STATE_FETCHED_SUCCESS, stateService.findAll(), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-country/{countryId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<StateRespDto>>> getStatesByCountry(@PathVariable String countryId) {
        SuccessApiResponse<List<StateRespDto>> response = new SuccessApiResponse<>(MessageConstant.STATE_FETCHED_SUCCESS, stateService.findByCountry(countryId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{stateId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<StateRespDto>> getState(@PathVariable String stateId) {
        SuccessApiResponse<StateRespDto> response = new SuccessApiResponse<>(MessageConstant.STATE_FETCHED_SUCCESS, stateService.findById(stateId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
