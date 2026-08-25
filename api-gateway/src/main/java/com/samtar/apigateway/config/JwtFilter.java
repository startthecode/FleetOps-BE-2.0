package com.samtar.apigateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

@Configuration
public class JwtFilter {

    @Bean
    JwtDecoder accessTokenDecoder() {
        return NimbusJwtDecoder
                .withPublicKey(accessPublicKey)
                .build();
    }

}