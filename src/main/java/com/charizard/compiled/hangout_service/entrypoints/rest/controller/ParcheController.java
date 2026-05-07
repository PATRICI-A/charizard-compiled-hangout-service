package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parches")
@RequiredArgsConstructor
@Tag(name = "Parche", description = "Parche Management")
public class ParcheController {

    private final CreateParcheInputPort createParcheUseCase;
    private final GetParcheInputPort getParcheUseCase;
    private final UpdateParcheInputPort updateParcheUseCase;
    private final CloseParcheInputPort closeParcheUseCase;

    @PostMapping
    @Operation(summary = "Create parche")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parche created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Student reached max active hangouts")
    })
    public ResponseEntity<ParcheResponse> createParche(
            @Valid @RequestBody CreateParcheRequest req,
            @RequestHeader("X-User-Id") UUID captainId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createParcheUseCase.createParche(req, captainId));
    }

    @GetMapping
    @Operation(summary = "Search parches with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of parches returned")
    })
    public ResponseEntity<List<ParcheResponse>> getParches(
            @Parameter(description = "Filter by parche type") @RequestParam(required = false) ParcheType tipo,
            @Parameter(description = "Filter by parche status") @RequestParam(required = false) ParcheStatus estado,
            @Parameter(description = "Filter by name (partial match, case-insensitive)") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filter by date (format: yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Filter by available spots (true = has space, false = full)") @RequestParam(required = false) Boolean cupoDisponible) {
        return ResponseEntity.ok(getParcheUseCase.getParches(tipo, estado, nombre, fecha, cupoDisponible));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parche by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parche found"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<ParcheResponse> getParcheById(@PathVariable UUID id) {
        return ResponseEntity.ok(getParcheUseCase.getParcheById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update parche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parche updated"),
            @ApiResponse(responseCode = "403", description = "Only captain can update"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<ParcheResponse> updateParche(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateParcheRequest req,
            @RequestHeader("X-User-Id") UUID solicitanteId) {
        return ResponseEntity.ok(updateParcheUseCase.updateParche(id, req, solicitanteId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parche (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parche archived"),
            @ApiResponse(responseCode = "403", description = "Only captain can delete"),
            @ApiResponse(responseCode = "404", description = "Parche not found")
    })
    public ResponseEntity<Void> deleteParche(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID captainId) {
        closeParcheUseCase.closeParche(id, captainId);
        return ResponseEntity.noContent().build();
    }
}
