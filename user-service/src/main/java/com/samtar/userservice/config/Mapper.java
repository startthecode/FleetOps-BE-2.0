package com.samtar.userservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class Mapper {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
