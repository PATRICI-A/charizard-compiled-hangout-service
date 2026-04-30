package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.application.dto.request.ResponderInvitacionRequest;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitacionEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitacionRepository;
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
class InvitacionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParcheRepository parcheRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InvitacionRepository invitacionRepository;

    private UUID parcheId;
    private UUID capitanId;
    private UUID estudianteId;
    private UUID invitacionId;

    @BeforeEach
    void setUp() {
        capitanId = UUID.randomUUID();
        estudianteId = UUID.randomUUID();

        ParcheEntity parche = ParcheEntity.builder()
                .name("Parche test")
                .description("Descripción test")
                .type(ParcheType.PRIVATE)
                .maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(capitanId)
                .build();
        parcheId = parcheRepository.save(parche).getId();

        MemberEntity capitan = MemberEntity.builder()
                .parcheId(parcheId)
                .studentId(capitanId)
                .memberRole(MemberRole.CAPTAIN)
                .build();
        memberRepository.save(capitan);

        InvitacionEntity invitacion = InvitacionEntity.builder()
                .parcheId(parcheId)
                .capitanId(capitanId)
                .estudianteInvitadoId(estudianteId)
                .estado(EstadoInvitacion.PENDIENTE)
                .build();
        invitacionId = invitacionRepository.save(invitacion).getId();
    }

    @AfterEach
    void tearDown() {
        invitacionRepository.deleteAll();
        memberRepository.deleteAll();
        parcheRepository.deleteAll();
    }

    @Test
    void aceptar_flujoExitoso_creaMembresia() throws Exception {
        ResponderInvitacionRequest request = new ResponderInvitacionRequest();
        request.setRespuesta(EstadoInvitacion.ACEPTADA);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> estudianteId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, estudianteId));
    }

    @Test
    void rechazar_flujoExitoso_noCreaMiembro() throws Exception {
        ResponderInvitacionRequest request = new ResponderInvitacionRequest();
        request.setRespuesta(EstadoInvitacion.RECHAZADA);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> estudianteId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));

        assertEquals(1, memberRepository.countByParcheId(parcheId)); // solo el capitán
    }

    @Test
    void responder_invitacionYaRespondida_retorna409() throws Exception {
        InvitacionEntity yaRespondida = invitacionRepository.findById(invitacionId).get();
        yaRespondida.setEstado(EstadoInvitacion.ACEPTADA);
        invitacionRepository.save(yaRespondida);

        ResponderInvitacionRequest request = new ResponderInvitacionRequest();
        request.setRespuesta(EstadoInvitacion.RECHAZADA);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> estudianteId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void responder_noEsElInvitado_retorna403() throws Exception {
        ResponderInvitacionRequest request = new ResponderInvitacionRequest();
        request.setRespuesta(EstadoInvitacion.ACEPTADA);

        UUID otroEstudiante = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invitacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> otroEstudiante.toString()))
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
                .captainId(capitanId)
                .build();
        UUID parcheFullId = parcheRepository.save(parcheConCupoMinimo).getId();

        MemberEntity m1 = MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.CAPTAIN).build();
        MemberEntity m2 = MemberEntity.builder().parcheId(parcheFullId).studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        UUID otroEstudiante = UUID.randomUUID();
        InvitacionEntity invFull = InvitacionEntity.builder()
                .parcheId(parcheFullId)
                .capitanId(capitanId)
                .estudianteInvitadoId(otroEstudiante)
                .estado(EstadoInvitacion.PENDIENTE)
                .build();
        UUID invFullId = invitacionRepository.save(invFull).getId();

        ResponderInvitacionRequest request = new ResponderInvitacionRequest();
        request.setRespuesta(EstadoInvitacion.ACEPTADA);

        mockMvc.perform(patch("/api/v1/invitaciones/{id}", invFullId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> otroEstudiante.toString()))
                .andExpect(status().isConflict());
    }
}
