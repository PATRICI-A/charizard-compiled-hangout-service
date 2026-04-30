package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.EnviarInvitacionRequest;
import com.charizard.compiled.hangout_service.application.dto.request.ResponderInvitacionRequest;
import com.charizard.compiled.hangout_service.application.dto.response.EnviarInvitacionResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitacionResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitacionInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.ResponderInvitacionInputPort;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvitacionController {

    private final InvitacionInputPort invitacionService;
    private final ResponderInvitacionInputPort responderInvitacionService;

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
    @PostMapping("/parches/{parcheId}/invitaciones")
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

    @Operation(summary = "Responder una invitación", description = "El estudiante acepta o rechaza una invitación a un parche.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitación respondida exitosamente",
            content = @Content(schema = @Schema(implementation = InvitacionResponse.class))),
        @ApiResponse(responseCode = "403", description = "No eres el invitado de esta invitación"),
        @ApiResponse(responseCode = "404", description = "Invitación no encontrada"),
        @ApiResponse(responseCode = "409", description = "La invitación ya fue respondida o el parche está lleno")
    })
    @PatchMapping("/invitaciones/{invitacionId}")
    public ResponseEntity<InvitacionResponse> responderInvitacion(
            @Parameter(description = "ID de la invitación", required = true)
            @PathVariable UUID invitacionId,
            @Valid @RequestBody ResponderInvitacionRequest request,
            Principal principal) {

        UUID estudianteId = UUID.fromString(principal.getName());
        InvitacionResponse response = responderInvitacionService.responderInvitacion(
                invitacionId, estudianteId, request.getRespuesta());

        return ResponseEntity.ok(response);
    }
}
