package com.portfolio.helpdesk.common.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

    private static final int MIN_KEY_BYTES = 32;   // 256 bits, required for HS256

    private final JwtProperties props;
    private final SecretKey signingKey;

    public JwtConfig(JwtProperties props) {
        this.props = props;
        this.signingKey = buildKey(props.secret());
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(signingKey));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(signingKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(props.issuer()));
        return decoder;
    }

    private static SecretKey buildKey(String base64Secret) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64Secret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("JWT_SECRET is not valid Base64", ex);
        }
        if (bytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET must decode to at least 256 bits (32 bytes); got " + (bytes.length * 8) + " bits");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}