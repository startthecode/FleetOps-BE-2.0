package com.samtar.inventoryservice.service;

import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.exception.BaseException;
import com.samtar.inventoryservice.constants.MessageConstant;
import com.samtar.inventoryservice.dto.request.UpdateReqDto;
import com.samtar.inventoryservice.dto.response.ResponseDto;
import com.samtar.inventoryservice.entity.InventoryEntity;
import com.samtar.inventoryservice.mapper.InventoryMapper;
import com.samtar.inventoryservice.repository.InventoryRepository;
import com.samtar.inventoryservice.repository.ProcessedEvtRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
private  final InventoryRepository inventoryRepository;
private  final ProcessedEvtRepository processedEvtRepository;
private  final InventoryMapper inventoryMapper;

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
    public Boolean delete(String productId, String wareHouseId) {
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductIdAndWarehouseId(UUID.fromString(productId), UUID.fromString(wareHouseId))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.CONFLICT));
        inventoryRepository.delete(inventoryItem);
        return true;
    }
    @Transactional
    public Boolean delete(String productId) {
        InventoryEntity inventoryItem = inventoryRepository.
                findByProductId(UUID.fromString(productId))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.CONFLICT));
        inventoryRepository.delete(inventoryItem);
        return true;
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "inventory-service-group")
    private void createInventory(ProductCreatedEvent inventoryCreatedEvent, Acknowledgment acknowledgements) throws Exception {
       System.out.println(inventoryCreatedEvent);
        InventoryEntity inventory = new InventoryEntity();
        inventory.setProductId(UUID.fromString(inventoryCreatedEvent.getProductId()));
        inventory.setWarehouseId(UUID.fromString(inventoryCreatedEvent.getWarehouseId()));
        inventory.setQuantity(inventory.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity());
        inventory.setAvailableQuantity(inventory.getAvailableQuantity());
        ResponseDto newInventory = this.create(inventory);
        if (newInventory.productId() != null) {
            acknowledgements.acknowledge();
            return;
        }
        ;
        String exceptionEvent = inventoryCreatedEvent.getEventId() + "failed Creation of Inventory" + inventoryCreatedEvent.toString();
        throw new Exception(exceptionEvent);
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_DELETED, groupId = "inventory-service-group")
    private void deleteInventory(ProductDeletedEvent inventoryCreatedEvent, Acknowledgment acknowledgements) throws Exception {
        Boolean deleteInventory = this.delete(inventoryCreatedEvent.getProductId());
        if (Boolean.TRUE.equals(deleteInventory)) {
            acknowledgements.acknowledge();
            return;
        }
        String exceptionEvent = inventoryCreatedEvent.getEventId() + "failed Deletion of Inventory" + inventoryCreatedEvent.toString();
        throw new Exception(exceptionEvent);
    }

}
