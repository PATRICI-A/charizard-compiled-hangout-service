package com.charizard.compiled.hangout_service.infrastructure.adapters.feign.client;

import com.charizard.compiled.hangout_service.infrastructure.adapters.feign.dto.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for auth-service.
 *
 * Used exclusively by {@code JwtAuthFilter} to validate an incoming JWT
 * and resolve the authenticated user's details (id, role, active status).
 *
 * No JWT forwarding interceptor is applied here — the token is passed
 * explicitly as a parameter because this call IS the validation step.
 *
 * Base URL: AUTH_SERVICE_URL env var (default: http://localhost:8081).
 */
@FeignClient(
        name = "auth-service",
        url = "${services.auth.url}"
)
public interface AuthFeignClient {

    /**
     * Validates the Bearer token and returns the associated user's profile.
     * auth-service returns 401 if the token is invalid or expired.
     *
     * @param authorization full header value, e.g. "Bearer eyJ..."
     */
    @GetMapping("/api/v1/auth/me")
    UserDetailsResponse getAuthenticatedUser(@RequestHeader("Authorization") String authorization);
}
