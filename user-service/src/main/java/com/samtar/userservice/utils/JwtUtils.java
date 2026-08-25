package com.samtar.userservice.utils;


import com.samtar.enums.ROLE;
import com.samtar.exception.BaseException;
import com.samtar.exception.TokenExceptions;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.dto.common.JwtClaimsDto;
import com.samtar.userservice.enums.TokenTypes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private final Long refreshTokenExpiry;
    private final Long accessTokenExpiry;

    private PrivateKey privateKey;

    public JwtUtils(
        @Value("${app.security.jwt.access-token-hex}") String accessKey,
        @Value("${app.security.jwt.refresh-token-hex}") String refreshKey,
        @Value("${app.security.jwt.access-expiry}") Long accessTokenExpiry,
        @Value("${app.security.jwt.refresh-expiry}") Long refreshTokenExpiry,
        PrivateKey privateKey
            ) {
        this.accessKey = toSecretKey(accessKey);
        this.refreshKey = toSecretKey(refreshKey);
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.privateKey = privateKey;
    }

    public String generateToken(TokenTypes tokenType, JwtClaimsDto data) {
        SecretKey secretKey = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshKey : this.accessKey;
        long expiry = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshTokenExpiry : this.accessTokenExpiry;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", data.userRole());
        claims.put("username", data.username());
        claims.put("userid", data.userId());
        claims.put("sessionId", data.sessionId());
        claims.put("email", data.email());
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .issuedAt(new Date())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public JwtClaimsDto decodeToken(String token, TokenTypes tokenType) throws Exception {
        SecretKey secretKey = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshKey : this.accessKey;
        try {
            return parseClaims(token, secretKey);
        } catch (ExpiredJwtException ex) {
            throw new TokenExceptions(MessageConstant.EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (JwtException ex) {
            throw new TokenExceptions(MessageConstant.INVALID_TOKEN,HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            log.info("error --- ",ex);
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE,HttpStatus.UNAUTHORIZED);
        }
    }


    private JwtClaimsDto parseClaims(String token, SecretKey key) {
        Claims data = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new JwtClaimsDto(
                (String) data.get("username"),
                ROLE.valueOf((String) data.get("role")),
                (String) data.get("userid"),
                (String) data.get("sessionId"),
                (String) data.get("email")
        );

    }


    private SecretKey toSecretKey(String normalKey) {
        byte[] key = HexFormat.of().parseHex(normalKey.trim());
        return Keys.hmacShaKeyFor(key);
    }

}
