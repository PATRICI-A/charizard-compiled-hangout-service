package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.EnviarInvitacionRequest;
import com.charizard.compiled.hangout_service.application.dto.response.EnviarInvitacionResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ErrorResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitacionResponse;
import com.charizard.compiled.hangout_service.application.service.InvitacionService;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvitacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InvitacionService invitacionService;

    @InjectMocks
    private InvitacionController invitacionController;

    private ObjectMapper objectMapper;
    private UUID parcheId;
    private UUID capitanId;
    private UUID estudiante1Id;
    private UUID estudiante2Id;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(invitacionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        parcheId = UUID.randomUUID();
        capitanId = UUID.randomUUID();
        estudiante1Id = UUID.randomUUID();
        estudiante2Id = UUID.randomUUID();
    }

    @Test
    void enviar_flujoExitoso_retorna201() throws Exception {
        InvitacionResponse inv1 = InvitacionResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .estudianteInvitadoId(estudiante1Id)
                .estado(EstadoInvitacion.PENDIENTE)
                .fechaEnvio(LocalDateTime.now())
                .build();

        InvitacionResponse inv2 = InvitacionResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .estudianteInvitadoId(estudiante2Id)
                .estado(EstadoInvitacion.PENDIENTE)
                .fechaEnvio(LocalDateTime.now())
                .build();

        EnviarInvitacionResponse response = EnviarInvitacionResponse.builder()
                .invitacionesCreadas(List.of(inv1, inv2))
                .errores(List.of())
                .build();

        when(invitacionService.enviarInvitacion(eq(parcheId), eq(capitanId), any()))
                .thenReturn(response);

        EnviarInvitacionRequest request = new EnviarInvitacionRequest();
        request.setEstudiantesIds(List.of(estudiante1Id, estudiante2Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> capitanId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invitacionesCreadas").isArray())
                .andExpect(jsonPath("$.invitacionesCreadas.length()").value(2))
                .andExpect(jsonPath("$.invitacionesCreadas[0].estado").value("PENDIENTE"));
    }

    @Test
    void enviar_estudianteYaEsMiembro_retorna409() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .estudianteId(estudiante1Id)
                .error("El estudiante ya es miembro del parche")
                .status(409)
                .build();

        EnviarInvitacionResponse response = EnviarInvitacionResponse.builder()
                .invitacionesCreadas(List.of())
                .errores(List.of(error))
                .build();

        when(invitacionService.enviarInvitacion(eq(parcheId), eq(capitanId), any()))
                .thenReturn(response);

        EnviarInvitacionRequest request = new EnviarInvitacionRequest();
        request.setEstudiantesIds(List.of(estudiante1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> capitanId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errores").isArray())
                .andExpect(jsonPath("$.errores[0].status").value(409));
    }

    @Test
    void enviar_invitacionDuplicada_retorna409() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .estudianteId(estudiante1Id)
                .error("Ya existe una invitación pendiente para este estudiante")
                .status(409)
                .build();

        EnviarInvitacionResponse response = EnviarInvitacionResponse.builder()
                .invitacionesCreadas(List.of())
                .errores(List.of(error))
                .build();

        when(invitacionService.enviarInvitacion(eq(parcheId), eq(capitanId), any()))
                .thenReturn(response);

        EnviarInvitacionRequest request = new EnviarInvitacionRequest();
        request.setEstudiantesIds(List.of(estudiante1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> capitanId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errores[0].status").value(409));
    }

    @Test
    void enviar_noEsCapitan_retorna403() throws Exception {
        when(invitacionService.enviarInvitacion(eq(parcheId), eq(capitanId), any()))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "El usuario no es capitán de este parche"
                ));

        EnviarInvitacionRequest request = new EnviarInvitacionRequest();
        request.setEstudiantesIds(List.of(estudiante1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> capitanId.toString()))
                .andExpect(status().isForbidden());
    }
}
