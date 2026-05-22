package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Internal - Analytics", description = "Internal endpoints for AnalyticsService inter-service calls")
@RestController
@RequestMapping("/api/v1/parches/internal")
@RequiredArgsConstructor
public class InternalAnalyticsController {

    private final JoinParcheInputPort joinParcheService;

    @GetMapping("/user/{userId}/parche-count")
    @Operation(summary = "Get total parche count for a user (inter-service)")
    @ApiResponse(responseCode = "200", description = "Parche count returned")
    public ResponseEntity<Integer> getUserParcheCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(joinParcheService.getUserParcheCount(userId));
    }
}
