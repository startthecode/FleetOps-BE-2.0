package com.samtar.productservice.service.imp;

import com.samtar.exception.BaseException;
import com.samtar.exception.ValidationException;
import com.samtar.productservice.constants.MessageConstant;
import com.samtar.productservice.dto.request.CreateProductReqDto;
import com.samtar.productservice.dto.response.ProductRespDto;
import com.samtar.productservice.mapper.ProductMapper;
import com.samtar.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
private ProductRepository productRepository;
private ProductMapper productMapper;


@Transactional
   public void createProduct(CreateProductReqDto payload){
    if(productRepository.existsBySkuNameIgnoreCase(payload.sku()) || productRepository.existsBySkuNameIgnoreCase(payload.productName().trim())){
        throw new BaseException(MessageConstant.PRODUCT_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}


}
