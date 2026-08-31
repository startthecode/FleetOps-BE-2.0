package com.samtar.apigateway.client;

import com.samtar.dto.SuccessApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class UserServiceClient {
    @Value("${app.security.cookie.auth-token.name}")
    String refCookieName;
    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder, @Value("${client.userervice.baseurl}") String url){
        System.out.println(url);
        webClient = builder.baseUrl(url).build();
    }

    public Mono<SuccessApiResponse<Boolean>> updateOrRejectSession(
            String sessionId,
            String accessToken,
            String cookieToken) {


        return webClient.post()
                .uri("/api/v1/internal/sessions/{sessionId}/hydrate", sessionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.COOKIE, refCookieName + "=" + cookieToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SuccessApiResponse<Boolean>>() {});
    }

}
