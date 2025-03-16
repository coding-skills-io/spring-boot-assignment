package assignment.config.security;

import assignment.config.security.filter.JWTAuthenticationFilter;
import assignment.config.security.filter.JWTAuthorizationFilter;
import assignment.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    private final SecretKey secretKey;

    public SecurityConfig(final ObjectMapper objectMapper, final TokenService tokenService, final SecretKey secretKey) {
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
        this.secretKey = secretKey;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http, final AuthenticationManager authenticationManager) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                                    response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
                                }
                        )
                )
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/login", "/api/login/refresh", "/actuator/health").permitAll()
                                .requestMatchers("/api/members/search").authenticated()
                                .requestMatchers("/api/eligibility/verify").hasRole("API_USER")
                )
                .addFilter(new JWTAuthenticationFilter(authenticationManager, objectMapper, tokenService))
                .addFilterAfter(new JWTAuthorizationFilter(secretKey), JWTAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("{noop}password")
                        .roles("API_USER")
                        .build()
        );
    }

}
