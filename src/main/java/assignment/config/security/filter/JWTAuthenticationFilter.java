package assignment.config.security.filter;

import assignment.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String TOKEN_TYPE_KEY = "token_type";
    public static final String ACCESS_TOKEN_TYPE = "access_token";
    public static final String REFRESH_TOKEN_TYPE = "refresh_token";

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager, final ObjectMapper objectMapper, final TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;

        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            var user = objectMapper.readValue(request.getInputStream(), User.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.username(), user.password()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        var username = authResult.getName();
        List<String> roles = authResult.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var accessToken = tokenService.generateAccessToken(username, roles);
        var refreshToken = tokenService.generateRefreshToken(username, roles);

        var responseBody =
                """
                {
                    "accessToken": "%s",
                    "refreshToken": "%s"
                }
                """.formatted(accessToken, refreshToken);

        response.addHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("application/json");
        response.getWriter().write(responseBody);
    }

    record User(String username, String password) { }

}