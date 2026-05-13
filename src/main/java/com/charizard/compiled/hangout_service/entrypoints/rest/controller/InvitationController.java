package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.RespondInvitationRequest;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para la gestión de invitaciones a parches privados.
 * Permite enviar, aceptar y rechazar invitaciones.
 */
@Tag(name = "Invitations", description = "Parche's Invitations Management")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationInputPort invitationService;
    private final RespondInvitationInputPort respondInvitationService;

    @Operation(
        summary = "Send invitation to a private hangout",
        description = "Sends an invitation to a student for a private hangout. Only the captain can send invitations."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Invitation sent successfully",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "403", description = "User is not the captain of this hangout"),
        @ApiResponse(responseCode = "409", description = "Student is already a member or has a pending invitation")
    })
    @PostMapping("/parches/{parcheId}/invitaciones/{studentId}")
    public ResponseEntity<InvitationResponse> sendInvitation(
            @Parameter(description = "Hangout ID", required = true)
            @PathVariable UUID parcheId,
            @Parameter(description = "Student ID to invite", required = true)
            @PathVariable UUID studentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID captainId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invitationService.sendInvitation(parcheId, captainId, studentId));
    }

    @Operation(
        summary = "Respond to an invitation",
        description = "The student accepts or rejects an invitation to join a hangout."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation responded successfully",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "403", description = "You are not the invited student"),
        @ApiResponse(responseCode = "404", description = "Invitation not found"),
        @ApiResponse(responseCode = "409", description = "Invitation already responded or hangout is full")
    })
    @PatchMapping("/invitaciones/{invitationId}")
    public ResponseEntity<InvitationResponse> respondInvitation(
            @Parameter(description = "Invitation ID", required = true)
            @PathVariable UUID invitationId,
            @Valid @RequestBody RespondInvitationRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID studentId) {

        return ResponseEntity.ok(
                respondInvitationService.respondInvitation(invitationId, studentId, request.getAnswer()));
    }

    @Operation(summary = "Accept an invitation", description = "The student accepts an invitation to join a hangout.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation accepted successfully",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "403", description = "You are not the invited student"),
        @ApiResponse(responseCode = "404", description = "Invitation not found"),
        @ApiResponse(responseCode = "409", description = "Invitation already responded or hangout is full")
    })
    @PostMapping("/invitaciones/{invitationId}/aceptar")
    public ResponseEntity<InvitationResponse> acceptInvitation(
            @Parameter(description = "Invitation ID", required = true)
            @PathVariable UUID invitationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID studentId) {

        return ResponseEntity.ok(respondInvitationService.acceptInvitation(invitationId, studentId));
    }

    @Operation(summary = "Reject an invitation", description = "The student rejects an invitation to join a hangout.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation rejected successfully",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "403", description = "You are not the invited student"),
        @ApiResponse(responseCode = "404", description = "Invitation not found"),
        @ApiResponse(responseCode = "409", description = "Invitation already responded")
    })
    @PostMapping("/invitaciones/{invitationId}/rechazar")
    public ResponseEntity<InvitationResponse> rejectInvitation(
            @Parameter(description = "Invitation ID", required = true)
            @PathVariable UUID invitationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID studentId) {

        return ResponseEntity.ok(respondInvitationService.rejectInvitation(invitationId, studentId));
    }
}
