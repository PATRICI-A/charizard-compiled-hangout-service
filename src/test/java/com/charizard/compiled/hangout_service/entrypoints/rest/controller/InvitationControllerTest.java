package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.SendInvitationRequest;
import com.charizard.compiled.hangout_service.application.dto.response.SendInvitationResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ErrorResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
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
class InvitationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InvitationInputPort invitationService;

    @InjectMocks
    private InvitationController invitationController;

    private ObjectMapper objectMapper;
    private UUID parcheId;
    private UUID captainId;
    private UUID student1Id;
    private UUID student2Id;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(invitationController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        parcheId = UUID.randomUUID();
        captainId = UUID.randomUUID();
        student1Id = UUID.randomUUID();
        student2Id = UUID.randomUUID();
    }

    @Test
    void enviar_flujoExitoso_retorna201() throws Exception {
        InvitationResponse inv1 = InvitationResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(student1Id)
                .status(InvitationStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        InvitationResponse inv2 = InvitationResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(student2Id)
                .status(InvitationStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        SendInvitationResponse response = SendInvitationResponse.builder()
                .createdInvitations(List.of(inv1, inv2))
                .errors(List.of())
                .build();

        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), any()))
                .thenReturn(response);

        SendInvitationRequest request = new SendInvitationRequest();
        request.setStudentIds(List.of(student1Id, student2Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitations", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdInvitations").isArray())
                .andExpect(jsonPath("$.createdInvitations.length()").value(2))
                .andExpect(jsonPath("$.createdInvitations[0].status").value("PENDING"));
    }

    @Test
    void enviar_estudianteYaEsMiembro_retorna409() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .studentId(student1Id)
                .error("El estudiante ya es miembro del parche")
                .status(409)
                .build();

        SendInvitationResponse response = SendInvitationResponse.builder()
                .createdInvitations(List.of())
                .errors(List.of(error))
                .build();

        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), any()))
                .thenReturn(response);

        SendInvitationRequest request = new SendInvitationRequest();
        request.setStudentIds(List.of(student1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitations", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status").value(409));
    }

    @Test
    void enviar_invitacionDuplicada_retorna409() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .studentId(student1Id)
                .error("Ya existe una invitación pendiente para este estudiante")
                .status(409)
                .build();

        SendInvitationResponse response = SendInvitationResponse.builder()
                .createdInvitations(List.of())
                .errors(List.of(error))
                .build();

        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), any()))
                .thenReturn(response);

        SendInvitationRequest request = new SendInvitationRequest();
        request.setStudentIds(List.of(student1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitations", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.errors[0].status").value(409));
    }

    @Test
    void enviar_noEsCapitan_retorna403() throws Exception {
        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), any()))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "El usuario no es capitán de este parche"
                ));

        SendInvitationRequest request = new SendInvitationRequest();
        request.setStudentIds(List.of(student1Id));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitations", parcheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isForbidden());
    }
}
