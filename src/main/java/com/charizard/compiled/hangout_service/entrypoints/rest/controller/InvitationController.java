package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.SendInvitationRequest;
import com.charizard.compiled.hangout_service.application.dto.request.RespondInvitationRequest;
import com.charizard.compiled.hangout_service.application.dto.response.SendInvitationResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.RespondInvitationInputPort;
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

import java.util.UUID;

@Tag(name = "Invitaciones", description = "Endpoints para gestionar invitaciones de parches")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationInputPort invitationService;
    private final RespondInvitationInputPort respondInvitationService;

    @Operation(
        summary = "Enviar invitaciones a un parche privado",
        description = "Envía invitaciones a estudiantes para un parche privado. Solo el capitán puede enviar invitaciones."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Invitaciones procesadas exitosamente",
            content = @Content(schema = @Schema(implementation = SendInvitationResponse.class))
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
    @PostMapping("/parches/{parcheId}/invitations")
    public ResponseEntity<SendInvitationResponse> sendInvitations(
            @Parameter(description = "ID del parche", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID parcheId,
            @Valid @RequestBody SendInvitationRequest request,
            @RequestHeader("X-User-Id") UUID captainId) {

        SendInvitationResponse response = invitationService.sendInvitation(
                parcheId, captainId, request.getStudentIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Responder una invitación", description = "El estudiante acepta o rechaza una invitación a un parche.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitación respondida exitosamente",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "403", description = "No eres el invitado de esta invitación"),
        @ApiResponse(responseCode = "404", description = "Invitación no encontrada"),
        @ApiResponse(responseCode = "409", description = "La invitación ya fue respondida o el parche está lleno")
    })
    @PatchMapping("/invitations/{invitationId}")
    public ResponseEntity<InvitationResponse> respondInvitation(
            @Parameter(description = "ID de la invitación", required = true)
            @PathVariable UUID invitationId,
            @Valid @RequestBody RespondInvitationRequest request,
            @RequestHeader("X-User-Id") UUID studentId) {

        InvitationResponse response = respondInvitationService.respondInvitation(
                invitationId, studentId, request.getAnswer());

        return ResponseEntity.ok(response);
    }
}
