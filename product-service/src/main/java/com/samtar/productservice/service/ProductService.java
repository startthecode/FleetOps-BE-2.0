package com.samtar.productservice.service;

import com.samtar.avro.ProductCreatedEvent;
import com.samtar.avro.ProductDeletedEvent;
import com.samtar.avro.ProductUpdatedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.consts.ReqHeadersKeys;
import com.samtar.enums.OutboxStatus;
import com.samtar.enums.kafkaEvents.ProductEvents;
import com.samtar.exception.BaseException;
import com.samtar.productservice.constants.MessageConstant;
import com.samtar.productservice.dto.request.CreateProductReqDto;
import com.samtar.productservice.dto.request.UpdateProductReqDto;
import com.samtar.productservice.dto.response.ProductRespDto;
import com.samtar.productservice.entity.OutboxEventEntity;
import com.samtar.productservice.entity.ProductEntity;
import com.samtar.productservice.mapper.ProductMapper;
import com.samtar.productservice.repository.OutBoxEventRepository;
import com.samtar.productservice.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OutBoxEventRepository outBoxEventRepository;

    @Transactional
    public ProductRespDto createProduct(CreateProductReqDto payload) {
        if (productRepository.existsByProductNameIgnoreCaseOrSkuIgnoreCase(payload.productName().trim(), payload.sku().trim())) {
            throw new BaseException(MessageConstant.PRODUCT_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        ProductEntity newProduct = productMapper.toEntity(payload);
        ProductEntity insertedProduct = productRepository.save(newProduct);
        generateCreationEvent(insertedProduct);
        return productMapper.toResponse(insertedProduct);
    }

    @Transactional
    public ProductRespDto updateProduct(UpdateProductReqDto payload, HttpServletRequest req) {
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        ProductEntity existingProduct = productRepository.findByIdAndSellerId(UUID.fromString(payload.productId()), UUID.fromString(userID)).orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        productMapper.toUpdatedEntity(existingProduct, payload);
        ProductEntity updatedProduct = productRepository.save(existingProduct);
        generateUpdateEvent(updatedProduct);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(String productId, HttpServletRequest req) {
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        ProductEntity existingProduct = productRepository
                .findByIdAndSellerId(UUID
                                .fromString(productId),
                        UUID.fromString(userID))
                .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        try {
            productRepository.delete(existingProduct);
            generateDeletionEvent(existingProduct);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public List<ProductRespDto> allProductsByUser(HttpServletRequest req) {
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        List<ProductEntity> existingProducts = productRepository.findBySellerId(UUID.fromString(userID));
        return productMapper.toResponse(existingProducts);
    }

    @Transactional
    public List<ProductRespDto> allProducts(HttpServletRequest req) {
        List<ProductEntity> existingProducts = productRepository.findAll();
        return productMapper.toResponse(existingProducts);
    }

    @Transactional
    private void outBoxInsertion(ProductEntity product, SpecificRecordBase payload, String eventTopic) {
        try {
            OutboxEventEntity evntEntity = new OutboxEventEntity();
            evntEntity.setAggregateId(product.getId());
            evntEntity.setPayload(encodeAvro(payload));
            evntEntity.setTopic(eventTopic);
            evntEntity.setRetryCount(0);
            evntEntity.setStatus(OutboxStatus.PENDING);
            evntEntity.setCreatedAt(Instant.now());
            outBoxEventRepository.save(evntEntity);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.CONFLICT);
        }
    }

    // Store the event as Base64-encoded Avro binary so the outbox TEXT column can hold it
    // and the publisher can rebuild the exact SpecificRecord to send through KafkaAvroSerializer.
    private static String encodeAvro(SpecificRecordBase record) throws IOException {
        SpecificDatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(record.getSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private void generateCreationEvent(ProductEntity product) {
        ProductCreatedEvent event = ProductCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setProductId(product.getId().toString())
                .setProductName(product.getProductName())
                .setDescription(product.getDescription())
                .setPrice(
                        ByteBuffer.wrap(
                                product.getSellingPrice()
                                        .movePointRight(2)
                                        .toBigIntegerExact()
                                        .toByteArray()
                        )
                )
                .setQuantity(product.getStockQuantity())
                .setCategoryId(
                        product.getCategoryId() != null
                                ? product.getCategoryId().toString()
                                : null
                )
                .build();
        outBoxInsertion(product, event, KafkaTopics.PRODUCT_CREATED);
    }

    private void generateUpdateEvent(ProductEntity product) {
        ProductUpdatedEvent event = ProductUpdatedEvent.newBuilder()
                .setProductId(product.getId().toString())
                .setEventId(UUID.randomUUID().toString())
                .setProductName(product.getProductName())
                .setDescription(product.getDescription())
                .setPrice(
                        ByteBuffer.wrap(
                                product.getSellingPrice()
                                        .movePointRight(2)
                                        .toBigIntegerExact()
                                        .toByteArray()
                        )
                )
                .setQuantity(product.getStockQuantity())
                .setCategoryId(
                        product.getCategoryId() != null
                                ? product.getCategoryId().toString()
                                : null
                )
                .build();
        outBoxInsertion(product, event, KafkaTopics.PRODUCT_UPDATED);
    }

    private void generateDeletionEvent(ProductEntity product) {
        ProductDeletedEvent event = ProductDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setProductId(product.getId().toString())
                .setProductName(product.getProductName())
                .setDescription(product.getDescription())
                .setPrice(
                        ByteBuffer.wrap(
                                product.getSellingPrice()
                                        .movePointRight(2)
                                        .toBigIntegerExact()
                                        .toByteArray()
                        )
                )
                .setQuantity(product.getStockQuantity())
                .setCategoryId(
                        product.getCategoryId() != null
                                ? product.getCategoryId().toString()
                                : null
                )
                .build();
        outBoxInsertion(product, event, KafkaTopics.PRODUCT_DELETED);
    }


}
