package com.samtar.productservice.service;

import com.samtar.consts.ReqHeadersKeys;
import com.samtar.exception.BaseException;
import com.samtar.productservice.constants.MessageConstant;
import com.samtar.productservice.dto.request.CreateProductReqDto;
import com.samtar.productservice.dto.request.UpdateProductReqDto;
import com.samtar.productservice.dto.response.ProductRespDto;
import com.samtar.productservice.entity.ProductEntity;
import com.samtar.productservice.mapper.ProductMapper;
import com.samtar.productservice.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    public ProductRespDto createProduct(CreateProductReqDto payload) {
        if (productRepository.existsByProductNameIgnoreCaseOrSkuIgnoreCase(payload.productName().trim(), payload.sku().trim())) {
            throw new BaseException(MessageConstant.PRODUCT_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        ProductEntity newProduct = productMapper.toEntity(payload);
        return productMapper.toResponse(productRepository.save(newProduct));
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





}
