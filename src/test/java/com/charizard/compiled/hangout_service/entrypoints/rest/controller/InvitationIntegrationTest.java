package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.RespondInvitationRequest;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitationRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class InvitationIntegrationTest {

    @Autowired WebApplicationContext webApplicationContext;
    @Autowired ParcheRepository parcheRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired InvitationRepository invitationRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private UUID parcheId;
    private UUID captainId;
    private UUID studentId;
    private UUID invitationId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.findAndRegisterModules();

        captainId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        parcheId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche test").description("Test").place("Test place")
                .type(ParcheType.PRIVATE).maximumQuota(10)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build()).getId();

        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId).studentId(captainId)
                .memberRole(MemberRole.CAPTAIN).build());

        invitationId = invitationRepository.save(InvitationEntity.builder()
                .parcheId(parcheId).captainId(captainId)
                .invitedStudentId(studentId).status(InvitationStatus.PENDING)
                .build()).getId();
    }

    @AfterEach
    void tearDown() {
        invitationRepository.deleteAll();
        memberRepository.deleteAll();
        parcheRepository.deleteAll();
    }

    @Test
    @DisplayName("PATCH /invitaciones/{id} ACCEPTED crea membership y retorna 200")
    void acceptInvitation_flujoExitoso_creaMembershipYRetorna200() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    @DisplayName("PATCH /invitaciones/{id} REJECTED no crea membership y retorna 200")
    void rejectInvitation_flujoExitoso_noCreaMembershipYRetorna200() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        assertEquals(1, memberRepository.countByParcheId(parcheId)); // solo el capitán
    }

    @Test
    @DisplayName("PATCH /invitaciones/{id} retorna 409 cuando la invitación ya fue respondida")
    void respondInvitation_yaRespondida_retorna409() throws Exception {
        InvitationEntity inv = invitationRepository.findById(invitationId).orElseThrow();
        inv.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(inv);

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH /invitaciones/{id} retorna 403 cuando no es el estudiante invitado")
    void respondInvitation_noEsElInvitado_retorna403() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", UUID.randomUUID().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /invitaciones/{id} ACCEPTED retorna 409 cuando el parche está lleno")
    void acceptInvitation_parcheCompleto_retorna409() throws Exception {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        UUID fullParcheId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche lleno").description("Sin cupo").place("Lugar")
                .type(ParcheType.PRIVATE).maximumQuota(2)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build()).getId();

        memberRepository.save(MemberEntity.builder().parcheId(fullParcheId)
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());
        memberRepository.save(MemberEntity.builder().parcheId(fullParcheId)
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

        UUID otroEstudiante = UUID.randomUUID();
        UUID invFullId = invitationRepository.save(InvitationEntity.builder()
                .parcheId(fullParcheId).captainId(captainId)
                .invitedStudentId(otroEstudiante).status(InvitationStatus.PENDING)
                .build()).getId();

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invFullId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", otroEstudiante.toString()))
                .andExpect(status().isConflict());
    }
}
