package assignment.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtConfig(String secret, long expiration, long refreshExpiration) {
}
