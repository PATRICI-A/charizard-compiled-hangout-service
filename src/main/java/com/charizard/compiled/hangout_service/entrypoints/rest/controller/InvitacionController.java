package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.EnviarInvitacionRequest;
import com.charizard.compiled.hangout_service.application.dto.response.EnviarInvitacionResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitacionInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Invitaciones", description = "Endpoints para gestionar invitaciones de parches")
@RestController
@RequestMapping("/api/v1/parches")
@RequiredArgsConstructor
public class InvitacionController {

    private final InvitacionInputPort invitacionService;

    @Operation(
        summary = "Enviar invitaciones a un parche privado",
        description = "Envía invitaciones a estudiantes para un parche privado. Solo el capitán puede enviar invitaciones."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Invitaciones procesadas exitosamente",
            content = @Content(schema = @Schema(implementation = EnviarInvitacionResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No autorizado - El usuario no es el capitán del parche"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto - El estudiante ya es miembro o ya tiene una invitación pendiente"
        )
    })
    @PostMapping("/{parcheId}/invitaciones")
    public ResponseEntity<EnviarInvitacionResponse> enviarInvitaciones(
            @Parameter(description = "ID del parche", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID parcheId,
            @Valid @RequestBody EnviarInvitacionRequest request,
            Principal principal) {

        UUID capitanId = UUID.fromString(principal.getName());

        EnviarInvitacionResponse response = invitacionService.enviarInvitacion(
                parcheId, capitanId, request.getEstudiantesIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
