package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.GetParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.UpdateParcheInputPort;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParcheControllerTest {

    @Mock CreateParcheInputPort createParcheUseCase;
    @Mock GetParcheInputPort getParcheUseCase;
    @Mock UpdateParcheInputPort updateParcheUseCase;
    @Mock CloseParcheInputPort closeParcheUseCase;

    @InjectMocks ParcheController controller;

    MockMvc mockMvc;

    private UUID parcheId;
    private ParcheResponse parcheResponse;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        parcheId = UUID.randomUUID();
        parcheResponse = ParcheResponse.builder()
                .id(parcheId)
                .name("Parche de fútbol")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .actualMembers(3)
                .captainId(UUID.randomUUID())
                .date(LocalDate.of(2026, 6, 15))
                .hour(LocalTime.of(15, 30))
                .build();
    }

    @Test
    @DisplayName("GET /parches sin filtros retorna 200 con lista")
    void getParches_sinFiltros_retorna200() throws Exception {
        when(getParcheUseCase.getParches(null, null, null, null, null))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[0].actualMembers").value(3));
    }

    @Test
    @DisplayName("GET /parches?nombre=futbol filtra por nombre")
    void getParches_conNombre_retorna200() throws Exception {
        when(getParcheUseCase.getParches(null, null, "futbol", null, null))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("nombre", "futbol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("GET /parches?fecha=2026-06-15 filtra por fecha")
    void getParches_conFecha_retorna200() throws Exception {
        when(getParcheUseCase.getParches(null, null, null, LocalDate.of(2026, 6, 15), null))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("fecha", "2026-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(parcheId.toString()));
    }

    @Test
    @DisplayName("GET /parches?cupoDisponible=true filtra parches con espacio")
    void getParches_conCupoDisponible_retorna200() throws Exception {
        when(getParcheUseCase.getParches(null, null, null, null, true))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("cupoDisponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maximumQuota").value(10));
    }

    @Test
    @DisplayName("GET /parches?tipo=PUBLIC&estado=ACTIVE filtra por tipo y estado")
    void getParches_conTipoYEstado_retorna200() throws Exception {
        when(getParcheUseCase.getParches(ParcheType.PUBLIC, ParcheStatus.ACTIVE, null, null, null))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches")
                        .param("tipo", "PUBLIC")
                        .param("estado", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /parches con todos los filtros retorna 200")
    void getParches_conTodosLosFiltros_retorna200() throws Exception {
        when(getParcheUseCase.getParches(
                ParcheType.PUBLIC, ParcheStatus.ACTIVE, "futbol",
                LocalDate.of(2026, 6, 15), true))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches")
                        .param("tipo", "PUBLIC")
                        .param("estado", "ACTIVE")
                        .param("nombre", "futbol")
                        .param("fecha", "2026-06-15")
                        .param("cupoDisponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("GET /parches sin resultados retorna 200 con lista vacía")
    void getParches_sinResultados_retorna200ListaVacia() throws Exception {
        when(getParcheUseCase.getParches(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/parches").param("nombre", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /parches/{id} retorna 200 cuando el parche existe")
    void getParcheById_retorna200_cuandoExiste() throws Exception {
        when(getParcheUseCase.getParcheById(parcheId)).thenReturn(parcheResponse);

        mockMvc.perform(get("/api/v1/parches/{id}", parcheId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(parcheId.toString()))
                .andExpect(jsonPath("$.name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("GET /parches/{id} retorna 404 cuando el parche no existe")
    void getParcheById_retorna404_cuandoNoExiste() throws Exception {
        UUID inexistente = UUID.randomUUID();
        when(getParcheUseCase.getParcheById(inexistente))
                .thenThrow(new ParcheNotFoundException("Parche not found with id: " + inexistente));

        mockMvc.perform(get("/api/v1/parches/{id}", inexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches retorna 201 con parche creado")
    void createParche_retorna201() throws Exception {
        UUID captainId = UUID.randomUUID();
        CreateParcheRequest req = CreateParcheRequest.builder()
                .name("Parche nuevo")
                .place("Parque")
                .category(ParcheCategory.MUSIC)
                .date(LocalDate.of(2027, 1, 1))
                .hour(LocalTime.of(14, 0))
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        when(createParcheUseCase.createParche(any(), eq(captainId))).thenReturn(parcheResponse);

        mockMvc.perform(post("/api/v1/parches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString())
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("POST /parches retorna 409 cuando captain alcanzó límite")
    void createParche_limiteAlcanzado_retorna409() throws Exception {
        UUID captainId = UUID.randomUUID();
        CreateParcheRequest req = CreateParcheRequest.builder()
                .name("Parche nuevo")
                .place("Parque")
                .category(ParcheCategory.MUSIC)
                .date(LocalDate.of(2027, 1, 1))
                .hour(LocalTime.of(14, 0))
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        when(createParcheUseCase.createParche(any(), eq(captainId)))
                .thenThrow(new MaxHangoutsReachedException());

        mockMvc.perform(post("/api/v1/parches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString())
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH /parches/{id} retorna 200 con parche actualizado")
    void updateParche_retorna200() throws Exception {
        UUID solicitanteId = UUID.randomUUID();
        UpdateParcheRequest req = UpdateParcheRequest.builder().name("Nombre Actualizado").build();

        when(updateParcheUseCase.updateParche(eq(parcheId), any(), eq(solicitanteId)))
                .thenReturn(parcheResponse);

        mockMvc.perform(patch("/api/v1/parches/{id}", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", solicitanteId.toString())
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /parches/{id} retorna 403 cuando no es captain")
    void updateParche_noEsCaptain_retorna403() throws Exception {
        UUID solicitanteId = UUID.randomUUID();
        UpdateParcheRequest req = UpdateParcheRequest.builder().name("Nombre").build();

        when(updateParcheUseCase.updateParche(eq(parcheId), any(), eq(solicitanteId)))
                .thenThrow(new AccessDeniedException("Only the captain can edit this parche"));

        mockMvc.perform(patch("/api/v1/parches/{id}", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", solicitanteId.toString())
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /parches/{id} retorna 204 cuando captain cierra parche")
    void deleteParche_retorna204() throws Exception {
        UUID captainId = UUID.randomUUID();
        doNothing().when(closeParcheUseCase).closeParche(parcheId, captainId);

        mockMvc.perform(delete("/api/v1/parches/{id}", parcheId)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isNoContent());

        verify(closeParcheUseCase).closeParche(parcheId, captainId);
    }

    @Test
    @DisplayName("DELETE /parches/{id} retorna 404 cuando parche no existe")
    void deleteParche_noExiste_retorna404() throws Exception {
        UUID captainId = UUID.randomUUID();
        doThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId))
                .when(closeParcheUseCase).closeParche(parcheId, captainId);

        mockMvc.perform(delete("/api/v1/parches/{id}", parcheId)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /parches/{id} retorna 403 cuando no es captain")
    void deleteParche_noEsCaptain_retorna403() throws Exception {
        UUID captainId = UUID.randomUUID();
        doThrow(new AccessDeniedException("Only the captain can delete this parche"))
                .when(closeParcheUseCase).closeParche(parcheId, captainId);

        mockMvc.perform(delete("/api/v1/parches/{id}", parcheId)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isForbidden());
    }
}
