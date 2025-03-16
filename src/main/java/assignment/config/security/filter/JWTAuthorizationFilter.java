package assignment.config.security.filter;

import assignment.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

import static assignment.config.security.filter.JWTAuthenticationFilter.ACCESS_TOKEN_TYPE;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;


    public JWTAuthorizationFilter(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");

            try {
                var claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                if (!ACCESS_TOKEN_TYPE.equals(claims.get("token_type"))) {
                    throw new InvalidTokenException("Token is not an access token");
                }

                List<GrantedAuthority> roles = claims.get("roles", List.class)
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .toList();

                var authToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, roles);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (final JwtException e) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("""
                    {
                        "message": "Invalid or expired authorization header"
                    }
                    """);

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
