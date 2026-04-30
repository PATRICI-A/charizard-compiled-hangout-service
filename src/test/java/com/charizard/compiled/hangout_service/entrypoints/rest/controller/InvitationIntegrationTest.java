package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InvitationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                .description("Test description")
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
    void accept_successFlow_createsMembership() throws Exception {
        mockMvc.perform(post("/api/v1/invitaciones/{id}/aceptar", invitationId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void reject_successFlow_doesNotCreateMember() throws Exception {
        mockMvc.perform(post("/api/v1/invitaciones/{id}/rechazar", invitationId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        assertEquals(1, memberRepository.countByParcheId(parcheId)); // only the captain
    }

    @Test
    void respond_invitationAlreadyResponded_returns409() throws Exception {
        InvitationEntity alreadyResponded = invitationRepository.findById(invitationId).get();
        alreadyResponded.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(alreadyResponded);

        mockMvc.perform(post("/api/v1/invitaciones/{id}/rechazar", invitationId)
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void respond_notTheInvitedStudent_returns403() throws Exception {
        UUID otherStudent = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/invitaciones/{id}/aceptar", invitationId)
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void accept_hangoutFull_returns409() throws Exception {
        ParcheEntity fullParche = ParcheEntity.builder()
                .name("Full hangout")
                .description("No capacity")
                .type(ParcheType.PRIVATE)
                .maximumQuota(2)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        UUID parcheFullId = parcheRepository.save(fullParche).getId();

        memberRepository.save(MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.CAPTAIN).build());
        memberRepository.save(MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

        UUID otherStudent = UUID.randomUUID();
        InvitationEntity invFull = InvitationEntity.builder()
                .parcheId(parcheFullId)
                .captainId(captainId)
                .invitedStudentId(otherStudent)
                .status(InvitationStatus.PENDING)
                .build();
        UUID invFullId = invitationRepository.save(invFull).getId();

        mockMvc.perform(post("/api/v1/invitaciones/{id}/aceptar", invFullId)
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isConflict());
    }
}
