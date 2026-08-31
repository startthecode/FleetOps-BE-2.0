package com.samtar.apigateway.config;


import com.samtar.apigateway.constants.MessageConstant;
import com.samtar.consts.Routes;
import com.samtar.dto.ExceptionApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
private final ObjectMapper mapper;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {

        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((exchange, exception) -> {

                            ServerHttpResponse response = exchange.getResponse();

                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            ExceptionApiResponse<?> errorResponse =
                                    ExceptionApiResponse.of(
                                            null,
                                            MessageConstant.UNAUTHORIZED_USER
                                    );

                            try {
                                byte[] bytes = mapper.writeValueAsBytes(errorResponse);

                                DataBuffer buffer =
                                        response.bufferFactory().wrap(bytes);

                                return response.writeWith(Mono.just(buffer));

                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        })
                )
                .build();
    }

}
