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
import org.hibernate.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private final Long refreshTokenExpiry;
    private final Long accessTokenExpiry;

    public JwtUtils(
            @Value("${jwt.access-token-hex}") String accessKey,
            @Value("${jwt.refresh-token-hex}") String refreshKey,
            @Value("${jwt.access-expiry}") Long accessTokenExpiry,
            @Value("${jwt.refresh-expiry}") Long refreshTokenExpiry

    ) {
        this.accessKey = toSecretKey(accessKey);
        this.refreshKey = toSecretKey(refreshKey);
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateToken(TokenTypes tokenType, JwtClaimsDto data) {
        SecretKey secretKey = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshKey : this.accessKey;
        long expiry = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshTokenExpiry : this.accessTokenExpiry;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", data.userRole());
        claims.put("username", data.username());
        return Jwts.builder().signWith(secretKey).claims(claims).expiration(new Date(System.currentTimeMillis() + expiry)).issuedAt(new Date()).compact();
    }

    public JwtClaimsDto decodeToken(String token, TokenTypes tokenType) throws Exception {
        SecretKey secretKey = TokenTypes.REFRESH_TOKEN == tokenType ? this.refreshKey : this.accessKey;
        try {
            return parseClaims(token, secretKey);
        } catch (ExpiredJwtException ex) {
            throw new TokenExceptions(MessageConstant.EXPIRED_TOKEN);
        } catch (JwtException ex) {
            throw new TokenExceptions(MessageConstant.INVALID_TOKEN);

        } catch (Exception ex) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE);
        }
    }


    private JwtClaimsDto parseClaims(String token, SecretKey key) {
        Claims data = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new JwtClaimsDto(
                (String) data.get("username"),
                (ROLE) data.get("role"));
    }


    private SecretKey toSecretKey(String normalKey) {
        byte[] key = HexFormat.of().parseHex(normalKey.trim());
        return Keys.hmacShaKeyFor(key);
    }

}
