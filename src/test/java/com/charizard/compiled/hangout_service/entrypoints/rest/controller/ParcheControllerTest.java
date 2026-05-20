package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheDetailResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.in.CloseParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.CreateParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.GetParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.UpdateParcheInputPort;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    ObjectMapper objectMapper;

    private UUID parcheId;
    private ParcheResponse parcheResponse;
    private ParcheDetailResponse parcheDetailResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        parcheId = UUID.randomUUID();
        parcheResponse = ParcheResponse.builder()
                .id(parcheId)
                .name("Parche de fútbol")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .actualMembers(3)
                .ownerId(UUID.randomUUID())
                .date(LocalDate.of(2026, 6, 15))
                .hour(LocalTime.of(15, 30))
                .build();

        parcheDetailResponse = ParcheDetailResponse.builder()
                .id(parcheId)
                .name("Parche de fútbol")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .actualMembers(3)
                .ownerId(UUID.randomUUID())
                .members(List.of())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthentication(UUID userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of()));
        SecurityContextHolder.setContext(context);
    }

    // ─── GET /parches ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /parches sin filtros retorna 200 con lista de parches")
    void getParches_sinFiltros_retorna200ConLista() throws Exception {
        when(getParcheUseCase.getParches(null, null, null, null))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[0].actualMembers").value(3));
    }

    @Test
    @DisplayName("GET /parches?nombre=futbol filtra por nombre")
    void getParches_conNombre_filtraPorNombre() throws Exception {
        when(getParcheUseCase.getParches(eq("futbol"), any(), any(), any()))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("nombre", "futbol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("GET /parches?fecha=2026-06-15 filtra por fecha")
    void getParches_conFecha_filtraPorFecha() throws Exception {
        when(getParcheUseCase.getParches(any(), eq(LocalDate.of(2026, 6, 15)), any(), any()))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("fecha", "2026-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(parcheId.toString()));
    }

    @Test
    @DisplayName("GET /parches?categoria=Deportes filtra por categoria")
    void getParches_conCategoria_filtraPorCategoria() throws Exception {
        when(getParcheUseCase.getParches(any(), any(), eq("Deportes"), any()))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("categoria", "Deportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(parcheId.toString()));
    }

    @Test
    @DisplayName("GET /parches?cupoDisponible=true filtra parches con espacio disponible")
    void getParches_conCupoDisponibleTrue_filtraParchesConEspacio() throws Exception {
        when(getParcheUseCase.getParches(any(), any(), any(), eq(true)))
                .thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches").param("cupoDisponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maximumQuota").value(10));
    }

    @Test
    @DisplayName("GET /parches sin resultados retorna 200 con lista vacía")
    void getParches_sinResultados_retorna200ListaVacia() throws Exception {
        when(getParcheUseCase.getParches(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/parches").param("nombre", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─── GET /parches/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /parches/{id} retorna 200 con el detalle del parche cuando existe")
    void getParcheById_existe_retorna200ConDetalle() throws Exception {
        when(getParcheUseCase.getParcheById(parcheId)).thenReturn(parcheDetailResponse);

        mockMvc.perform(get("/api/v1/parches/{id}", parcheId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(parcheId.toString()))
                .andExpect(jsonPath("$.name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("GET /parches/{id} retorna 404 cuando el parche no existe")
    void getParcheById_noExiste_retorna404() throws Exception {
        UUID inexistente = UUID.randomUUID();
        when(getParcheUseCase.getParcheById(inexistente))
                .thenThrow(new ParcheNotFoundException("Parche not found with id: " + inexistente));

        mockMvc.perform(get("/api/v1/parches/{id}", inexistente))
                .andExpect(status().isNotFound());
    }

    // ─── GET /parches/me ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /parches/me retorna 200 con los parches del usuario autenticado")
    void getMyParches_usuarioAutenticado_retorna200() throws Exception {
        UUID userId = UUID.randomUUID();
        setAuthentication(userId);

        when(getParcheUseCase.getMyParches(userId)).thenReturn(List.of(parcheResponse));

        mockMvc.perform(get("/api/v1/parches/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Parche de fútbol"));
    }

    // ─── POST /parches ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /parches retorna 201 con el parche creado")
    void createParche_valido_retorna201ConParche() throws Exception {
        UUID ownerId = UUID.randomUUID();
        setAuthentication(ownerId);

        CreateParcheRequest req = CreateParcheRequest.builder()
                .name("Parche nuevo")
                .place("Parque")
                .category("MUSIC")
                .date(LocalDate.of(2027, 1, 1))
                .hour(LocalTime.of(14, 0))
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        when(createParcheUseCase.createParche(any(), eq(ownerId))).thenReturn(parcheResponse);

        mockMvc.perform(post("/api/v1/parches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Parche de fútbol"));
    }

    @Test
    @DisplayName("POST /parches retorna 409 cuando el owner alcanzó el límite de 5 parches")
    void createParche_limiteAlcanzado_retorna409() throws Exception {
        UUID ownerId = UUID.randomUUID();
        setAuthentication(ownerId);

        CreateParcheRequest req = CreateParcheRequest.builder()
                .name("Parche nuevo").place("Parque").category("MUSIC")
                .date(LocalDate.of(2027, 1, 1)).hour(LocalTime.of(14, 0))
                .maximumQuota(10).type(ParcheType.PUBLIC).build();

        when(createParcheUseCase.createParche(any(), eq(ownerId)))
                .thenThrow(new MaxHangoutsReachedException());

        mockMvc.perform(post("/api/v1/parches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    // ─── PATCH /parches/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /parches/{id} retorna 200 con parche actualizado")
    void updateParche_valido_retorna200() throws Exception {
        UUID solicitanteId = UUID.randomUUID();
        setAuthentication(solicitanteId);

        UpdateParcheRequest req = UpdateParcheRequest.builder().description("Nueva descripción").build();

        when(updateParcheUseCase.updateParche(eq(parcheId), any(), eq(solicitanteId)))
                .thenReturn(parcheResponse);

        mockMvc.perform(patch("/api/v1/parches/{id}", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /parches/{id} retorna 403 cuando el solicitante no es el owner")
    void updateParche_noEsOwner_retorna403() throws Exception {
        UUID solicitanteId = UUID.randomUUID();
        setAuthentication(solicitanteId);

        UpdateParcheRequest req = UpdateParcheRequest.builder().description("Desc").build();

        when(updateParcheUseCase.updateParche(eq(parcheId), any(), eq(solicitanteId)))
                .thenThrow(new AccessDeniedException("Only the owner can edit this parche"));

        mockMvc.perform(patch("/api/v1/parches/{id}", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ─── DELETE /parches/{id} — admin only ───────────────────────────────

    @Test
    @DisplayName("DELETE /parches/{id} retorna 204 cuando el admin archiva el parche")
    void deleteParche_admin_retorna204() throws Exception {
        doNothing().when(closeParcheUseCase).closeParche(parcheId);

        mockMvc.perform(delete("/api/v1/parches/{id}", parcheId))
                .andExpect(status().isNoContent());

        verify(closeParcheUseCase).closeParche(parcheId);
    }

    @Test
    @DisplayName("DELETE /parches/{id} retorna 404 cuando el parche no existe")
    void deleteParche_noExiste_retorna404() throws Exception {
        doThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId))
                .when(closeParcheUseCase).closeParche(parcheId);

        mockMvc.perform(delete("/api/v1/parches/{id}", parcheId))
                .andExpect(status().isNotFound());
    }
}
