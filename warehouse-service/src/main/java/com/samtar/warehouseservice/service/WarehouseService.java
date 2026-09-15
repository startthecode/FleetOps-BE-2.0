package com.samtar.warehouseservice.service;

import com.samtar.consts.ReqHeadersKeys;
import com.samtar.exception.BaseException;
import com.samtar.warehouseservice.constants.MessageConstant;
import com.samtar.warehouseservice.dto.request.CreateWarehouseReqDto;
import com.samtar.warehouseservice.dto.request.UpdateWarehouseReqDto;
import com.samtar.warehouseservice.dto.response.WarehouseRespDto;
import com.samtar.warehouseservice.entity.WarehouseEntity;
import com.samtar.warehouseservice.mapper.WarehouseMapper;
import com.samtar.warehouseservice.repository.WarehouseRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    public WarehouseRespDto create(CreateWarehouseReqDto payload, HttpServletRequest req) {
        if (warehouseRepository.existsByCode(payload.code())) {
            throw new BaseException(MessageConstant.WAREHOUSE_CODE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        WarehouseEntity newWarehouse = warehouseMapper.toEntity(payload);
        newWarehouse.setCity(UUID.fromString(payload.city()));
        return warehouseMapper.toResponse(warehouseRepository.save(newWarehouse));
    }


    @Transactional
    public WarehouseRespDto update(UpdateWarehouseReqDto payload) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(payload.warehouseId()))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        warehouseMapper.toUpdatedEntity(existingWarehouse, payload);
        return warehouseMapper.toResponse(warehouseRepository.save(existingWarehouse));
    }


    @Transactional
    public void delete(String warehouseId) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(warehouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        warehouseRepository.delete(existingWarehouse);
    }


    @Transactional
    public WarehouseRespDto findById(String warehouseId) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(warehouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return warehouseMapper.toResponse(existingWarehouse);
    }


    @Transactional
    public List<WarehouseRespDto> allWarehouses() {
        return warehouseMapper.toResponse(warehouseRepository.findAll());
    }


}
