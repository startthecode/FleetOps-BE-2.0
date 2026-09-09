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
        UUID sellerId = sellerId(req);
        if (warehouseRepository.existsBySellerIdAndCodeIgnoreCase(sellerId, payload.code().trim())) {
            throw new BaseException(MessageConstant.WAREHOUSE_CODE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        WarehouseEntity newWarehouse = warehouseMapper.toEntity(payload);
        newWarehouse.setSellerId(sellerId);
        newWarehouse.setCode(payload.code().trim());
        return warehouseMapper.toResponse(warehouseRepository.save(newWarehouse));
    }

    @Transactional
    public WarehouseRespDto update(UpdateWarehouseReqDto payload, HttpServletRequest req) {
        UUID sellerId = sellerId(req);
        WarehouseEntity existingWarehouse = warehouseRepository
                .findByIdAndSellerId(UUID.fromString(payload.warehouseId()), sellerId)
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return applyUpdate(existingWarehouse, payload);
    }

    @Transactional
    public WarehouseRespDto update(UpdateWarehouseReqDto payload) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(payload.warehouseId()))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return applyUpdate(existingWarehouse, payload);
    }

    @Transactional
    public void delete(String warehouseId, HttpServletRequest req) {
        UUID sellerId = sellerId(req);
        WarehouseEntity existingWarehouse = warehouseRepository
                .findByIdAndSellerId(UUID.fromString(warehouseId), sellerId)
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        warehouseRepository.delete(existingWarehouse);
    }

    @Transactional
    public void delete(String warehouseId) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(warehouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        warehouseRepository.delete(existingWarehouse);
    }

    @Transactional
    public WarehouseRespDto findById(String warehouseId, HttpServletRequest req) {
        UUID sellerId = sellerId(req);
        WarehouseEntity existingWarehouse = warehouseRepository
                .findByIdAndSellerId(UUID.fromString(warehouseId), sellerId)
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return warehouseMapper.toResponse(existingWarehouse);
    }

    @Transactional
    public WarehouseRespDto findById(String warehouseId) {
        WarehouseEntity existingWarehouse = warehouseRepository
                .findById(UUID.fromString(warehouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.WAREHOUSE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return warehouseMapper.toResponse(existingWarehouse);
    }

    @Transactional
    public List<WarehouseRespDto> allWarehousesByUser(HttpServletRequest req) {
        return warehouseMapper.toResponse(warehouseRepository.findBySellerId(sellerId(req)));
    }

    @Transactional
    public List<WarehouseRespDto> allWarehouses() {
        return warehouseMapper.toResponse(warehouseRepository.findAll());
    }

    // Code is part of uk_warehouse_code, so a code change needs the uniqueness re-check.
    private WarehouseRespDto applyUpdate(WarehouseEntity warehouse, UpdateWarehouseReqDto payload) {
        if (payload.code() != null
                && !payload.code().trim().equalsIgnoreCase(warehouse.getCode())
                && warehouseRepository.existsBySellerIdAndCodeIgnoreCase(warehouse.getSellerId(), payload.code().trim())) {
            throw new BaseException(MessageConstant.WAREHOUSE_CODE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        warehouseMapper.toUpdatedEntity(warehouse, payload);
        if (payload.code() != null) warehouse.setCode(payload.code().trim());
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    private UUID sellerId(HttpServletRequest req) {
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        if (userID == null) {
            throw new BaseException(MessageConstant.SELLER_ID_INVALID, HttpStatus.UNAUTHORIZED);
        }
        return UUID.fromString(userID);
    }
}
