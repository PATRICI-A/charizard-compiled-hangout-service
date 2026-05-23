package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.domain.ports.in.CleanupUserInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoint interno para el flujo de eliminación de usuarios.
 * Llamado por el servicio de identidad cuando un usuario es borrado del sistema.
 * Limpia toda la presencia del usuario en el módulo de parches.
 */
@Tag(name = "User Cleanup", description = "Internal endpoint for user deletion cascade")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserCleanupController {

    private final CleanupUserInputPort cleanupUserUseCase;

    @Operation(
            summary = "Cleanup user from all patches",
            description = "Removes the user from all active patches. " +
                    "If the user owns a patch with other members, ownership is transferred to the oldest member. " +
                    "If the user is the sole member of a patch they own, the patch is archived. " +
                    "All invitations involving the user are also deleted. " +
                    "Internal use only — called by the identity service on account deletion."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cleanup completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid userId format")
    })
    @DeleteMapping("/parches/internal/users/{userId}/cleanup")
    public ResponseEntity<Void> cleanupUser(
            @Parameter(description = "ID of the deleted user", required = true)
            @PathVariable UUID userId) {

        cleanupUserUseCase.cleanupUser(userId);
        return ResponseEntity.noContent().build();
    }
}
