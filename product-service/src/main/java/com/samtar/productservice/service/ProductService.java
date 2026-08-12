package com.samtar.productservice.service;

import com.samtar.avro.ProductCreatedEvent;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;
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
        generateEventFlow(insertedProduct);
        return productMapper.toResponse(insertedProduct);
    }

    @Transactional
    public ProductRespDto updateProduct(UpdateProductReqDto payload, HttpServletRequest req) {
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        ProductEntity existingProduct = productRepository.findByIdAndSellerId(UUID.fromString(payload.productId()), UUID.fromString(userID)).orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        productMapper.toUpdatedEntity(existingProduct, payload);
        return productMapper.toResponse(productRepository.save(existingProduct));
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
        String userID = req.getHeader(ReqHeadersKeys.USER_ID);
        List<ProductEntity> existingProducts = productRepository.findAll();
        return productMapper.toResponse(existingProducts);
    }


    @Transactional
    private void generateEventFlow(ProductEntity product) {
        try {
            ProductCreatedEvent event = ProductCreatedEvent.newBuilder()
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
            OutboxEventEntity evntEntity = new OutboxEventEntity();
            evntEntity.setAggregateId(product.getId());
            evntEntity.setPayload(String.valueOf(event));
            evntEntity.setTopic(ProductEvents.CREATED.toString());
            evntEntity.setRetryCount(0);
            evntEntity.setStatus(OutboxStatus.PENDING);
            outBoxEventRepository.save(evntEntity);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.CONFLICT);
        }
    }
}
