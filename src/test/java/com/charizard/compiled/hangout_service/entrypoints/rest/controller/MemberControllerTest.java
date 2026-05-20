package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaximumCapacityReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.LeaveParcheInputPort;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock JoinParcheInputPort joinParcheService;
    @Mock LeaveParcheInputPort leaveParcheService;

    @InjectMocks MemberController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private UUID parcheId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        objectMapper = new ObjectMapper();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(studentId.toString(), null, List.of()));
        SecurityContextHolder.setContext(context);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── POST /parches/{id}/miembros ──────────────────────────────────────

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 201 cuando el student se une exitosamente")
    void unirseAParche_valido_retorna201ConMember() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .studentId(studentId)
                .build();

        when(joinParcheService.unirseAParche(parcheId, studentId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(studentId.toString()));
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 404 cuando el parche no existe")
    void unirseAParche_parcheNoExiste_retorna404() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student ya es miembro")
    void unirseAParche_yaMiembro_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new StudentAlreadyMemberException("Student is already a member of this parche"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el parche está lleno")
    void unirseAParche_cupoLleno_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new MaximumCapacityReachedException("Parche is already full"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student tiene 5 parches activos")
    void unirseAParche_limiteParches_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new MaxHangoutsReachedException());

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId))
                .andExpect(status().isConflict());
    }

    // ─── POST /parches/{id}/miembros/leave ────────────────────────────────

    @Test
    @DisplayName("POST /parches/{id}/miembros/leave sin body retorna 204 cuando el no-owner sale")
    void salirDeParche_noOwnerSinBody_retorna204() throws Exception {
        doNothing().when(leaveParcheService).salirDeParche(eq(parcheId), eq(studentId), isNull());

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros/leave", parcheId))
                .andExpect(status().isNoContent());

        verify(leaveParcheService).salirDeParche(parcheId, studentId, null);
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros/leave con newOwnerId retorna 204 cuando el owner transfiere y sale")
    void salirDeParche_ownerConNewOwner_retorna204() throws Exception {
        UUID newOwnerId = UUID.randomUUID();
        String body = objectMapper.writeValueAsString(new java.util.HashMap<String, String>() {{
            put("newOwnerId", newOwnerId.toString());
        }});

        doNothing().when(leaveParcheService).salirDeParche(eq(parcheId), eq(studentId), eq(newOwnerId));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros/leave", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros/leave retorna 404 cuando el parche no existe")
    void salirDeParche_parcheNoExiste_retorna404() throws Exception {
        doThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId))
                .when(leaveParcheService).salirDeParche(eq(parcheId), eq(studentId), isNull());

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros/leave", parcheId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros/leave retorna 400 cuando el owner no provee newOwnerId")
    void salirDeParche_ownerSinNewOwner_retorna400() throws Exception {
        doThrow(new ResponseStatusException(BAD_REQUEST, "newOwnerId is required when the owner leaves"))
                .when(leaveParcheService).salirDeParche(eq(parcheId), eq(studentId), isNull());

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros/leave", parcheId))
                .andExpect(status().isBadRequest());
    }
}
