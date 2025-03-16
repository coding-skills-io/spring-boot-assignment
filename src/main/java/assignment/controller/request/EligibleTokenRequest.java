package assignment.controller.request;

import jakarta.validation.constraints.NotBlank;

public record EligibleTokenRequest(@NotBlank(message = "token cannot be empty") String token) {
}
