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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class InvitationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        captainId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        LocalDateTime dateRealization = LocalDateTime.now().plusDays(1);
        ParcheEntity parche = ParcheEntity.builder()
                .name("Parche test")
                .description("Test description")
                .place("Test place")
                .type(ParcheType.PRIVATE)
                .maximumQuota(10)
                .date(dateRealization.toLocalDate())
                .hour(dateRealization.toLocalTime())
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        parcheId = parcheRepository.save(parche).getId();

        MemberEntity captain = MemberEntity.builder()
                .parcheId(parcheId)
                .studentId(captainId)
                .memberRole(MemberRole.STUDENT)
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
    void reject_successFlow_doesNotCreateMember() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        assertEquals(1, memberRepository.countByParcheId(parcheId));
    }

    @Test
    void respond_alreadyResponded_returns409() throws Exception {
        InvitationEntity alreadyResponded = invitationRepository.findById(invitationId).get();
        alreadyResponded.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(alreadyResponded);

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.REJECTED);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", studentId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void respond_notTheInvitedStudent_returns403() throws Exception {
        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setAnswer(InvitationStatus.ACCEPTED);

        UUID otherStudent = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void accept_hangoutFull_returns409() throws Exception {
        LocalDateTime fullDate = LocalDateTime.now().plusDays(1);
        ParcheEntity fullParche = ParcheEntity.builder()
                .name("Full hangout")
                .description("No capacity")
                .place("Full place")
                .type(ParcheType.PRIVATE)
                .maximumQuota(2)
                .date(fullDate.toLocalDate())
                .hour(fullDate.toLocalTime())
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        UUID parcheFullId = parcheRepository.save(fullParche).getId();

        memberRepository.save(MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());
        memberRepository.save(MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

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

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invFullId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", otherStudent.toString()))
                .andExpect(status().isConflict());
    }
}
