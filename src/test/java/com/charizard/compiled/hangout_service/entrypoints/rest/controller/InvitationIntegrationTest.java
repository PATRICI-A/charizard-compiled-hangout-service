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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InvitationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParcheRepository parcheRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    private UUID parcheId;
    private UUID captainId;
    private UUID studentId;
    private UUID invitationId;

    @BeforeEach
    void setUp() {
        captainId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        ParcheEntity parche = ParcheEntity.builder()
                .name("Parche test")
                .description("Descripción test")
                .type(ParcheType.PRIVATE)
                .maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        parcheId = parcheRepository.save(parche).getId();

        MemberEntity captain = MemberEntity.builder()
                .parcheId(parcheId)
                .studentId(captainId)
                .memberRole(MemberRole.CAPTAIN)
                .build();
        memberRepository.save(captain);

        InvitationEntity invitation = InvitationEntity.builder()
                .parcheId(parcheId)
                .captainId(captainId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();
        invitationId = invitationRepository.save(invitation).getId();
    }

    @AfterEach
    void tearDown() {
        invitationRepository.deleteAll();
        memberRepository.deleteAll();
        parcheRepository.deleteAll();
    }

    @Test
    void aceptar_flujoExitoso_creaMembresia() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        mockMvc.perform(patch("/api/v1/invitations/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void rechazar_flujoExitoso_noCreaMiembro() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitations/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        assertEquals(1, memberRepository.countByParcheId(parcheId)); // only the captain
    }

    @Test
    void responder_invitacionYaRespondida_retorna409() throws Exception {
        InvitationEntity alreadyResponded = invitationRepository.findById(invitationId).get();
        alreadyResponded.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(alreadyResponded);

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitations/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void responder_noEsElInvitado_retorna403() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        UUID otherStudent = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/invitations/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void aceptar_cupoLleno_retorna409() throws Exception {
        ParcheEntity parcheConCupoMinimo = ParcheEntity.builder()
                .name("Parche lleno")
                .description("Sin cupo")
                .type(ParcheType.PRIVATE)
                .maximumQuota(2)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        UUID parcheFullId = parcheRepository.save(parcheConCupoMinimo).getId();

        MemberEntity m1 = MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.CAPTAIN).build();
        MemberEntity m2 = MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        UUID otherStudent = UUID.randomUUID();
        InvitationEntity invFull = InvitationEntity.builder()
                .parcheId(parcheFullId)
                .captainId(captainId)
                .invitedStudentId(otherStudent)
                .status(InvitationStatus.PENDING)
                .build();
        UUID invFullId = invitationRepository.save(invFull).getId();

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        mockMvc.perform(patch("/api/v1/invitations/{id}", invFullId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isConflict());
    }
}
