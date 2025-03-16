package assignment.controller;

import assignment.controller.request.EligibleTokenRequest;
import assignment.controller.response.JwtVerifyResponseBody;
import assignment.controller.response.JwtVerifyResponseBodyBuilder;
import assignment.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static assignment.config.security.filter.JWTAuthenticationFilter.TOKEN_TYPE_KEY;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final TokenService tokenService;

    public EligibilityController(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/verify")
    public JwtVerifyResponseBody verify(@RequestBody EligibleTokenRequest request) {
        var claims = tokenService.parseToken(request.token());

        return JwtVerifyResponseBodyBuilder.builder()
                .eligible(true)
                .user(claims.getSubject())
                .roles(claims.get("roles", List.class))
                .tokenType(claims.get(TOKEN_TYPE_KEY, String.class))
                .build();
    }

}
