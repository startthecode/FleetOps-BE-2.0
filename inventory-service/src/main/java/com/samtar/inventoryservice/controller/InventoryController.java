package com.samtar.inventoryservice.controller;


import com.samtar.dto.SuccessApiResponse;
import com.samtar.inventoryservice.annotation.LowerAuthorityAnnotation;
import com.samtar.inventoryservice.annotation.MasterLevelAuthorityAnnotation;
import com.samtar.inventoryservice.constants.MessageConstant;
import com.samtar.inventoryservice.dto.request.UpdateReqDto;
import com.samtar.inventoryservice.dto.response.ResponseDto;
import com.samtar.inventoryservice.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/update")
    @LowerAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<ResponseDto>> update(@Valid @RequestBody UpdateReqDto updateReqDto, HttpServletRequest req) {
        SuccessApiResponse<ResponseDto> resp = new SuccessApiResponse<>(MessageConstant.INVENTORY_UPDATED_SUCCESS, inventoryService.update(updateReqDto, req), LocalDateTime.now());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/master/update")
    @MasterLevelAuthorityAnnotation
    public ResponseEntity<SuccessApiResponse<ResponseDto>> update(@Valid @RequestBody UpdateReqDto updateReqDto) {
        SuccessApiResponse<ResponseDto> resp = new SuccessApiResponse<>(MessageConstant.INVENTORY_UPDATED_SUCCESS, inventoryService.update(updateReqDto), LocalDateTime.now());
        return ResponseEntity.ok(resp);
    }
}
