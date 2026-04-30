package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.service.ParcheService;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parches")
@RequiredArgsConstructor
@Tag(name = "Parche", description = "Parche Management")
public class ParcheController {

    private final ParcheService parcheService;

    @PostMapping
    @Operation(summary = "Create parche")
    public ResponseEntity<ParcheResponse> createParche(
            @Valid @RequestBody CreateParcheRequest req,
            @RequestHeader("X-User-Id") UUID captainId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parcheService.createParche(req, captainId));
    }

    @GetMapping
    @Operation(summary = "Get all Parches")
    public ResponseEntity<List<ParcheResponse>> getParches(
            @RequestParam(required = false) ParcheType tipo,
            @RequestParam(required = false) ParcheStatus estado) {
        return ResponseEntity.ok(parcheService.getParches(tipo, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parches by ID")
    public ResponseEntity<ParcheResponse> getParcheById(@PathVariable UUID id) {
        return ResponseEntity.ok(parcheService.getParcheById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update parche")
    public ResponseEntity<ParcheResponse> updateParche(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateParcheRequest req,
            @RequestHeader("X-User-Id") UUID solicitanteId) {
        return ResponseEntity.ok(parcheService.updateParche(id, req, solicitanteId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parche")
    public ResponseEntity<Void> deleteParche(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID captainId) {
        parcheService.deleteParche(id, captainId);
        return ResponseEntity.noContent().build();
    }
}
