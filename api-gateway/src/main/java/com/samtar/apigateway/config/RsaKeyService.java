package com.samtar.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyService {
    private final RSAPublicKey publicKey;

    public RsaKeyService() throws Exception {
        this.publicKey = publicKey();
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public RSAPublicKey publicKey() throws Exception {
        ClassPathResource resource =
                new ClassPathResource("keys/public.pem");

        String pem = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String publicKeyContent = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}
