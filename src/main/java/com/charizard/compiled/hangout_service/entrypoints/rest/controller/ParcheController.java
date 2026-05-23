package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.CategoryResponse;
import com.charizard.compiled.hangout_service.application.dto.response.EventResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheDetailResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.dto.response.PlaceResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.GetOpcionesInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.GetParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.UpdateParcheInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de parches.
 * Expone endpoints para crear, consultar, actualizar y archivar parches.
 */
@RestController
@RequestMapping("/api/v1/parches")
@RequiredArgsConstructor
@Tag(name = "Parche", description = "Parche Management")
public class ParcheController {

    private final CreateParcheInputPort createParcheUseCase;
    private final GetParcheInputPort getParcheUseCase;
    private final UpdateParcheInputPort updateParcheUseCase;
    private final CloseParcheInputPort closeParcheUseCase;
    private final GetOpcionesInputPort getOpcionesUseCase;

    @PostMapping
    @Operation(summary = "Create parche")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parche created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Student reached max active hangouts")
    })
    public ResponseEntity<ParcheResponse> createParche(
            @Valid @RequestBody CreateParcheRequest req,
            @Parameter(hidden = true) @AuthenticationPrincipal String ownerIdStr) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createParcheUseCase.createParche(req, UUID.fromString(ownerIdStr)));
    }

    @GetMapping
    @Operation(summary = "Search PUBLIC + ACTIVE parches with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of parches returned")
    })
    public ResponseEntity<List<ParcheResponse>> getParches(
            @Parameter(description = "Filter by name (partial, case-insensitive)") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filter by date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Filter by category") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filter by campus zone code (from geo-service, ej: ED_A)") @RequestParam(required = false) String lugar,
            @Parameter(description = "true = has space, false = full") @RequestParam(required = false) Boolean cupoDisponible) {
        return ResponseEntity.ok(getParcheUseCase.getParches(nombre, fecha, categoria, lugar, cupoDisponible));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my active parches (PUBLIC and PRIVATE where I am a member)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active parches for the authenticated user")
    })
    public ResponseEntity<List<ParcheResponse>> getMyParches(
            @Parameter(hidden = true) @AuthenticationPrincipal String userIdStr) {
        return ResponseEntity.ok(getParcheUseCase.getMyParches(UUID.fromString(userIdStr)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parche detail by ID (enriched with members, place, event)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parche detail found"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<ParcheDetailResponse> getParcheById(@PathVariable UUID id) {
        return ResponseEntity.ok(getParcheUseCase.getParcheById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update parche (owner only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parche updated"),
            @ApiResponse(responseCode = "403", description = "Only the owner can update"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<ParcheResponse> updateParche(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateParcheRequest req,
            @AuthenticationPrincipal String solicitanteIdStr) {
        return ResponseEntity.ok(updateParcheUseCase.updateParche(id, req, UUID.fromString(solicitanteIdStr)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archive parche (admin only — soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parche archived"),
            @ApiResponse(responseCode = "403", description = "Only ADMIN can archive a parche"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<Void> deleteParche(@PathVariable UUID id) {
        closeParcheUseCase.closeParche(id);
        return ResponseEntity.noContent().build();
    }

    // ── Opciones para formularios de creación / edición ──────────────────────

    @GetMapping("/opciones/categorias")
    @Operation(summary = "List available categories for parche creation/edit form")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories list (empty if category-service unavailable)")
    })
    public ResponseEntity<List<CategoryResponse>> getCategorias() {
        return ResponseEntity.ok(getOpcionesUseCase.getCategorias());
    }

    @GetMapping("/opciones/lugares")
    @Operation(summary = "List available places for parche creation/edit form")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Places list (empty if place-service unavailable)")
    })
    public ResponseEntity<List<PlaceResponse>> getLugares() {
        return ResponseEntity.ok(getOpcionesUseCase.getLugares());
    }

    @GetMapping("/opciones/eventos")
    @Operation(summary = "List available events for parche creation/edit form")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events list (empty if event-service unavailable)")
    })
    public ResponseEntity<List<EventResponse>> getEventos() {
        return ResponseEntity.ok(getOpcionesUseCase.getEventos());
    }
}
