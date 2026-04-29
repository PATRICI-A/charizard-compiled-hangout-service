package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.service.ParcheService;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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
public class ParcheController {

    private final ParcheService parcheService;

    @PostMapping
    public ResponseEntity<ParcheResponse> crearParche(
            @Valid @RequestBody CreateParcheRequest req,
            @RequestHeader("X-User-Id") UUID captainId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parcheService.crearParche(req, captainId));
    }

    @GetMapping
    public ResponseEntity<List<ParcheResponse>> obtenerParches(
            @RequestParam(required = false) ParcheType tipo,
            @RequestParam(required = false) ParcheStatus estado) {
        return ResponseEntity.ok(parcheService.obtenerParches(tipo, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcheResponse> obtenerParchePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(parcheService.obtenerParchePorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ParcheResponse> actualizarParche(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateParcheRequest req,
            @RequestHeader("X-User-Id") UUID solicitanteId) {
        return ResponseEntity.ok(parcheService.actualizarParche(id, req, solicitanteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParche(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID captainId) {
        parcheService.eliminarParche(id, captainId);
        return ResponseEntity.noContent().build();
    }
}
