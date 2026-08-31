package com.samtar.apigateway.config;


import com.samtar.apigateway.client.UserServiceClient;
import com.samtar.apigateway.service.CacheService;
import com.samtar.consts.CacheKeys;
import com.samtar.dto.cache.SessionCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthSessionValidation {
private final CacheService cacheService;
private final UserServiceClient userServiceClient;

public Mono<Boolean> validateSession(String sessionID,String accessToken,
                                     String cookieToken){
    String sessionCacheKey = CacheKeys.USER_SESSION + ":" + sessionID;
        if(cacheService.get(sessionCacheKey, SessionCache.class) != null){
            return Mono.just(true);
        }

        return  userServiceClient
                .updateOrRejectSession(sessionID, accessToken,cookieToken)
                .map(e-> {
                    System.out.println(e);
                    System.out.println(cacheService.get(sessionCacheKey, SessionCache.class));
                   return e.data() && cacheService.get(sessionCacheKey, SessionCache.class) != null;
                });

}

}
