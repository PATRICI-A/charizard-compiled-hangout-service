package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitationInputPort;
import com.charizard.compiled.hangout_service.domain.ports.in.RespondInvitationInputPort;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
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

    @Mock
    private RespondInvitationInputPort respondInvitationService;

    @InjectMocks
    private InvitationController invitationController;

    private ObjectMapper objectMapper;
    private UUID parcheId;
    private UUID captainId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(invitationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        parcheId = UUID.randomUUID();
        captainId = UUID.randomUUID();
        studentId = UUID.randomUUID();
    }

    @Test
    void sendInvitation_successFlow_returns201() throws Exception {
        InvitationResponse response = InvitationResponse.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.invitedStudentId").value(studentId.toString()));
    }

    @Test
    void sendInvitation_notCaptain_returns403() throws Exception {
        when(invitationService.sendInvitation(eq(parcheId), eq(captainId), eq(studentId)))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "User is not the captain of this hangout"
                ));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendInvitation_studentAlreadyMember_returns409() throws Exception {
        when(invitationService.sendInvitation(any(), any(), any()))
                .thenThrow(new com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException(
                        "Student is already a member of this hangout"
                ));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void sendInvitation_duplicateInvitation_returns409() throws Exception {
        when(invitationService.sendInvitation(any(), any(), any()))
                .thenThrow(new com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException(
                        "A pending invitation already exists for this student"
                ));

        mockMvc.perform(post("/api/v1/parches/{parcheId}/invitaciones/{studentId}", parcheId, studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", captainId.toString()))
                .andExpect(status().isConflict());
    }
}
