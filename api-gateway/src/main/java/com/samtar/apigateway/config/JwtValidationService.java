package com.samtar.apigateway.config;

import com.samtar.apigateway.constants.MessageConstant;
import com.samtar.apigateway.dto.JwtClaimsDto;
import com.samtar.enums.ROLE;
import com.samtar.exception.BaseException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.management.relation.Role;
import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class JwtValidationService {
    private final AuthSessionValidation authSessionValidation;
    private final RsaKeyService rsaKeyService;

    public Mono<JwtClaimsDto> validateTokens(String accessToken, String cookieToken) {

        return Mono.fromCallable(() -> {
            try {
                Jws<Claims> accessJws = Jwts.parser()
                        .verifyWith(
                                rsaKeyService
                                        .getPublicKey())
                        .build().parseSignedClaims(accessToken);

                Jws<Claims> refreshJws = Jwts.parser()
                        .verifyWith(
                                rsaKeyService
                                        .getPublicKey())
                        .build().parseSignedClaims(cookieToken);
                Claims accessClaims = accessJws.getPayload();

                Claims cookieClaims = refreshJws.getPayload();
                return validateClaims(accessClaims, cookieClaims);
            } catch (ExpiredJwtException e) {
                System.out.println(e);
                throw new BaseException(
                        MessageConstant.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED
                );
            } catch (JwtException e) {
                System.out.println(e);

                throw new BaseException(
                        MessageConstant.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED
                );
            }
        }).flatMap(e -> authSessionValidation.validateSession(e.sessionId(), accessToken, cookieToken).flatMap(
                f -> f ? Mono.just(e) : Mono.error(new Exception("rdsg"))
        ));
    }


    private JwtClaimsDto validateClaims(Claims accessClaims, Claims cookieClaims) {

        String accessUserId = accessClaims.get("userid", String.class);
        String cookieUserId = cookieClaims.get("userid", String.class);

        if (accessUserId == null || cookieUserId == null) {

            throw new BaseException(
                    MessageConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED
            );
        }

        // Both JWTs must belong to same user
        if (!accessUserId.equals(cookieUserId)) {
            throw new BaseException(
                    MessageConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED
            );
        }

        String accessSessionId = accessClaims.get("sessionId", String.class);

        String cookieSessionId = cookieClaims.get("sessionId", String.class);

        if (accessSessionId == null || cookieSessionId == null) {

            throw new BaseException(
                    MessageConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED
            );
        }

        // Both JWTs must belong to same session
        if (!accessSessionId.equals(cookieSessionId)) {
            throw new BaseException(
                    MessageConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED
            );
        }

        String username = accessClaims.get("username", String.class);

        String email = accessClaims.get("email", String.class);

        ROLE role = ROLE.valueOf(accessClaims.get("role", String.class));

        return new JwtClaimsDto(username, role, accessUserId, accessSessionId, email);
    }


}
