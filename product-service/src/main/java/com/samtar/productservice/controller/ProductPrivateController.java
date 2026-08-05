package com.samtar.productservice.controller;


import com.samtar.dto.SuccessApiResponse;
import com.samtar.productservice.annotation.LowerAuthorityAnnotation;
import com.samtar.productservice.constants.MessageConstant;
import com.samtar.productservice.dto.request.CreateProductReqDto;
import com.samtar.productservice.dto.request.UpdateProductReqDto;
import com.samtar.productservice.dto.response.ProductRespDto;
import com.samtar.productservice.service.ProductService;
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
@RequestMapping("/api/v1/product/private")
public class ProductPrivateController {
    private final ProductService productService;


    @PostMapping("/create")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<ProductRespDto>> createProduct(@Valid @RequestBody CreateProductReqDto payload) {
        ProductRespDto newProduct = productService.createProduct(payload);
        SuccessApiResponse<ProductRespDto> response = new SuccessApiResponse<>(MessageConstant.PRODUCT_CREATED_SUCCESS, newProduct, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<ProductRespDto>> updateProduct(@Valid @RequestBody UpdateProductReqDto payload, HttpServletRequest request) {
        ProductRespDto newProduct = productService.updateProduct(payload, request);
        SuccessApiResponse<ProductRespDto> response = new SuccessApiResponse<>(MessageConstant.PRODUCT_CREATED_SUCCESS, newProduct, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{productid}")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<Null>> updateProduct(@PathVariable String productid, HttpServletRequest request) {
        productService.deleteProduct(productid, request);
        SuccessApiResponse<Null> response = new SuccessApiResponse<>(MessageConstant.PRODUCT_DELETED_SUCCESS, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<ProductRespDto>>> getProductByUser(HttpServletRequest request) {
        SuccessApiResponse<List<ProductRespDto>> response = new SuccessApiResponse<>(MessageConstant.PRODUCT_CREATED_SUCCESS, productService.allProductsByUser(request), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/all")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<List<ProductRespDto>>> getAllProduct(HttpServletRequest request) {
        SuccessApiResponse<List<ProductRespDto>> response = new SuccessApiResponse<>(MessageConstant.PRODUCT_CREATED_SUCCESS, productService.allProductsByUser(request), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }


}
