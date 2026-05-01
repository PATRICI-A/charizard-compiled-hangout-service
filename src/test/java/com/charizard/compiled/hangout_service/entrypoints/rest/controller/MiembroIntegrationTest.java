package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class MiembroIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ParcheRepository parcheRepository;

    @Autowired
    private MemberRepository memberRepository;

    private UUID captainId;
    private UUID parcheId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        memberRepository.deleteAll();
        parcheRepository.deleteAll();

        captainId = UUID.randomUUID();

        ParcheEntity parche = ParcheEntity.builder()
                .name("Parche Base")
                .description("Parche para tests de integración")
                .place("Campus universitario")
                .type(ParcheType.PUBLIC)
                .maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        parcheId = parcheRepository.save(parche).getId();

        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId)
                .studentId(captainId)
                .memberRole(MemberRole.STUDENT)
                .build());
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        parcheRepository.deleteAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flujo: Unirse a parche público
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void unirse_flujoExitoso() throws Exception {
        UUID studentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isCreated());

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void unirse_cupoLleno_retorna409() throws Exception {
        ParcheEntity fullParche = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Lleno").description("Sin cupo").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(2)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build());

        memberRepository.save(MemberEntity.builder().parcheId(fullParche.getId())
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());
        memberRepository.save(MemberEntity.builder().parcheId(fullParche.getId())
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", fullParche.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isConflict());
    }

    @Test
    void unirse_masde5ParachesActivos_retorna409() throws Exception {
        UUID busyStudent = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            ParcheEntity other = parcheRepository.save(ParcheEntity.builder()
                    .name("Parche extra " + i).description("Desc").place("Lugar")
                    .type(ParcheType.PUBLIC).maximumQuota(10)
                    .dateRealization(LocalDateTime.now().plusDays(1))
                    .status(ParcheStatus.ACTIVE).captainId(UUID.randomUUID())
                    .build());
            memberRepository.save(MemberEntity.builder()
                    .parcheId(other.getId()).studentId(busyStudent)
                    .memberRole(MemberRole.STUDENT).build());
        }

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", busyStudent))
                .andExpect(status().isConflict());
    }

    @Test
    void unirse_estudianteYaEsMiembro_retorna409() throws Exception {
        UUID studentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isConflict());
    }

    @Test
    void unirse_parcheNoExiste_retorna404() throws Exception {
        mockMvc.perform(post("/api/v1/parches/{id}/miembros", UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void unirse_parcheArchivado_retorna400() throws Exception {
        ParcheEntity archived = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.FILED).captainId(captainId)
                .build());

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", archived.getId())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flujo: Salir del parche
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void salir_flujoExitoso() throws Exception {
        UUID studentId = UUID.randomUUID();
        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build());

        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isNoContent());

        assertFalse(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void salir_capitan_sinTransferencia_retorna400() throws Exception {
        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", captainId))
                .andExpect(status().isBadRequest());

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, captainId));
    }

    @Test
    void salir_noEsMiembro_retorna404() throws Exception {
        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void salir_parcheArchivado_retorna400() throws Exception {
        ParcheEntity archived = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.FILED).captainId(captainId)
                .build());

        UUID studentId = UUID.randomUUID();
        memberRepository.save(MemberEntity.builder()
                .parcheId(archived.getId()).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build());

        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", archived.getId())
                        .header("X-User-Id", studentId))
                .andExpect(status().isBadRequest());
    }
}
