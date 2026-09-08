package com.samtar.apigateway.config;

import com.samtar.apigateway.constants.MessageConstant;
import com.samtar.apigateway.dto.JwtClaimsDto;
import com.samtar.consts.ReqHeadersKeys;
import com.samtar.consts.Routes;
import com.samtar.dto.ExceptionApiResponse;
import com.samtar.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter, Ordered {
    private final ObjectMapper mapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    List<String> publicRoutes = List.of(Routes.unprotected);
    @Value("${app.security.cookie.auth-token.name}")
    String refCookieName;

    private final JwtValidationService jwtValidationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        String path = exchange
                .getRequest()
                .getURI()
                .getPath();

        if (publicRoutes.stream().anyMatch(e -> pathMatcher.match(e, path))) {
            return chain.filter(exchange);
        }

        // Auth header token
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String accessToken = extractBearerToken(authorization);
        if (accessToken == null) {
            return exceptionResponse(exchange, MessageConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        // cookie token
        String cookieToken = exchange
                .getRequest()
                .getCookies()
                .getFirst(refCookieName) != null
                ? exchange.getRequest()
                .getCookies()
                .getFirst(refCookieName)
                .getValue()
                : null;

        if (cookieToken == null || cookieToken.isBlank()) {
            return exceptionResponse(exchange, MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
        }

        return jwtValidationService.validateTokens(accessToken, cookieToken).flatMap(e ->
                forwardAuthenticatedRequest(exchange, chain, e)).onErrorResume(BaseException.class, ex -> exceptionResponse(
                null,
                ex.getMessage(),
                (HttpStatus) ex.getStatusCode()
        )).onErrorResume(Exception.class, ex -> exceptionResponse(
                exchange,
                MessageConstant.INVALID_TOKEN,
                HttpStatus.UNAUTHORIZED
        ));
    }


    private Mono<Void> forwardAuthenticatedRequest(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            JwtClaimsDto user) {
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .headers(headers -> {
                    // Remove client-supplied identity headers
                    String userId = ReqHeadersKeys.USER_ID;
                    String username = ReqHeadersKeys.USER_USERNAME;
                    String email = ReqHeadersKeys.USER_EMAIL;
                    String role = ReqHeadersKeys.USER_ROLE;
                    String sessionId = ReqHeadersKeys.USER_SESSION_ID;

                    headers.remove(userId);
                    headers.remove(username);
                    headers.remove(email);
                    headers.remove(role);
                    headers.remove(sessionId);

                    // Add trusted values from validated JWT
                    headers.set(userId, user.userId());
                    headers.set(username, user.username());
                    headers.set(email, user.email());
                    headers.set(role, user.userRole().toString());
                    headers.set(sessionId, user.sessionId());
                })
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    private String extractAuthToken(String token) {
        return token.substring(7);
    }

    private String extractBearerToken(
            String authorization) {

        if (authorization == null) {
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization
                .substring(7)
                .trim();

        return token.isBlank()
                ? null
                : token;
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private Mono<Void> exceptionResponse(ServerWebExchange exchange, String message, HttpStatus httpStatus) {

        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ExceptionApiResponse<?> errorResponse =
                ExceptionApiResponse.of(null, message);

        try {
            byte[] bytes = mapper.writeValueAsBytes(errorResponse);

            DataBuffer buffer =
                    response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {
            return Mono.error(e);
        }
    }


}