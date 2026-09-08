package com.samtar.inventoryservice.service;

import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.consts.ReqHeadersKeys;
import com.samtar.exception.BaseException;
import com.samtar.inventoryservice.constants.MessageConstant;
import com.samtar.inventoryservice.dto.request.UpdateReqDto;
import com.samtar.inventoryservice.dto.response.ResponseDto;
import com.samtar.inventoryservice.entity.InventoryEntity;
import com.samtar.inventoryservice.entity.ProcessedEventsEntity;
import com.samtar.inventoryservice.mapper.InventoryMapper;
import com.samtar.inventoryservice.repository.InventoryRepository;
import com.samtar.inventoryservice.repository.ProcessedEvtRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProcessedEvtRepository processedEvtRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public ResponseDto update(UpdateReqDto updateReqDto, HttpServletRequest req) {
        String sellerId = req.getHeader(ReqHeadersKeys.USER_ID);
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductIdAndWarehouseIdAndSellerId(UUID.fromString(updateReqDto.productId()), UUID.fromString(updateReqDto.warehouseId()), UUID.fromString(sellerId))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.CONFLICT));
        inventoryMapper.updateEntity(updateReqDto, inventoryItem);
        inventoryRepository.save(inventoryItem);
        return inventoryMapper.toResponse(inventoryItem);
    }

    @Transactional
    public ResponseDto update(UpdateReqDto updateReqDto) {
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductIdAndWarehouseId(UUID.fromString(updateReqDto.productId()), UUID.fromString(updateReqDto.warehouseId()))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.CONFLICT));
        inventoryMapper.updateEntity(updateReqDto, inventoryItem);
        inventoryRepository.save(inventoryItem);
        return inventoryMapper.toResponse(inventoryItem);
    }

    @Transactional
    public ResponseDto create(InventoryEntity inventoryEntity) {
        return inventoryMapper.toResponse(inventoryRepository.save(inventoryEntity));
    }


    @Transactional
    public InventoryEntity create(ProductCreatedEvent productCreatedEvent) {
        try {
            InventoryEntity inventory = new InventoryEntity();
            inventory.setProductId(UUID.fromString(productCreatedEvent.getProductId()));
            inventory.setWarehouseId(UUID.fromString(productCreatedEvent.getWarehouseId()));
            inventory.setQuantity(productCreatedEvent.getQuantity());
            inventory.setSellerId(UUID.fromString(productCreatedEvent.getSellerId()));
            inventory.setReservedQuantity(productCreatedEvent.getReservedQuantity());
            inventory.setAvailableQuantity(productCreatedEvent.getAvailableQuantity());
            ProcessedEventsEntity processedEventsEntity = new ProcessedEventsEntity();
            processedEventsEntity.setEventId(UUID.fromString(productCreatedEvent.getEventId()));
            processedEventsEntity.setEventType(KafkaTopics.PRODUCT_CREATED);
            processedEventsEntity.setProcessedAt(Instant.now());
            InventoryEntity resp = inventoryRepository.save(inventory);
            processedEvtRepository.save(processedEventsEntity);
            return resp;
        } catch (Exception e) {
            System.out.println("------------------------");
            System.out.println(e);
            System.out.println("------------------------");
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public Boolean delete(String productId, String wareHouseId) {
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductIdAndWarehouseId(UUID.fromString(productId), UUID.fromString(wareHouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.CONFLICT));
        inventoryRepository.delete(inventoryItem);
        return true;
    }

    @Transactional
    public Boolean delete(ProductDeletedEvent productDeletedEvent) {
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductId(UUID.fromString(productDeletedEvent.getProductId()))
                .orElse(null);
        if (inventoryItem == null) return false;
        inventoryRepository.delete(inventoryItem);
        ProcessedEventsEntity processedEventsEntity = new ProcessedEventsEntity();
        processedEventsEntity.setEventId(UUID.fromString(productDeletedEvent.getEventId()));
        processedEventsEntity.setEventType(KafkaTopics.PRODUCT_DELETED);
        processedEventsEntity.setProcessedAt(Instant.now());
        processedEvtRepository.save(processedEventsEntity);
        return true;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "inventory-service-group")
    public void createInventory(ProductCreatedEvent inventoryCreatedEvent, Acknowledgment acknowledgements) throws Exception {
        try {
            if (processedEvtRepository.existsByEventId(UUID.fromString(inventoryCreatedEvent.getEventId()))) {
                acknowledgements.acknowledge();
                return;
            }
            this.create(inventoryCreatedEvent);
            acknowledgements.acknowledge();
        } catch (Exception e) {
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.PRODUCT_DELETED, groupId = "inventory-service-group")
    public void deleteInventory(ProductDeletedEvent inventoryDeleteEvent, Acknowledgment acknowledgements) throws Exception {
        try {
            if (processedEvtRepository.existsByEventId(UUID.fromString(inventoryDeleteEvent.getEventId()))) {
                acknowledgements.acknowledge();
                return;
            }
            this.delete(inventoryDeleteEvent);
            acknowledgements.acknowledge();
        } catch (Exception e) {
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
