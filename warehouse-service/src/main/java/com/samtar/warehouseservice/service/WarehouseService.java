package com.samtar.warehouseservice.service;

import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.WarehouseCreatedEvent;
import com.samtar.avro.WarehouseDeletedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.consts.ReqHeadersKeys;
import com.samtar.enums.OutboxStatus;
import com.samtar.exception.BaseException;
import com.samtar.warehouseservice.constants.MessageConstant;
import com.samtar.warehouseservice.dto.request.CreateWarehouseReqDto;
import com.samtar.warehouseservice.dto.request.UpdateWarehouseReqDto;
import com.samtar.warehouseservice.dto.response.WarehouseRespDto;
import com.samtar.warehouseservice.entity.OutboxEventEntity;
import com.samtar.warehouseservice.entity.WarehouseEntity;
import com.samtar.warehouseservice.mapper.WarehouseMapper;
import com.samtar.warehouseservice.repository.OutboxEventRepository;
import com.samtar.warehouseservice.repository.WarehouseRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public WarehouseRespDto create(CreateWarehouseReqDto payload, HttpServletRequest req) {
        if (warehouseRepository.existsByCode(payload.code())) {
            throw new BaseException(MessageConstant.WAREHOUSE_CODE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        WarehouseEntity newWarehouse = warehouseMapper.toEntity(payload);
        newWarehouse.setCity(UUID.fromString(payload.city()));
        WarehouseEntity response = warehouseRepository.save(newWarehouse);
        generateCreationEvent(response, payload);
        return warehouseMapper.toResponse(response);
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
        generateDeleteEvent(existingWarehouse);
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

    private static String encodeAvro(SpecificRecordBase record) throws IOException {
        SpecificDatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(record.getSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private void generateCreationEvent(WarehouseEntity warehouse, CreateWarehouseReqDto payload) {
        WarehouseCreatedEvent event1 = WarehouseCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setWarehouseId(warehouse.getId().toString())
                .build();
        outBoxInsertion(warehouse, event1, KafkaTopics.WAREHOUSE_CREATED);
    }

    private void generateDeleteEvent(WarehouseEntity warehouse) {
        WarehouseDeletedEvent event1 = WarehouseDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setWarehouseId(warehouse.getId().toString())
                .build();
        outBoxInsertion(warehouse, event1, KafkaTopics.WAREHOUSE_DELETED);
    }

    @Transactional
    private void outBoxInsertion(WarehouseEntity warehouse, SpecificRecordBase payload, String eventTopic) {
        try {
            OutboxEventEntity evntEntity = new OutboxEventEntity();
            evntEntity.setAggregateId(warehouse.getId());
            evntEntity.setPayload(encodeAvro(payload));
            evntEntity.setTopic(eventTopic);
            evntEntity.setRetryCount(0);
            evntEntity.setStatus(OutboxStatus.PENDING);
            evntEntity.setCreatedAt(Instant.now());
            outboxEventRepository.save(evntEntity);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.CONFLICT);
        }
    }


}
