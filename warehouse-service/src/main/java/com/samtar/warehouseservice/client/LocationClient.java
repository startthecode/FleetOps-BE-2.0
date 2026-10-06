package com.samtar.warehouseservice.client;


import com.samtar.warehouseservice.client.dto.response.CityRespDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface LocationClient {
    @GetExchange("/{cityId}")
    CityRespDto getCity(
            @PathVariable String cityId
    );
}
