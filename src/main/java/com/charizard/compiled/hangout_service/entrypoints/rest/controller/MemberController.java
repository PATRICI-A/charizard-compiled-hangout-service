package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.LeaveParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.LeaveParcheInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para la gestión de membresías.
 * Permite a los estudiantes unirse o salir de parches públicos.
 */
@Tag(name = "Member", description = "manage membership in Parches")
@RestController
@RequestMapping("/api/v1/parches/{parcheId}/miembros")
@RequiredArgsConstructor
public class MemberController {

    private final JoinParcheInputPort joinParcheService;
    private final LeaveParcheInputPort leaveParcheService;

    @Operation(
        summary = "Join a public hangout",
        description = "Allows a student to join a public active hangout directly, without invitation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Successfully joined the hangout",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))),
        @ApiResponse(responseCode = "400", description = "Hangout is archived"),
        @ApiResponse(responseCode = "404", description = "Hangout not found"),
        @ApiResponse(responseCode = "409", description = "Already a member, hangout full, or student has 5 active hangouts")
    })
    @PostMapping
    public ResponseEntity<MemberResponse> unirseAParche(
            @Parameter(description = "Hangout ID", required = true)
            @PathVariable UUID parcheId,
            @AuthenticationPrincipal UUID studentId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(joinParcheService.unirseAParche(parcheId, studentId));
    }

    @Operation(
        summary = "Leave a hangout",
        description = "Leave a hangout. If the caller is the owner and there are other members, " +
                      "newOwnerId is required in the request body. If the owner is the only member, " +
                      "the hangout is archived automatically."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully left the hangout"),
        @ApiResponse(responseCode = "400", description = "Hangout is archived, or owner must provide newOwnerId"),
        @ApiResponse(responseCode = "404", description = "Hangout not found or student is not a member")
    })
    @PostMapping("/leave")
    public ResponseEntity<Void> salirDeParche(
            @Parameter(description = "Hangout ID", required = true)
            @PathVariable UUID parcheId,
            @AuthenticationPrincipal UUID studentId,
            @RequestBody(required = false) LeaveParcheRequest body) {

        UUID newOwnerId = (body != null) ? body.getNewOwnerId() : null;
        leaveParcheService.salirDeParche(parcheId, studentId, newOwnerId);
        return ResponseEntity.noContent().build();
    }
}
