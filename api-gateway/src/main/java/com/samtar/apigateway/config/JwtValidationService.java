package com.samtar.apigateway.config;

import com.samtar.apigateway.dto.JwtClaimsDto;
import com.samtar.enums.ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.management.relation.Role;
import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class JwtValidationService {

    private final RsaKeyService rsaKeyService;

    public Mono<JwtClaimsDto> validateTokens(String accessToken, String cookieToken) {

        return Mono.fromCallable(() -> {
            Jws<Claims> accessJws = Jwts.parser().verifyWith(rsaKeyService.getPublicKey()).build().parseSignedClaims(accessToken);

            Jws<Claims> refreshJws = Jwts.parser().verifyWith(rsaKeyService.getPublicKey()).build().parseSignedClaims(accessToken);
            Claims accessClaims = accessJws.getPayload();

            Claims cookieClaims = refreshJws.getPayload();
            return validateClaims(accessClaims, cookieClaims);
        });
    }


    private JwtClaimsDto validateClaims(Claims accessClaims, Claims cookieClaims) {

        String accessUserId = accessClaims.get("userId", String.class);

        String cookieUserId = cookieClaims.get("userId", String.class);

        if (accessUserId == null || cookieUserId == null) {

//            throw new InvalidJwtException(
//                    "userId missing"
//            );
        }

        // Both JWTs must belong to same user
        if (!accessUserId.equals(cookieUserId)) {

//            throw new InvalidJwtException(
//                    "JWT user mismatch"
//            );
        }

        String accessSessionId = accessClaims.get("sessionId", String.class);

        String cookieSessionId = cookieClaims.get("sessionId", String.class);

        if (accessSessionId == null || cookieSessionId == null) {

//            throw new InvalidJwtException(
//                    "sessionId missing"
//            );
        }

        // Both JWTs must belong to same session
        if (!accessSessionId.equals(cookieSessionId)) {

//            throw new InvalidJwtException(
//                    "JWT session mismatch"
//            );
        }

        String username = accessClaims.get("username", String.class);

        String email = accessClaims.get("email", String.class);

        ROLE role = accessClaims.get("userRole", ROLE.class);

        return new JwtClaimsDto(username, role, accessUserId, accessSessionId, email);
    }


}
