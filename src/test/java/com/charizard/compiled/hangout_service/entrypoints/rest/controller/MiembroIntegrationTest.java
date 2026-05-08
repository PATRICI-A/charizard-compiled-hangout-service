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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class MiembroIntegrationTest {

    @Autowired WebApplicationContext webApplicationContext;
    @Autowired ParcheRepository parcheRepository;
    @Autowired MemberRepository memberRepository;

    MockMvc mockMvc;

    private UUID captainId;
    private UUID parcheId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        memberRepository.deleteAll();
        parcheRepository.deleteAll();

        captainId = UUID.randomUUID();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        parcheId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Base").description("Parche para tests de integración")
                .place("Campus universitario").type(ParcheType.PUBLIC).maximumQuota(10)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build()).getId();

        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId).studentId(captainId)
                .memberRole(MemberRole.CAPTAIN).build());
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        parcheRepository.deleteAll();
    }

    // ─── Unirse a parche ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /parches/{id}/miembros flujo exitoso crea membership")
    void unirse_flujoExitoso_creaMembership() throws Exception {
        UUID studentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isCreated());

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el cupo está lleno")
    void unirse_cupoLleno_retorna409() throws Exception {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        UUID fullParcheId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Lleno").description("Sin cupo").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(2)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build()).getId();

        memberRepository.save(MemberEntity.builder().parcheId(fullParcheId)
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());
        memberRepository.save(MemberEntity.builder().parcheId(fullParcheId)
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", fullParcheId)
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student tiene 5 parches activos")
    void unirse_estudianteConLimiteAlcanzado_retorna409() throws Exception {
        UUID busyStudent = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        for (int i = 0; i < 5; i++) {
            UUID otherParcheId = parcheRepository.save(ParcheEntity.builder()
                    .name("Parche extra " + i).description("Desc").place("Lugar")
                    .type(ParcheType.PUBLIC).maximumQuota(10)
                    .date(date.toLocalDate()).hour(date.toLocalTime())
                    .status(ParcheStatus.ACTIVE).captainId(UUID.randomUUID())
                    .build()).getId();
            memberRepository.save(MemberEntity.builder()
                    .parcheId(otherParcheId).studentId(busyStudent)
                    .memberRole(MemberRole.STUDENT).build());
        }

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", busyStudent))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 409 cuando el student ya es miembro")
    void unirse_estudianteYaMiembro_retorna409() throws Exception {
        UUID studentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 404 cuando el parche no existe")
    void unirse_parcheNoExiste_retorna404() throws Exception {
        mockMvc.perform(post("/api/v1/parches/{id}/miembros", UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /parches/{id}/miembros retorna 400 cuando el parche está archivado")
    void unirse_parcheArchivado_retorna400() throws Exception {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        UUID archivedId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.FILED).captainId(captainId)
                .build()).getId();

        mockMvc.perform(post("/api/v1/parches/{id}/miembros", archivedId)
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    // ─── Salir del parche ─────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /parches/{id}/miembros flujo exitoso elimina membership")
    void salir_flujoExitoso_eliminaMembership() throws Exception {
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
    @DisplayName("DELETE /parches/{id}/miembros retorna 400 cuando el capitán intenta salir")
    void salir_captain_retorna400() throws Exception {
        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", captainId))
                .andExpect(status().isBadRequest());

        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, captainId));
    }

    @Test
    @DisplayName("DELETE /parches/{id}/miembros retorna 404 cuando el student no es miembro")
    void salir_noEsMiembro_retorna404() throws Exception {
        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", parcheId)
                        .header("X-User-Id", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /parches/{id}/miembros retorna 400 cuando el parche está archivado")
    void salir_parcheArchivado_retorna400() throws Exception {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        UUID archivedId = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .date(date.toLocalDate()).hour(date.toLocalTime())
                .status(ParcheStatus.FILED).captainId(captainId)
                .build()).getId();

        UUID studentId = UUID.randomUUID();
        memberRepository.save(MemberEntity.builder()
                .parcheId(archivedId).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build());

        mockMvc.perform(delete("/api/v1/parches/{id}/miembros", archivedId)
                        .header("X-User-Id", studentId))
                .andExpect(status().isBadRequest());
    }
}
