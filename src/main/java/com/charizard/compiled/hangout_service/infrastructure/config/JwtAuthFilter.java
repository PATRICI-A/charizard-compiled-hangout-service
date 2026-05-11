package com.charizard.compiled.hangout_service.infrastructure.config;

import com.charizard.compiled.hangout_service.infrastructure.adapters.feign.client.AuthFeignClient;
import com.charizard.compiled.hangout_service.infrastructure.adapters.feign.dto.UserDetailsResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Validates every incoming request against auth-service via OpenFeign.
 *
 * Flow:
 *  1. Extract "Authorization: Bearer <token>" from the request.
 *  2. Call auth-service GET /api/v1/auth/me with that header.
 *  3. On success → populate SecurityContext with the user's UUID and role.
 *  4. On any failure (missing token, 401 from auth-service, network error)
 *     → reject with 401. Fail-closed by design.
 *
 * Swagger / OpenAPI paths are excluded so the docs remain reachable.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthFeignClient authFeignClient;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            rejectUnauthorized(response, "Missing or malformed Authorization header");
            return;
        }

        try {
            UserDetailsResponse user = authFeignClient.getAuthenticatedUser(authHeader);

            if (!user.isActive()) {
                rejectUnauthorized(response, "User account is inactive");
                return;
            }

            UUID userId = UUID.fromString(user.getId());
            String role = "ROLE_" + (user.getRole() != null ? user.getRole().toUpperCase() : "USER");

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[Auth] Authenticated user {} with role {}", userId, role);

        } catch (Exception ex) {
            log.warn("[Auth] Token validation failed: {}", ex.getMessage());
            rejectUnauthorized(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void rejectUnauthorized(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}"
        );
    }
}
