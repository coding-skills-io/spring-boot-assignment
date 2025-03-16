package assignment.fixtures;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static assignment.config.security.filter.JWTAuthenticationFilter.*;

public class TestFixtures {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static SecretKey SECRET_KEY = Keys.hmacShaKeyFor("test-jwt-secret-should-be-stored-securely-in-aws-secrets-manager-or-parameter-store".getBytes(StandardCharsets.UTF_8));

    public static String generateAccessToken(final Date expirationDate) {
        return generateToken(expirationDate, ACCESS_TOKEN_TYPE);
    }

    public static String generateRefreshToken(final Date expirationDate) {
        return generateToken(expirationDate, REFRESH_TOKEN_TYPE);
    }

    public static String generateToken(final Date date, final String tokenType) {
        return Jwts.builder()
                .subject("user")
                .issuer("spring-boot-assignment")
                .expiration(date)
                .signWith(SECRET_KEY)
                .issuedAt(Date.from(Instant.now()))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_KEY, tokenType)
                .claim("roles", List.of("ROLE_API_USER"))
                .compact();
    }





}
