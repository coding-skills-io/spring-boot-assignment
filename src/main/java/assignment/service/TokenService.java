package assignment.service;

import assignment.config.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static assignment.config.security.filter.JWTAuthenticationFilter.*;

@Service
public class TokenService {

    private final JwtConfig jwtConfig;
    private final Clock clock;
    private final SecretKey secretKey;

    public TokenService(final JwtConfig jwtConfig, final Clock clock, final SecretKey secretKey) {
        this.jwtConfig = jwtConfig;
        this.clock = clock;
        this.secretKey = secretKey;
    }

    public String generateAccessToken(final String username, final List<String> roles) {
        return generateToken(username, jwtConfig.expiration(), ACCESS_TOKEN_TYPE, roles);
    }

    public String generateRefreshToken(final String username, final List<String> roles) {
        return generateToken(username, jwtConfig.refreshExpiration(), REFRESH_TOKEN_TYPE, roles);
    }

    public Claims parseToken(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateToken(final String username, final long expiration, final String tokenType, final List<String> roles) {
        var now = clock.instant();
        var accessTokenExpirationDate = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(username)
                .issuer("spring-boot-assignment")
                .expiration(Date.from(accessTokenExpirationDate))
                .signWith(secretKey)
                .issuedAt(Date.from(now))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_KEY, tokenType)
                .claim("roles", roles)
                .compact();
    }

}
