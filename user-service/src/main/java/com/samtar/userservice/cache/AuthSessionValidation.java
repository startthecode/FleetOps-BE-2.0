package com.samtar.userservice.cache;


import com.samtar.consts.CacheKeys;
import com.samtar.dto.cache.SessionCache;
import com.samtar.userservice.entity.SessionEntity;
import com.samtar.userservice.entity.UsersEntity;
import com.samtar.userservice.repository.SessionRepository;
import com.samtar.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthSessionValidation {
private final UserRepository userRepository;
private final SessionRepository sessionRepository;
private final CacheService cacheService;
public Boolean validateSession(String sessionID){
    String sessionCacheKey = CacheKeys.USER_SESSION + ":" + sessionID;
    SessionCache sessionCache = cacheService.get(sessionCacheKey, SessionCache.class);
    if(sessionCache == null) {
        SessionEntity sessionDtl = sessionRepository.findById(UUID.fromString(sessionID)).orElse(null);
        if(sessionDtl == null) return false;
        UsersEntity userData = sessionDtl.getUser();
        cacheService.set(sessionCacheKey,new SessionCache(
                userData.getUsername(),
                userData.getEmail(),
                userData.getRole(),
                userData.getId()
        ));
    }
    return true;
}

}
