package com.charizard.compiled.hangout_service.infrastructure.adapters.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload returned by auth-service when validating a JWT token.
 * Only the fields hangout-service actually needs are mapped here.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {

    private String id;
    private String username;
    private String email;
    private String role;
    private boolean active;
}
