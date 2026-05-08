package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaximumCapacityReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.LeaveParcheInputPort;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock JoinParcheInputPort joinParcheService;
    @Mock LeaveParcheInputPort leaveParcheService;

    @InjectMocks MemberController controller;

    MockMvc mockMvc;

    private UUID parcheId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();
    }

    // ─── POST /parches/{id}/miembros ──────────────────────────────────────

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 201 cuando el student se une exitosamente")
    void unirseAParche_valido_retorna201ConMember() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .studentId(studentId)
                .memberRole(MemberRole.STUDENT)
                .build();

        when(joinParcheService.unirseAParche(parcheId, studentId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(studentId.toString()))
                .andExpect(jsonPath("$.memberRole").value("STUDENT"));
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 404 cuando el parche no existe")
    void unirseAParche_parcheNoExiste_retorna404() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student ya es miembro")
    void unirseAParche_yaMiembro_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new StudentAlreadyMemberException("Student is already a member of this parche"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el parche está lleno")
    void unirseAParche_cupoLleno_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new MaximumCapacityReachedException("Parche is already full"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student tiene 5 parches activos")
    void unirseAParche_limiteParches_retorna409() throws Exception {
        when(joinParcheService.unirseAParche(parcheId, studentId))
                .thenThrow(new MaxHangoutsReachedException());

        mockMvc.perform(post("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    // ─── DELETE /parches/{id}/miembros ────────────────────────────────────

    @Test
    @DisplayName("DELETE /parches/{id}/miembros retorna 204 cuando el student sale exitosamente")
    void salirDeParche_valido_retorna204() throws Exception {
        doNothing().when(leaveParcheService).salirDeParche(parcheId, studentId);

        mockMvc.perform(delete("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isNoContent());

        verify(leaveParcheService).salirDeParche(parcheId, studentId);
    }

    @Test
    @DisplayName("DELETE /parches/{id}/miembros retorna 404 cuando el parche no existe")
    void salirDeParche_parcheNoExiste_retorna404() throws Exception {
        doThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId))
                .when(leaveParcheService).salirDeParche(parcheId, studentId);

        mockMvc.perform(delete("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /parches/{id}/miembros retorna 400 cuando el student es el capitán")
    void salirDeParche_esCaptain_retorna400() throws Exception {
        doThrow(new IllegalArgumentException("Captain cannot leave without transferring leadership first"))
                .when(leaveParcheService).salirDeParche(parcheId, studentId);

        mockMvc.perform(delete("/api/v1/parches/{parcheId}/miembros", parcheId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isBadRequest());
    }
}
