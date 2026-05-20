package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.RespondInvitationInputPort;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

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

    @Mock InvitationInputPort invitationService;
    @Mock RespondInvitationInputPort respondInvitationService;

    @InjectMocks InvitationController invitationController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private UUID parcheId;
    private UUID inviterId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        inviterId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(inviterId.toString(), null, List.of()));
        SecurityContextHolder.setContext(context);

        mockMvc = MockMvcBuilders.standaloneSetup(invitationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /parches/{id}/invitaciones/{studentId} retorna 201 con invitación PENDING")
    void sendInvitation_valido_retorna201ConInvitacionPending() throws Exception {
        InvitationResponse response = InvitationResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        when(invitationService.sendInvitation(eq(parcheId), eq(inviterId), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.invitedStudentId").value(studentId.toString()));
    }

    @Test
    @DisplayName("POST /parches/{id}/invitaciones/{studentId} retorna 404 cuando el parche no existe")
    void sendInvitation_parcheNoExiste_retorna404() throws Exception {
        when(invitationService.sendInvitation(eq(parcheId), eq(inviterId), eq(studentId)))
                .thenThrow(new ParcheNotFoundException("Parche not found with id: " + parcheId));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches/{id}/invitaciones/{studentId} retorna 403 cuando el solicitante no es miembro")
    void sendInvitation_noEsMiembro_retorna403() throws Exception {
        when(invitationService.sendInvitation(eq(parcheId), eq(inviterId), eq(studentId)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this hangout"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /parches/{id}/invitaciones/{studentId} retorna 409 cuando el estudiante ya es miembro")
    void sendInvitation_estudianteYaMiembro_retorna409() throws Exception {
        when(invitationService.sendInvitation(any(), any(), any()))
                .thenThrow(new StudentAlreadyMemberException("Student is already a member of this hangout"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/invitaciones/{studentId} retorna 409 cuando ya hay una invitación PENDING")
    void sendInvitation_invitacionDuplicada_retorna409() throws Exception {
        when(invitationService.sendInvitation(any(), any(), any()))
                .thenThrow(new DuplicateInvitationException("A pending invitation already exists for this student"));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
