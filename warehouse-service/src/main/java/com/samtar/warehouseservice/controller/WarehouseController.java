package com.samtar.warehouseservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.warehouseservice.annotation.LowerAuthorityAnnotation;
import com.samtar.warehouseservice.annotation.MasterLevelAuthorityAnnotation;
import com.samtar.warehouseservice.constants.MessageConstant;
import com.samtar.warehouseservice.dto.request.CreateWarehouseReqDto;
import com.samtar.warehouseservice.dto.request.UpdateWarehouseReqDto;
import com.samtar.warehouseservice.dto.response.WarehouseRespDto;
import com.samtar.warehouseservice.service.WarehouseService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping("/create")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<WarehouseRespDto>> createWarehouse(@Valid @RequestBody CreateWarehouseReqDto payload, HttpServletRequest req) {
        SuccessApiResponse<WarehouseRespDto> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_CREATED_SUCCESS, warehouseService.create(payload, req), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<WarehouseRespDto>> updateWarehouse(@Valid @RequestBody UpdateWarehouseReqDto payload, HttpServletRequest req) {
        SuccessApiResponse<WarehouseRespDto> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_UPDATED_SUCCESS, warehouseService.update(payload, req), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{warehouseId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> deleteWarehouse(@PathVariable String warehouseId, HttpServletRequest req) {
        warehouseService.delete(warehouseId, req);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/{warehouseId}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<WarehouseRespDto>> getWarehouse(@PathVariable String warehouseId, HttpServletRequest req) {
        SuccessApiResponse<WarehouseRespDto> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_FETCHED_SUCCESS, warehouseService.findById(warehouseId, req), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<WarehouseRespDto>>> getWarehousesByUser(HttpServletRequest req) {
        SuccessApiResponse<List<WarehouseRespDto>> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_FETCHED_SUCCESS, warehouseService.allWarehousesByUser(req), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/master/update")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<WarehouseRespDto>> updateWarehouse(@Valid @RequestBody UpdateWarehouseReqDto payload) {
        SuccessApiResponse<WarehouseRespDto> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_UPDATED_SUCCESS, warehouseService.update(payload), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/master/delete/{warehouseId}")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> deleteWarehouse(@PathVariable String warehouseId) {
        warehouseService.delete(warehouseId);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/master/{warehouseId}")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<WarehouseRespDto>> getWarehouse(@PathVariable String warehouseId) {
        SuccessApiResponse<WarehouseRespDto> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_FETCHED_SUCCESS, warehouseService.findById(warehouseId), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/all")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<WarehouseRespDto>>> getAllWarehouses() {
        SuccessApiResponse<List<WarehouseRespDto>> response = new SuccessApiResponse<>(MessageConstant.WAREHOUSE_FETCHED_SUCCESS, warehouseService.allWarehouses(), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
