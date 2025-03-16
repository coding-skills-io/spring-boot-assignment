package assignment.controller;

import assignment.exception.InvalidTokenException;
import assignment.controller.request.RefreshTokenRequest;
import assignment.controller.response.TokenResponseBody;
import assignment.service.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static assignment.config.security.filter.JWTAuthenticationFilter.REFRESH_TOKEN_TYPE;

@RestController
@RequestMapping("/api/login")
public class RefreshTokenController {

    private final TokenService tokenService;

    public RefreshTokenController(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/refresh")
    public TokenResponseBody refreshToken(@RequestBody final RefreshTokenRequest refreshTokenRequest, final HttpServletResponse response) {
        try {
            var claims = tokenService.parseToken(refreshTokenRequest.refreshToken());

            if (!REFRESH_TOKEN_TYPE.equals(claims.get("token_type"))) {
                throw new InvalidTokenException("Token is not a refresh token");
            }

            var username = claims.getSubject();
            List<String> roles = (List<String>) claims.get("roles");

            var accessToken = tokenService.generateAccessToken(username, roles);

            return new TokenResponseBody(accessToken);
        } catch (final JwtException jwtException) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

}
